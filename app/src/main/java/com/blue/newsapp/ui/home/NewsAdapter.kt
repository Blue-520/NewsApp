package com.blue.newsapp.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.blue.newsapp.data.model.Article
import com.blue.newsapp.databinding.ItemNewsBinding

class NewsAdapter(private val onItemClick : (Article) -> Unit): ListAdapter<Article, NewsAdapter.ViewHolder>(DiffCallBack()) {
    class ViewHolder(val binding: ItemNewsBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(news: Article, onItemClick: (Article) -> Unit){
            binding.newsCardView.bind(title = news.title, source = news.source.name, time = news.publishedAt ?: "", imageUrl = news.urlToImage)

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
        val article = getItem(position)

        holder.bind(article, onItemClick)
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