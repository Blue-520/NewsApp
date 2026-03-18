package com.blue.newsapp.ui.home

import com.blue.newsapp.R
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.blue.newsapp.data.model.Article
import com.blue.newsapp.databinding.ItemNewsBinding
import com.bumptech.glide.Glide

class NewsAdapter(private val onItemClick : (Article) -> Unit): ListAdapter<Article, NewsAdapter.ViewHolder>(DiffCallBack()) {
    class ViewHolder(val binding: ItemNewsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val article = getItem(position)

        holder.binding.newsTitle.text = article.title
        holder.binding.newsSource.text = article.source.name


        val imageUrl = article.urlToImage
        if (imageUrl.isNullOrEmpty()){
            holder.binding.newsImage.setImageResource(R.drawable.img_error)
        }else{
            Glide.with(holder.binding.newsImage.context)
                .load(imageUrl)                                     // 加载网络图片
                .placeholder(R.drawable.img_placeholder)        // 图片还没下载完成之前，先显示占位图。
                .error(R.drawable.img_error)                    // 加载失败显示的图
                .centerCrop()                                               // 按比例缩放图片，让它铺满 ImageView，多余部分裁掉。
                .into(holder.binding.newsImage)                      // 最终显示到 ImageView
        }

        holder.binding.root.setOnClickListener {
            onItemClick(article)
        }
    }

    class DiffCallBack: DiffUtil.ItemCallback<Article>(){
        override fun areItemsTheSame(
            oldItem: Article,
            newItem: Article
        ): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(
            oldItem: Article,
            newItem: Article
        ): Boolean {
            return oldItem == newItem
        }
    }
}