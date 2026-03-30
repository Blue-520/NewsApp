package com.blue.newsapp.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blue.newsapp.data.loacl.database.SessionManager
import com.blue.newsapp.data.remote.model.request.AddCommentRequset
import com.blue.newsapp.repository.CommentReposity
import com.blue.newsapp.repository.FavoriteRepository
import com.blue.newsapp.repository.NewLocalRepository
import com.blue.newsapp.ui.NewsDetailUiModel
import com.blue.newsapp.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class NewsDetailViewModel @Inject constructor (
    private val favoriteRepository: FavoriteRepository,
    private val commentReposity: CommentReposity,
    private val sessionManager: SessionManager,
    private val localRepository: NewLocalRepository
) : ViewModel() {

    // 页面主状态：
    // 用来告诉 Fragment 当前详情页是“加载中 / 成功 / 失败”
    // 如果是成功，还会携带 NewsDetailUiModel 这份页面数据
    private val _uiState = MutableLiveData<UiState<NewsDetailUiModel>>()
    val uiState: LiveData<UiState<NewsDetailUiModel>> = _uiState

    // 一次性提示事件：
    // 比如“请先登录”“收藏成功”“评论失败”这种 Toast 文案
    private val _toastEvent = MutableLiveData<String>()
    val toastEvent: LiveData<String> = _toastEvent

    // 当前详情页对应的新闻 url
    // 这里先缓存下来，表示“当前正在看的新闻是谁”
    private var currentNewsUrl: String = ""

    /**
     * 进入详情页时加载数据
     *
     * 这个方法主要做 3 件事：
     * 1. 校验 url
     * 2. 查询当前新闻是否已收藏（已登录才查）
     * 3. 查询当前新闻评论列表
     *
     * 最后把这些数据组装成 NewsDetailUiModel，交给界面显示
     */
    fun loadNewsDetail(url: String) {

        // 如果 url 为空，说明这条新闻不合法，页面直接进入错误状态
        if (url.isBlank()) {
            _uiState.value = UiState.Error("新闻地址不能为空")
            return
        }

        // 保存当前新闻 url
        currentNewsUrl = url

        // 先把页面设为“加载中”
        // Fragment 观察到后，可以显示进度条/骨架屏等
        _uiState.value = UiState.Loading

        // 开启协程，异步执行后续逻辑
        viewModelScope.launch {
            try {
                // 判断当前用户是否已登录
                val isLogin = sessionManager.isLogin()

                // 查询收藏状态：
                // - 如果已登录，就去仓库查这条新闻是否已收藏
                // - 如果没登录，直接认为“未收藏”
                val favoriteResult = if (isLogin) {
                    favoriteRepository.isFavorite(url)
                } else {
                    Result.success(false)
                }

                // 查询评论列表
                val commentResult = commentReposity.checkComment(url)

                // 取出收藏状态结果：
                // - 成功：拿到 Boolean
                // - 失败：页面进入错误状态，并结束协程
                val isFavorite = favoriteResult.getOrElse {
                    _uiState.value = UiState.Error(it.message ?: "收藏状态加载失败")
                    return@launch
                }

                // 取出评论列表结果：
                // - 成功：拿到评论列表
                // - 失败：页面进入错误状态，并结束协程
                val comments = commentResult.getOrElse {
                    _uiState.value = UiState.Error(it.message ?: "评论加载失败")
                    return@launch
                }

                // 前面都成功后，页面进入 Success 状态
                // 同时组装页面所需数据
                _uiState.value = UiState.Success(
                    NewsDetailUiModel(
                        newsUrl = url,                  // 当前新闻地址
                        isFavorite = isFavorite,       // 当前是否收藏
                        comments = comments,           // 当前评论列表
                        favoriteLoading = false,       // 收藏按钮当前是否正在处理
                        commentSubmitting = false      // 评论当前是否正在提交
                    )
                )
            } catch (e: Exception) {
                // 出现异常，页面进入错误状态
                e.printStackTrace()
                _uiState.value = UiState.Error(e.message ?: "加载失败")
            }
        }
    }

    /**
     * 收藏 / 取消收藏
     *
     * 逻辑：
     * 1. 当前页面必须已经成功加载
     * 2. 必须登录
     * 3. 防止重复点击
     * 4. 如果原来已收藏 -> 执行取消收藏
     * 5. 如果原来未收藏 -> 执行新增收藏
     * 6. 成功后更新页面状态
     */
    fun toggleFavorite(
        title: String,
        imageUrl: String,
        sourceName: String,
        publishedAt: String,
        description: String,
        url: String,
        category: String
    ) {
        // 先拿当前页面状态
        val currentState = _uiState.value

        // 只有页面是 Success 时，才允许做收藏操作
        // 因为只有 Success 才有 currentState.data 可用
        if (currentState !is UiState.Success) return

        viewModelScope.launch {
            // 先判断是否登录
            val isLogin = sessionManager.isLogin()

            // 没登录不给收藏，只弹提示
            if (!isLogin) {
                _toastEvent.value = "请先登录再收藏"
                return@launch
            }

            // 取出当前页面已有数据
            val oldData = currentState.data

            // 如果当前已经在执行收藏操作，就不再重复执行
            // 防止用户短时间连续点击，导致重复请求
            if (oldData.favoriteLoading) return@launch

            // 先更新状态：把 favoriteLoading 改成 true
            // 表示“收藏操作正在处理中”
            _uiState.value = UiState.Success(
                oldData.copy(favoriteLoading = true)
            )

            try {
                // 根据当前是否已收藏，决定执行新增还是取消
                val result = if (oldData.isFavorite) {
                    // 已收藏 -> 本次点击是取消收藏
                    favoriteRepository.removeFavorite(url)
                } else {
                    // 未收藏 -> 本次点击是新增收藏
                    favoriteRepository.addFavorite(
                        title = title,
                        imageUrl = imageUrl,
                        sourceName = sourceName,
                        publishedAt = publishedAt,
                        description = description,
                        url = url
                    )
                }

                result.onSuccess {
                    // 收藏状态切换成功后，新的收藏状态就是旧状态取反
                    val newFavorite = !oldData.isFavorite

                    // 更新页面状态：
                    // 1. 改变 isFavorite
                    // 2. 把 favoriteLoading 设回 false
                    _uiState.value = UiState.Success(
                        oldData.copy(
                            isFavorite = newFavorite,
                            favoriteLoading = false
                        )
                    )

                    // 给用户提示
                    _toastEvent.value = if (newFavorite) "收藏成功" else "已取消收藏"

                    // 只有“新增收藏成功”时，才增加兴趣分
                    if (newFavorite) {
                        increaseScore(category)
                    }
                }.onFailure {
                    // 操作失败：
                    // 页面本身不需要进入 Error
                    // 因为详情页仍然是正常显示的，只是本次收藏操作失败
                    _uiState.value = UiState.Success(
                        oldData.copy(favoriteLoading = false)
                    )
                    _toastEvent.value = it.message ?: "操作失败"
                }
            } catch (e: Exception) {
                // 出现异常时，也要把 loading 状态恢复
                e.printStackTrace()
                _uiState.value = UiState.Success(
                    oldData.copy(favoriteLoading = false)
                )
                _toastEvent.value = e.message ?: "操作失败"
            }
        }
    }

    /**
     * 发布评论
     *
     * 逻辑：
     * 1. 页面必须是 Success
     * 2. 评论内容不能为空
     * 3. 用户必须已登录
     * 4. 防止重复提交
     * 5. 提交成功后，把新评论插到当前评论列表最前面
     */
    fun addComment(content: String) {
        // 先拿当前页面状态
        val currentState = _uiState.value

        // 只有页面成功加载后，才允许发表评论
        if (currentState !is UiState.Success) return

        // 评论内容为空，直接提示并结束
        if (content.isBlank()) {
            _toastEvent.value = "评论不能为空"
            return
        }

        viewModelScope.launch {
            // 判断登录状态
            val isLogin = sessionManager.isLogin()

            // 未登录不能评论
            if (!isLogin) {
                _toastEvent.value = "请先登录后评论"
                return@launch
            }

            // 取出当前页面已有数据
            val oldData = currentState.data

            // 如果当前已经在提交评论，就不重复提交
            if (oldData.commentSubmitting) return@launch

            // 先更新状态：评论提交中
            _uiState.value = UiState.Success(
                oldData.copy(commentSubmitting = true)
            )

            try {
                // 构造“发表评论请求体”
                // 需要告诉后端/仓库：评论的是哪条新闻、评论内容是什么
                val addResult = commentReposity.addComment(
                    AddCommentRequset(
                        newsUrl = oldData.newsUrl,
                        content = content
                    )
                )

                addResult.onSuccess { newComment ->
                    // 发表评论成功后：
                    // 1. 把新评论插到旧评论列表最前面
                    // 2. commentSubmitting 设回 false
                    _uiState.value = UiState.Success(
                        oldData.copy(
                            comments = listOf(newComment) + oldData.comments,
                            commentSubmitting = false
                        )
                    )

                    _toastEvent.value = "评论发布成功"
                }.onFailure {
                    // 发表评论失败：
                    // 只需要恢复 commentSubmitting 状态即可
                    _uiState.value = UiState.Success(
                        oldData.copy(commentSubmitting = false)
                    )
                    _toastEvent.value = it.message ?: "评论失败"
                }
            } catch (e: Exception) {
                // 异常兜底
                e.printStackTrace()
                _uiState.value = UiState.Success(
                    oldData.copy(commentSubmitting = false)
                )
                _toastEvent.value = e.message ?: "评论失败"
            }
        }
    }

    /**
     * 刷新评论
     *
     * 逻辑：
     * 1. 页面必须已经是 Success
     * 2. 根据当前新闻 url 重新查询评论
     * 3. 成功后只更新 comments 字段
     */
    fun refreshComments() {
        // 先取当前页面状态
        val currentState = _uiState.value

        // 只有 Success 才有 newsUrl 和已有页面数据
        if (currentState !is UiState.Success) return

        viewModelScope.launch {
            try {
                // 重新拉取当前新闻的评论列表
                val result = commentReposity.checkComment(currentState.data.newsUrl)

                result.onSuccess { comments ->
                    // 只更新评论列表，其他页面字段保持不变
                    _uiState.value = UiState.Success(
                        currentState.data.copy(comments = comments)
                    )
                }.onFailure {
                    // 刷新评论失败，只给提示，不切整页 Error
                    _toastEvent.value = it.message ?: "评论刷新失败"
                }
            } catch (e: Exception) {
                // 异常兜底
                e.printStackTrace()
                _toastEvent.value = e.message ?: "评论刷新失败"
            }
        }
    }


    fun increaseScore(category: String) {
        viewModelScope.launch {
            val userId = sessionManager.getUserId()

            if (userId == -1L) return@launch

            localRepository.increaseUserInterestScore(userId, category, 2)
        }
    }


    suspend fun isLogin(): Boolean {
        return sessionManager.isLogin()
    }
}