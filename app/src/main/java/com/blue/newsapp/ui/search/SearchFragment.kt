package com.blue.newsapp.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.blue.newsapp.ViewModel.SearchViewModel
import com.blue.newsapp.databinding.FragmentSearchBinding
import com.blue.newsapp.ui.home.HomeFragmentDirections
import com.blue.newsapp.ui.home.NewsAdapter


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

        binding.etSearch.setOnEditorActionListener { _, _, _ ->
            doSearch()
            true
        }
    }

    private fun doSearch() {
        val query = binding.etSearch.text.toString().trim()
        viewModel.searchNews(query)
    }

    private fun observeViewModel() {
        viewModel.searchResultList.observe(viewLifecycleOwner) { list ->
            newsAdapter.submitList(list)

            binding.tvState.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            if (list.isEmpty()) {
                binding.tvState.text = "没有搜索到相关新闻"
            }
        }

        viewModel.loading.observe(viewLifecycleOwner){ isLoading ->
            binding.loading.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(viewLifecycleOwner){ msg ->
            if (!msg.isNullOrEmpty()){
                binding.tvState.visibility = View.VISIBLE
                binding.tvState.text = msg
            }else{
                binding.tvState.visibility = View.GONE
            }
        }
    }
}