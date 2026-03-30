package com.blue.newsapp.ui.FavoriteNews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.blue.newsapp.ViewModel.FavoriteNewsViewModel
import com.blue.newsapp.data.remote.model.response.FavoriteNewsResponse
import com.blue.newsapp.databinding.FragmentFavoriteNewsBinding
import com.blue.newsapp.ui.UiState
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FavoriteNewsFragment: Fragment() {

    private var _binding : FragmentFavoriteNewsBinding? = null
    private val binding get() = _binding!!

    private val viewModel : FavoriteNewsViewModel by viewModels()

    private val navController by lazy{
        findNavController()
    }

    private lateinit var favoriteNewsAdapter: FavoriteNewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoriteNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favoriteNewsAdapter = FavoriteNewsAdapter{ favoriteNews ->
            val action = FavoriteNewsFragmentDirections.actionFavoriteNewsFragmentToNewsDetailFragment(
                title = favoriteNews.title,
                imageUrl = favoriteNews.imageUrl,
                sourceName = favoriteNews.sourceName,
                publishedAt = favoriteNews.publishedAt,
                description = favoriteNews.description,
                url = favoriteNews.url
            )
            navController.navigate(action)
        }
        binding.rvFavoriteNews.adapter = favoriteNewsAdapter
        binding.rvFavoriteNews.layoutManager = LinearLayoutManager(requireContext())

        observeUiState()

        viewModel.loadFavorites()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadFavorites()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeUiState(){
        viewModel.uiState.observe(viewLifecycleOwner){ uiState ->
            when(uiState){
                is UiState.Loading ->{
                    binding.tvEmpty.visibility = View.GONE
                    binding.rvFavoriteNews.visibility = View.GONE
                }

                is UiState.Empty -> {
                    binding.tvEmpty.text = uiState.message
                    binding.tvEmpty.visibility = View.VISIBLE
                    binding.rvFavoriteNews.visibility = View.GONE
                }

                is UiState.Success -> {
                    favoriteNewsAdapter.submitList(uiState.data)
                    binding.tvEmpty.visibility = View.GONE
                    binding.rvFavoriteNews.visibility = View.VISIBLE
                }

                is UiState.Error -> {
                    binding.tvEmpty.text = uiState.message
                    binding.tvEmpty.visibility = View.VISIBLE
                    binding.rvFavoriteNews.visibility = View.GONE
                }
            }
        }
    }
}