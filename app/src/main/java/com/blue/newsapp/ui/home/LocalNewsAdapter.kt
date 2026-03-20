package com.blue.newsapp.ui.home

import com.blue.newsapp.R
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.blue.newsapp.data.loacl.entity.NewsEntity
import com.blue.newsapp.databinding.ItemNewsBinding
import com.bumptech.glide.Glide

class LocalNewsAdapter(private val onItemClick : (NewsEntity) -> Unit): ListAdapter<NewsEntity, LocalNewsAdapter.ViewHolder>(DiffCallBack()) {
    class ViewHolder(val binding: ItemNewsBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(news: NewsEntity, onItemClick: (NewsEntity) -> Unit){
            binding.newsCardView.bind(title = news.title, source = news.name, time = news.publishedAt ?: "", imageUrl = news.urlToImage)

            binding.root.setOnClickListener {
                onItemClick(news)
            }
        }
    }

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
        val news = getItem(position)
        holder.bind(news, onItemClick)
    }

    class DiffCallBack: DiffUtil.ItemCallback<NewsEntity>(){
        override fun areItemsTheSame(
            oldItem: NewsEntity,
            newItem: NewsEntity
        ): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(
            oldItem: NewsEntity,
            newItem: NewsEntity
        ): Boolean {
            return oldItem == newItem
        }
    }
}