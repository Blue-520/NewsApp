package com.blue.newsapp.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.blue.newsapp.ViewModel.MineViewModelFactory
import com.blue.newsapp.ViewModel.RegisterViewModel
import com.blue.newsapp.data.loacl.database.AppDatabase
import com.blue.newsapp.databinding.FragmentRegisterBinding
import com.blue.newsapp.repository.UserReposity

class RegisterFragment: Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: RegisterViewModel

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

        val userDao = AppDatabase.getDatabase(requireContext()).userDao()
        val userReposity = UserReposity(userDao)

        val factory = MineViewModelFactory(userReposity)
        viewModel = ViewModelProvider(this, factory)[RegisterViewModel::class.java]

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
        viewModel.registerMessage.observe(viewLifecycleOwner){ message ->
             if (message.isNotEmpty()){
                 Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
             }
        }

        viewModel.registerSuccess.observe(viewLifecycleOwner){ success ->
            if (success == true){
                findNavController().navigateUp()
            }
        }
    }
}