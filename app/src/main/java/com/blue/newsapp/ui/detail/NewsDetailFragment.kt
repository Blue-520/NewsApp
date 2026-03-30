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
import com.blue.newsapp.ui.UiState
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
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

        // 设置新闻
        binding.detailTitle.text = title
        binding.detailSource.text = "来源：$sourceName"
        binding.detailPublishedAt.text = "发布时间：$publishedAt"
        binding.detailDescription.text = if (description.isNotEmpty()) description else "暂无简介"
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

        observeViewModel()

        viewModel.loadNewsDetail(url)

        // Toolbar 返回
        binding.detailToolbar.setNavigationOnClickListener {
            navController.navigateUp()
        }

        // 点击收藏 / 取消收藏
        binding.btnFavorite.setOnClickListener {
           viewModel.toggleFavorite(title, imageUrl, sourceName, publishedAt, description, url, category)
        }

        //发布评论
        binding.btnSendComment.setOnClickListener {
            val content = binding.etComment.text.toString().trim()
            viewModel.addComment(content)
        }

        // 点击“查看原文”
        binding.btnOpenNews.setOnClickListener {
            if (url.isNotEmpty()) {
                val action = NewsDetailFragmentDirections.actionNewsDetailFragmentToNewsWebFragment(url = url)
                navController.navigate(action)
            }
        }
    }


    private fun observeViewModel(){
        viewModel.uiState.observe(viewLifecycleOwner){ state ->
            when(state){
                UiState.Loading -> {
                    binding.btnFavorite.isEnabled = false
                    binding.btnSendComment.isEnabled = false
                }

                is UiState.Empty -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }

                is UiState.Success -> {
                    val data = state.data

                    binding.btnFavorite.isEnabled = !data.favoriteLoading
                    binding.btnSendComment.isEnabled = !data.commentSubmitting

                    if (data.isFavorite){
                        binding.btnFavorite.setImageResource(R.drawable.already_star)
                    }else{
                        binding.btnFavorite.setImageResource(R.drawable.not_star)
                    }

                    commentAdapter.submitList(state.data.comments)
                }

                is UiState.Error -> {
                    binding.btnFavorite.isEnabled = true
                    binding.btnSendComment.isEnabled = true
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.toastEvent.observe(viewLifecycleOwner){ message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

            if (message == "评论发布成功"){
                binding.etComment.setText("")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
