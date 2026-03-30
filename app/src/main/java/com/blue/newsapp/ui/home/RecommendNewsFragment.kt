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
import com.blue.newsapp.ui.UiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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


        viewModel.uiState.observe(viewLifecycleOwner){ state ->
            when(state){
                UiState.Loading -> {
                    binding.swipeRefresh.isRefreshing = true
                }

                is UiState.Empty -> {
                    binding.swipeRefresh.isRefreshing = false
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }

                is UiState.Success -> {
                    binding.swipeRefresh.isRefreshing = false
                    newsAdapter.submitList(state.data)
                }

                is UiState.Error -> {
                    binding.swipeRefresh.isRefreshing = false
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadNews()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun increaseIntersetSource(category: String){
        viewModel.increaseScore(category)
    }
}