package com.blue.newsapp.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.blue.newsapp.data.loacl.entity.NewsCommentEntity
import com.blue.newsapp.data.remote.model.response.CommentResponse
import com.blue.newsapp.databinding.ItemNewsCommentBinding
import java.text.SimpleDateFormat
import java.util.Locale

class NewsCommentAdapter: ListAdapter<CommentResponse, NewsCommentAdapter.ViewHolder>(DiffCallBack()) {

    class ViewHolder(val binding: ItemNewsCommentBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(comment: CommentResponse){
            binding.tvCommentContent.text = comment.content
            binding.tvCommentTime.text = comment.createTime
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
        holder.bind(getItem(position))
    }

    class DiffCallBack: DiffUtil.ItemCallback<CommentResponse>(){
        override fun areItemsTheSame(
            oldItem: CommentResponse,
            newItem: CommentResponse
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: CommentResponse,
            newItem: CommentResponse
        ): Boolean {
            return oldItem == newItem
        }
    }
}
