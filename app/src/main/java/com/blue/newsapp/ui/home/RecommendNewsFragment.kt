package com.blue.newsapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.blue.newsapp.ViewModel.RecommendNewsViewModel
import com.blue.newsapp.databinding.FragmentRecommendNewsBinding

class RecommendNewsFragment : Fragment(){

    private var _binding : FragmentRecommendNewsBinding? = null
    private val binding get() = _binding!!

    private val viewModel : RecommendNewsViewModel by viewModels()

    private val newsAdapter = LocalNewsAdapter{ news ->
            val action = HomeFragmentDirections.actionHomeFragmentToNewsDetailFragment(
                title = news.title, imageUrl = news.urlToImage ?: "", sourceName = news.name,
                publishedAt = news.publishedAt ?: "", description = news.description ?: "", url = news.url, category = news.category ?: "general")

        increaseIntersetSource(news.category ?: "general")
        findNavController().navigate(action)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecommendNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.newsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.newsRecyclerView.adapter = newsAdapter

        viewModel.newsList.observe(viewLifecycleOwner){ List ->
            newsAdapter.submitList(List)
        }

        viewModel.loading.observe(viewLifecycleOwner){ isLoading ->
            binding.swipeRefresh.isRefreshing = isLoading
        }

        viewModel.errorMessage.observe(viewLifecycleOwner){ msg ->
            if (!msg.isNullOrEmpty()){
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            }
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadRecommendNews()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun increaseIntersetSource(category: String){
        viewModel.increaseScore(category)
    }
}