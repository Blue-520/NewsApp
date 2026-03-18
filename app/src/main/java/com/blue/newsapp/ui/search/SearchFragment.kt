package com.blue.newsapp.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.blue.newsapp.R
import com.blue.newsapp.ViewModel.SearchViewModel
import com.blue.newsapp.databinding.FragmentSearchBinding
import com.blue.newsapp.ui.home.NewsAdapter

class SearchFragment: Fragment() {
    private var _binding : FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var newsAdapter: NewsAdapter

    private val viewModel : SearchViewModel by viewModels()

    // 记录当前搜索词，方便下拉刷新时重复搜索
    private var currentKeyword: String = ""

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

        binding.searchView

        viewModel.searchResult.observe(viewLifecycleOwner){

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /*private fun setRecyclrView(){
        newsAdapter = NewsAdapter(){ article ->
            val action = SearchFragmentDirections.actionSearchFragmentToNewsDetailFragment(article)
        }
    }*/

    /**
     * 初始化搜索框
     */
    private fun setupSearchView(){
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{

            // 点击键盘搜索按钮时触发
            override fun onQueryTextSubmit(query: String?): Boolean {
                val keyword = query?.trim().orEmpty()
                if (keyword.isNotEmpty()){
                    currentKeyword = keyword
                    viewModel.search(keyword)

                    // 收起焦点，界面更自然一点
                    binding.searchView.clearFocus()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // 这里先不做“边输入边搜索”
                // 后面如果你想优化，我再带你做防抖搜索
                return true
            }
        })
    }

    private fun observeViewModel(){
        viewModel.searchResult.observe(viewLifecycleOwner){ articles ->

        }
    }
}