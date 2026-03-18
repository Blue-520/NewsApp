package com.blue.newsapp.ui.mine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.blue.newsapp.R
import com.blue.newsapp.ViewModel.MineViewModel
import com.blue.newsapp.ViewModel.MineViewModelFactory
import com.blue.newsapp.data.loacl.database.UserPreferences
import com.blue.newsapp.databinding.FragmentMineBinding

class MineFragment: Fragment() {
    private var _binding : FragmentMineBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MineViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. 创建 UserPreferences
        val userPreference = UserPreferences(requireContext())

        // 2. 创建 ViewModel
        val factory = MineViewModelFactory(userPreference = userPreference)
        viewModel = ViewModelProvider(this, factory)[MineViewModel::class.java]

        // 去登录
        binding.btnGoLogin.setOnClickListener {
            findNavController().navigate(R.id.action_mineFragment_to_loginFragment)
        }

        // 去注册
        binding.btnGoRegister.setOnClickListener {
            findNavController().navigate(R.id.action_mineFragment_to_registerFragment)
        }

        //退出登录
        binding.btnLogout.setOnClickListener {
            viewModel.logout()
            Toast.makeText(requireContext(), "已退出登录", Toast.LENGTH_SHORT).show()
        }

        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeViewModel(){
        viewModel.isLogin.observe(viewLifecycleOwner){ isLogin ->
            updateLoginState(isLogin, viewModel.username.value ?: "")
        }

        viewModel.username.observe(viewLifecycleOwner){ username ->
            updateLoginState(viewModel.isLogin.value ?: false, username)
        }
    }

    /**
     * 根据登录状态刷新界面
     */
    private fun updateLoginState(isLogin: Boolean, username: String){
        if (isLogin){
            binding.layoutNotLogin.visibility = View.GONE
            binding.layoutLoggedIn.visibility = View.VISIBLE
            binding.tvWelcome.text = "欢迎你：${username}"
            binding.tvUsername.text = "用户名：${username}"
        }else{
            binding.layoutNotLogin.visibility = View.VISIBLE
            binding.layoutLoggedIn.visibility = View.GONE
        }
    }
}