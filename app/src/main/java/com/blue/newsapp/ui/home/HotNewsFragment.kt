package com.blue.newsapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.blue.newsapp.ViewModel.HotNewsViewModel
import com.blue.newsapp.databinding.FragmentHotNewsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HotNewsFragment: Fragment() {
    private var _binding : FragmentHotNewsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HotNewsViewModel by viewModels()
    private val newsAdapter = NewsAdapter{ news ->
        val action = HomeFragmentDirections.actionHomeFragmentToNewsDetailFragment(
            title = news.title, imageUrl = news.urlToImage ?: "", sourceName = news.source.name,
            publishedAt = news.publishedAt ?: "", description = news.description ?: "", url = news.url, category = news.category ?: "general")
        findNavController().navigate(action)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHotNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.newsRecyclerView.adapter = newsAdapter
        binding.newsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        //收集分页数据
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.hotNewsFlow.collectLatest { pagingData ->
                newsAdapter.submitData(pagingData)
            }
        }

        //观察分页加载状态
        //LoadState有3个行为：refre刷新， append加载下一页， prepend加载上一页   3个状态：Loading加载中， NotLonding结束/空闲， Error失败
        viewLifecycleOwner.lifecycleScope.launch {
            newsAdapter.loadStateFlow.collectLatest { loadStates ->

                //下拉刷新圈圈：跟refresh绑定
                binding.swipeRefresh.isRefreshing = loadStates.refresh is LoadState.Loading

                //首屏加载失败
                val refreshState = loadStates.refresh
                if (refreshState is LoadState.Error) {
                    Toast.makeText(requireContext(), "新闻加载失败：${refreshState.error.message ?: "请检查网络"}", Toast.LENGTH_SHORT).show()
                }

                //首屏加载成功但没有数据
                val isListEmpty = loadStates.refresh is LoadState.NotLoading && newsAdapter.itemCount == 0          //首次加载已经结束 loading适配器里一条数据都没有
                if (isListEmpty) {
                    Toast.makeText(requireContext(), "暂无实时新闻", Toast.LENGTH_SHORT).show()
                }

                //追加加载失败（翻到后面一页失败）
                val appendState = loadStates.append
                if (appendState is LoadState.Error) {
                    Toast.makeText(requireContext(), "加载更多失败：${appendState.error.message ?: "请稍后重试"}", Toast.LENGTH_SHORT).show()
                }
            }

            //下拉刷新
            binding.swipeRefresh.setOnRefreshListener {
                newsAdapter.refresh()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}