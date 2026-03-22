package com.blue.newsapp.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.blue.newsapp.R
import com.blue.newsapp.databinding.ViewNewsCardBinding
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class NewsCardView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defstyleAttr: Int = 0)
    : FrameLayout(context, attrs, defstyleAttr) {

    private val binding: ViewNewsCardBinding = ViewNewsCardBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        //读取XML里面传进来的自定义属性
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.NewsCardView)

            val title = typedArray.getString(R.styleable.NewsCardView_newsTitle)
            val source = typedArray.getString(R.styleable.NewsCardView_newsSource)
            val time = typedArray.getString(R.styleable.NewsCardView_newsTime)
            val imageResId = typedArray.getResourceId(R.styleable.NewsCardView_newsImage, R.drawable.img_placeholder)
            val imageUrl  = typedArray.getString(R.styleable.NewsCardView_newsImageUrl)

            setTitle(title ?: "")
            setSource(source ?: "")
            setTime(time ?: "")
            if (!imageUrl.isNullOrEmpty()) {
                setImageUrl(imageUrl)
            } else {
                setImage(imageResId)
            }


            typedArray.recycle()
        }
    }
    /**
     * 设置新闻标题
     */
    fun setTitle(title: String){
        binding.tvNewsTitle.text = title
    }

    /**
     * 设置新闻来源
     */
    fun setSource(source: String){
        binding.tvNewsSource.text = source
    }

    /**
     * 设置发布时间
     */
    fun setTime(time: String){
        binding.tvNewsTime.text = formatApiTime(time)
    }

    /**
     * 设置本地图片资源
     */
    fun setImage(resId: Int){
        binding.ivNewsImage.setImageResource(resId)
    }

    /**
     * 设置网络图片
     */
    fun setImageUrl(url: String){
        if (url.isBlank()){
            setPlaceholder()
            return
        }

        Glide.with(context)
            .load(url)
            .placeholder(R.drawable.img_placeholder)
            .error(R.drawable.img_error)
            .into(binding.ivNewsImage)
    }

    /**
     * 设置默认占位图
     */
    fun setPlaceholder(){
        binding.ivNewsImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.img_placeholder))
    }

    /**
     * 给外部一个统一绑定方法
     */
    fun bind(title: String, source: String, time: String, imageUrl: String?){
        setTitle(title)
        setSource(source)
        setTime(time)

        if (imageUrl.isNullOrBlank()) {
            setPlaceholder()
        } else {
            setImageUrl(imageUrl)
        }
    }

    /**
     * 将接口的String时间改为标准格式
     */
    private fun formatApiTime(timeStr: String?): String {
        if (timeStr.isNullOrBlank()) return ""

        return try {
            val inputSdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            inputSdf.timeZone = TimeZone.getTimeZone("UTC")

            val outputSdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val date = inputSdf.parse(timeStr)

            if (date != null) outputSdf.format(date) else ""
        } catch (e: Exception) {
            timeStr
        }
    }
}