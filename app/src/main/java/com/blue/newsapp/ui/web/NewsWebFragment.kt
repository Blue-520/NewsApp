package com.blue.newsapp.ui.web

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.blue.newsapp.databinding.FragmentNewsWebBinding

class NewsWebFragment: Fragment() {

    private var _binding : FragmentNewsWebBinding? = null
    private val binding get() = _binding!!
    private val args : NewsWebFragmentArgs by navArgs()

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewsWebBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // 获取从详情页传来的 url
        val url = args.url

        // WebView 基本设置
        binding.webView.settings.apply {
            javaScriptEnabled = true          // 允许执行 JavaScript
            domStorageEnabled = true          // 开启 DOM 本地存储，很多现代网页都需要
            loadsImagesAutomatically = true   // 自动加载图片
            mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
            // 允许 https 页面中加载部分 http 资源，某些网页会用到
        }

        // 让网页在当前 WebView 中打开，而不是跳到系统浏览器, 处理网页跳转、开始加载、加载失败等“网页内容层”的事情
        binding.webView.webViewClient = object : WebViewClient(){

            // 点击网页里的链接时，仍然在当前 WebView 中打开
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                // 返回 false 表示不用你拦截,仍然在当前 WebView 里加载
                return false
            }

            // 网页开始加载，显示进度条
/*            override fun onPageStarted(
                view: WebView?,
                url: String?,
                favicon: Bitmap?
            ) {
                super.onPageStarted(view, url, favicon)
                binding.progressBar.visibility = View.VISIBLE
            }*/

            /*// 网页加载完成，隐藏进度条
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                binding.progressBar.visibility = View.GONE
            }*/

            // 网页加载失败，隐藏进度条并提示错误
            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                binding.progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "网页打开失败", Toast.LENGTH_SHORT).show()
            }
        }

        // 处理网页进度、网页标题等“浏览器表现层”的事情
        binding.webView.webChromeClient = object : WebChromeClient(){
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)

                // 把网页当前加载进度同步给顶部进度条
                binding.progressBar.progress = newProgress

                // 进度到 100，说明页面基本加载完成，隐藏进度条
                if (newProgress == 100){
                    binding.progressBar.visibility = View.GONE
                }else{
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        }

        // 加载网页
        if (url.isNotEmpty()){
            binding.progressBar.visibility = View.VISIBLE
            binding.webView.loadUrl(url)
        }

        // 校验一下 url，避免空字符串或者非法字符串直接加载
        if (url.startsWith("http://") || url.startsWith("https://")) {
            binding.progressBar.visibility = View.VISIBLE
            binding.webView.loadUrl(url)
        } else {
            Toast.makeText(requireContext(), "网页地址无效", Toast.LENGTH_SHORT).show()
        }

        // 处理系统返回键：如果网页可以后退，就先在网页里后退，否则再退出当前 Fragment
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    if (binding.webView.canGoBack()){
                        binding.webView.goBack()
                    }else{
                        isEnabled = false
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                }
            }
        )
    }

    //释放 WebView 资源，避免内存泄漏
    override fun onDestroyView() {
        super.onDestroyView()

        // 先停止加载
        binding.webView.stopLoading()

        // 从父布局中移除，再销毁 WebView，避免内存泄漏风险
        (binding.webView.parent as? ViewGroup)?.removeView(binding.webView)
        binding.webView.destroy()

        binding.webView.destroy()
        _binding = null
    }
}