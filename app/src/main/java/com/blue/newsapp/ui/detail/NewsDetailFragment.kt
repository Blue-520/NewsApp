package com.blue.newsapp.ui.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.blue.newsapp.R
import com.blue.newsapp.ViewModel.NewsDetailViewModel
import com.blue.newsapp.databinding.FragmentNewsDetailBinding
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch

class NewsDetailFragment: Fragment() {
    private var _binding : FragmentNewsDetailBinding? = null
    private val binding get() = _binding!!

    private val navController by lazy {
        findNavController()
    }

    private lateinit var commentAdapter: NewsCommentAdapter

    private val viewModel : NewsDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewsDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args : NewsDetailFragmentArgs by navArgs()

        // 从 arguments 中取出首页传过来的数据
        val title = args.title
        val imageUrl = args.imageUrl
        val sourceName = args.sourceName
        val publishedAt = args.publishedAt
        val description = args.description
        val url = args.url
        val category = args.category


        // 初始化评论列表
        commentAdapter = NewsCommentAdapter()
        binding.rvComments.layoutManager = LinearLayoutManager(requireContext())
        binding.rvComments.adapter = commentAdapter

        // 告诉 ViewModel 当前是哪条新闻
        viewModel.setNewsUrl(url)


        // 设置标题
        binding.detailTitle.text = title

        // 设置来源
        binding.detailSource.text = "来源：$sourceName"

        // 设置发布时间
        binding.detailPublishedAt.text = "发布时间：$publishedAt"

        // 设置简介
        binding.detailDescription.text =
            if (description.isNotEmpty()) description else "暂无简介"

        // Toolbar 返回
        binding.detailToolbar.setNavigationOnClickListener {
            navController.navigateUp()
        }

        // 加载新闻图片
        if (imageUrl.isEmpty()) {
            Log.d("aaa", "图片是空的")
            binding.detailImage.setImageResource(R.drawable.img_error)
        } else {
            Glide.with(requireContext())
                .load(imageUrl)
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_error)
                .centerCrop()
                .into(binding.detailImage)
        }

        // 观察收藏状态
        viewModel.favoriteNews.observe(viewLifecycleOwner){ favoriteNews ->
            if (favoriteNews == null){
                binding.btnFavorite.setImageResource(R.drawable.not_star)
            }else{
                binding.btnFavorite.setImageResource(R.drawable.already_star)
            }
        }

        // 点击收藏 / 取消收藏
        binding.btnFavorite.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {

                val isLogin = viewModel.isLogin()

                if (!isLogin){
                    Toast.makeText(requireContext(), "请先登录再收藏", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val currentFavorite = viewModel.favoriteNews.value

                if (currentFavorite == null){
                    viewModel.addFavorite( title = title, imageUrl = imageUrl, sourceName = sourceName, publishedAt = publishedAt, description = description, url = url)
                    Toast.makeText(requireContext(), "收藏成功", Toast.LENGTH_SHORT).show()

                    //加分
                    viewModel.increaseScore(category)
                }else{
                    viewModel.removeFavorite(url)
                    Toast.makeText(requireContext(), "已取消收藏", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // 观察评论列表
        viewModel.commentList.observe(viewLifecycleOwner){ comments ->
            commentAdapter.submitList(comments)
        }

        //发布评论
        binding.btnSendComment.setOnClickListener {
            val content = binding.etComment.text?.toString()?.trim().orEmpty()

            if (content.isEmpty()){
                Toast.makeText(requireContext(), "评论不能为空", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.addComment(url, content)
            binding.etComment.setText("")
            Toast.makeText(requireContext(), "评论发布成功", Toast.LENGTH_SHORT).show()
        }

        // 点击“查看原文”
        binding.btnOpenNews.setOnClickListener {
            if (url.isNotEmpty()) {
                val action = NewsDetailFragmentDirections.actionNewsDetailFragmentToNewsWebFragment(url = url)
                navController.navigate(action)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
