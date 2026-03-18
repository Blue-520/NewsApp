package com.blue.newsapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.blue.newsapp.R
import com.blue.newsapp.ViewModel.NewsViewModel
import com.blue.newsapp.databinding.FragmentHomeBinding

class HomeFragment: Fragment() {
    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val newsAdapter by lazy {
        NewsAdapter{ article ->
            val action = HomeFragmentDirections.actionHomeFragmentToNewsDetailFragment(title = article.title, imageUrl = article.urlToImage ?: "",
                sourceName = article.source.name, publishedAt = article.publishedAt ?: "", description = article.description ?: "", url = article.url)

            findNavController().navigate(action)
        }
    }

    private val viewModel by viewModels<NewsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.newsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.newsRecyclerView.adapter = newsAdapter

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.swipeRefresh.isRefreshing = isLoading
        }

        viewModel.newList.observe(viewLifecycleOwner){ articles ->
            newsAdapter.submitList(articles)

            binding.swipeRefresh.isRefreshing = false
        }

        viewModel.errorMessage.observe(viewLifecycleOwner){ message ->
            if (!message.isNullOrEmpty()){
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }

            binding.swipeRefresh.isRefreshing = false
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadNews()
        }

        viewModel.loadNews()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}