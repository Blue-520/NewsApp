package com.blue.newsapp.ui.mine

import android.app.AlertDialog
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.blue.newsapp.R
import com.blue.newsapp.ViewModel.MineViewModel
import com.blue.newsapp.ViewModel.MineViewModelFactory
import com.blue.newsapp.data.loacl.database.AppDatabase
import com.blue.newsapp.data.loacl.database.UserPreferences
import com.blue.newsapp.data.loacl.entity.UserEntity
import com.blue.newsapp.databinding.FragmentMineBinding
import com.blue.newsapp.repository.UserReposity
import java.io.File
import java.io.FileOutputStream

class MineFragment: Fragment() {
    private var _binding : FragmentMineBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MineViewModel

    private enum class ImageTarget {
        AVATAR,
        BACKGROUND
    }

    private var pendingImageTarget: ImageTarget? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            runCatching {
                requireContext().contentResolver.takePersistableUriPermission(
                    uri,
                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            saveImageUri(uri.toString())
        }
    }

    private val takePhotoLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            val path = saveBitmapToFile(bitmap)
            if (!path.isNullOrEmpty()) {
                saveImageUri(path)
            }
        }
    }

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

        val userDao = AppDatabase.getDatabase(requireContext()).userDao()
        val userRepository = UserReposity(userDao)

        // 2. 创建 ViewModel
        val factory = MineViewModelFactory(userRepository = userRepository, userPreference = userPreference)
        viewModel = ViewModelProvider(this, factory)[MineViewModel::class.java]

        // 去登录
        binding.btnGoLogin.setOnClickListener {
            findNavController().navigate(R.id.action_mineFragment_to_loginFragment)
        }

        // 去注册
        binding.btnGoRegister.setOnClickListener {
            findNavController().navigate(R.id.action_mineFragment_to_registerFragment)
        }

        binding.layoutAvatar.setOnClickListener {
            showImagePickerDialog(ImageTarget.AVATAR)
        }

        binding.ivProfileBackground.setOnClickListener {
            showImagePickerDialog(ImageTarget.BACKGROUND)
        }

        binding.tvWelcome.setOnClickListener {
            showTextEditDialog(title = "修改用户名", hint = "输入新的用户名", initialValue = viewModel.userProfile.value?.username.orEmpty()) { value ->
                viewModel.updateUsername(value)
            }
        }

        binding.tvSignature.setOnClickListener {
            showTextEditDialog(title = "修改个性签名", hint = "写一句介绍自己", initialValue = viewModel.userProfile.value?.signature.orEmpty()) { value ->
                viewModel.updateSignature(value)
            }
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
            updateLoginState(isLogin, viewModel.userProfile.value)
        }

        viewModel.userProfile.observe(viewLifecycleOwner){ profile ->
            updateLoginState(viewModel.isLogin.value ?: false, profile)
        }

        viewModel.message.observe(viewLifecycleOwner){ msg ->
            if (!msg.isNullOrBlank()) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                viewModel.clearMessage()
            }
        }
    }

    /**
     * 根据登录状态刷新界面
     */
    private fun updateLoginState(isLogin: Boolean, profile: UserEntity?){
        if (isLogin){
            binding.layoutNotLogin.visibility = View.GONE
            binding.layoutLoggedIn.visibility = View.VISIBLE
            binding.btnLogout.visibility = View.VISIBLE
            val username = profile?.username.orEmpty()
            val signature = profile?.signature?.takeIf { it.isNotBlank() }
                ?: "这个人很懒，但会认真读每一条新闻"
            binding.tvWelcome.text = username
            binding.tvUsername.visibility = View.GONE
            binding.tvSignature.text = signature
            binding.tvAvatarLetter.text = username.firstOrNull()?.uppercase() ?: "N"
            bindAvatar(profile?.avatar.orEmpty())
            bindBackground(profile?.backgroundImage.orEmpty())
        }else{
            binding.layoutNotLogin.visibility = View.VISIBLE
            binding.layoutLoggedIn.visibility = View.GONE
            binding.btnLogout.visibility = View.GONE
            binding.tvUsername.visibility = View.GONE
            binding.tvWelcome.text = "未登录"
            binding.tvSignature.text = "登录后可以设置头像、背景和签名"
            bindAvatar("")
            bindBackground("")
        }
    }

    private fun showImagePickerDialog(target: ImageTarget) {
        pendingImageTarget = target
        AlertDialog.Builder(requireContext())
            .setTitle(if (target == ImageTarget.AVATAR) "修改头像" else "修改背景图")
            .setItems(arrayOf("从相册选择", "拍一张")) { _, which ->
                when (which) {
                    0 -> pickImageLauncher.launch(arrayOf("image/*"))
                    1 -> takePhotoLauncher.launch(null)
                }
            }
            .show()
    }

    private fun showTextEditDialog(
        title: String,
        hint: String,
        initialValue: String,
        onConfirm: (String) -> Unit
    ) {
        val editText = EditText(requireContext()).apply {
            setText(initialValue)
            this.hint = hint
            setSelection(text.length)
            setPadding(48, 36, 48, 36)
        }

        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setView(editText)
            .setPositiveButton("保存") { _, _ ->
                val value = editText.text.toString().trim()
                if (value.isNotEmpty()) {
                    onConfirm(value)
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun saveImageUri(value: String) {
        when (pendingImageTarget) {
            ImageTarget.AVATAR -> viewModel.updateAvatar(value)
            ImageTarget.BACKGROUND -> viewModel.updateBackgroundImage(value)
            null -> Unit
        }
        pendingImageTarget = null
    }

    private fun saveBitmapToFile(bitmap: Bitmap): String? {
        return try {
            val prefix = if (pendingImageTarget == ImageTarget.AVATAR) "avatar" else "background"
            val file = File(requireContext().filesDir, "${prefix}_${System.currentTimeMillis()}.jpg")
            FileOutputStream(file).use { stream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
            }
            file.absolutePath
        } catch (_: Exception) {
            null
        }
    }

    private fun bindAvatar(avatar: String) {
        if (avatar.isBlank()) {
            binding.ivAvatar.visibility = View.GONE
            binding.tvAvatarLetter.visibility = View.VISIBLE
        } else {
            binding.ivAvatar.visibility = View.VISIBLE
            binding.tvAvatarLetter.visibility = View.GONE
            Glide.with(this)
                .load(resolveImageModel(avatar))
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_placeholder)
                .into(binding.ivAvatar)
        }
    }

    private fun bindBackground(background: String) {
        if (background.isBlank()) {
            binding.ivProfileBackground.setImageResource(R.drawable.mine_header_bg)
            binding.ivProfileBackground.alpha = 1f
        } else {
            binding.ivProfileBackground.alpha = 1f
            Glide.with(this)
                .load(resolveImageModel(background))
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_placeholder)
                .into(binding.ivProfileBackground)
        }
    }

    private fun resolveImageModel(value: String): Any {
        return if (value.startsWith("content://")) Uri.parse(value) else File(value)
    }
}
