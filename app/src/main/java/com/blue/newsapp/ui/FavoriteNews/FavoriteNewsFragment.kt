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
import com.blue.newsapp.databinding.FragmentFavoriteNewsBinding


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

        favoriteNewsAdapter = FavoriteNewsAdapter{ favoriteNewsEntity ->
            val action = FavoriteNewsFragmentDirections.actionFavoriteNewsFragmentToNewsDetailFragment(
                title = favoriteNewsEntity.title,
                imageUrl = favoriteNewsEntity.imageUrl,
                sourceName = favoriteNewsEntity.sourceName,
                publishedAt = favoriteNewsEntity.publishedAt,
                description = favoriteNewsEntity.description,
                url = favoriteNewsEntity.url
            )
            navController.navigate(action)
        }
        binding.rvFavoriteNews.adapter = favoriteNewsAdapter
        binding.rvFavoriteNews.layoutManager = LinearLayoutManager(requireContext())


        viewModel.favoriteNewsList.observe(viewLifecycleOwner){ favoriteNewsList ->
            favoriteNewsAdapter.submitList(favoriteNewsList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}