package com.blue.newsapp.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.blue.newsapp.R
import com.blue.newsapp.ViewModel.LoginViewModel
import com.blue.newsapp.ViewModel.MineViewModelFactory
import com.blue.newsapp.data.loacl.database.AppDatabase
import com.blue.newsapp.data.loacl.database.UserPreferences
import com.blue.newsapp.databinding.FragmentLoginBinding
import com.blue.newsapp.repository.UserReposity

class LoginFragment: Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel : LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. 创建 Repository 和 UserPreferences
        val userDao = AppDatabase.getDatabase(requireContext()).userDao()
        val userRepository  = UserReposity(userDao)
        val userPreference = UserPreferences(requireContext())

        // 2. 创建 ViewModel
        val factory = MineViewModelFactory(userRepository, userPreference)
        viewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]

        // 点“去注册”
        binding.tvGoRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        // 点击登录
        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            viewModel.login(username, password)
        }

        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeViewModel(){

        // 监听提示消息
        viewModel.loginMessage.observe(viewLifecycleOwner){ message ->
            if (message.isNotEmpty()){
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }

        // 监听登录结果
        viewModel.loginSuccess.observe(viewLifecycleOwner){ success ->
            if (success == true){
                findNavController().navigateUp()
            }
        }
    }
}