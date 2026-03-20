package com.blue.newsapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.blue.newsapp.ViewModel.HotNewsViewModel
import com.blue.newsapp.databinding.FragmentHotNewsBinding

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

        viewModel.loading.observe(viewLifecycleOwner){ isLoading ->
            binding.swipeRefresh.isRefreshing = isLoading
        }

        viewModel.errorMessage.observe(viewLifecycleOwner){ msg ->
            if (!msg.isNullOrEmpty()){
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.newList.observe(viewLifecycleOwner){ list ->
            newsAdapter.submitList(list)
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadNews()
        }

        viewModel.loadNewsIfNeeded()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}