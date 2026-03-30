package com.blue.newsapp.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.blue.newsapp.R
import com.blue.newsapp.ViewModel.RegisterViewModel
import com.blue.newsapp.databinding.FragmentRegisterBinding
import com.blue.newsapp.ui.UiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment: Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 点“去登录”
        binding.tvGoLogin.setOnClickListener {
            findNavController().navigateUp()
        }

        // 注册逻辑
        binding.btnRegister.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()

           viewModel.register(username, password, confirmPassword)
        }

        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeViewModel(){
        viewModel.registerState.observe(viewLifecycleOwner){ state ->
            when(state){
                is UiState.Loading -> {

                }

                is UiState.Empty -> {

                }

                is UiState.Success -> {
                    Toast.makeText(requireContext(), "注册成功", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                }

                is UiState.Error -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}