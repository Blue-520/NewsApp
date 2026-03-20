package com.blue.newsapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.blue.newsapp.R
import com.blue.newsapp.databinding.FragmentHomeBinding
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment: Fragment() {
    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var pagerAdapter: HomePagerAdapter

    private val adapter = HomeFragmentDirections

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

        initViewPager()
        initSearchBar()

        binding.searchBar.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initViewPager(){
        pagerAdapter = HomePagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager){ tab, position ->
            when(position){
                0 -> tab.text = "推荐"
                1 -> tab.text = "时事热点"
            }
        }.attach()
    }

    private fun initSearchBar(){
        binding.searchBar.setOnClickListener {
            //findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
            Toast.makeText(requireContext(), "大家好", Toast.LENGTH_SHORT).show()
        }
    }
}