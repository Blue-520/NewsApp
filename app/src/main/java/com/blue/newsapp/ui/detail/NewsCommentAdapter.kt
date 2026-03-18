package com.blue.newsapp.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.blue.newsapp.data.loacl.entity.NewsCommentEntity
import com.blue.newsapp.databinding.ItemNewsCommentBinding
import java.text.SimpleDateFormat
import java.util.Locale

class NewsCommentAdapter: ListAdapter<NewsCommentEntity, NewsCommentAdapter.ViewHolder>(DiffCallBack()) {

    class ViewHolder(val binding: ItemNewsCommentBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(comment: NewsCommentEntity){
            binding.tvCommentContent.text = comment.content
            binding.tvCommentTime.text = formation(comment.createTime)
        }

        /**
         * 把时间戳转换成 yyyy-MM-dd HH:mm 格式
         */
        private fun formation(timeMillis: Long): String{
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            return sdf.format(timeMillis)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemNewsCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val comment = getItem(position)
        holder.bind(comment)
    }

    class DiffCallBack: DiffUtil.ItemCallback<NewsCommentEntity>(){
        override fun areItemsTheSame(
            oldItem: NewsCommentEntity,
            newItem: NewsCommentEntity
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: NewsCommentEntity,
            newItem: NewsCommentEntity
        ): Boolean {
            return oldItem == newItem
        }
    }
}