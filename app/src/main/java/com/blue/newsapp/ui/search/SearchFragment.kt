package com.blue.newsapp.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.blue.newsapp.ViewModel.SearchViewModel
import com.blue.newsapp.databinding.FragmentSearchBinding
import com.blue.newsapp.ui.home.NewsAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment: Fragment() {
    private var _binding : FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel : SearchViewModel by viewModels()

    private val newsAdapter by lazy {
        NewsAdapter{ article ->
            val action = SearchFragmentDirections.actionSearchFragmentToNewsDetailFragment(title = article.title,
                imageUrl = article.urlToImage ?: "", sourceName = article.source.name,
                publishedAt = article.publishedAt ?: "", description = article.description ?: "",
                url = article.url, category = article.category ?: "general")

            findNavController().navigate(action)
            //viewModel.saveNews(article, article.category ?: "general")
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        initClick()
        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initRecyclerView() {
        binding.rvSearchNews.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = newsAdapter
        }
    }

    private fun initClick() {
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.tvSearch.setOnClickListener {
            doSearch()
        }

        //键盘的搜索也可以直接用
        binding.etSearch.setOnEditorActionListener { _, _, _ ->
            doSearch()
            true
        }
    }

    private fun doSearch() {
        val query = binding.etSearch.text.toString().trim()
        viewModel.search(query)
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.searchFlow.collectLatest { pagingData ->
                    newsAdapter.submitData(pagingData)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            newsAdapter.loadStateFlow.collectLatest { loadStates ->

                val refreshState = loadStates.refresh
                if (refreshState is LoadState.Error) {
                    Toast.makeText(requireContext(), "新闻加载失败：${refreshState.error.message ?: "请检查网络"}", Toast.LENGTH_SHORT).show()
                }

                val isListEmpty = loadStates.refresh is LoadState.NotLoading && newsAdapter.itemCount == 0
                if (isListEmpty){
                    Toast.makeText(requireContext(), "暂无实时新闻", Toast.LENGTH_SHORT).show()
                }

                val appendState = loadStates.append
                if (appendState is LoadState.Error) {
                    Toast.makeText(requireContext(), "加载更多失败：${appendState.error.message ?: "请稍后重试"}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}