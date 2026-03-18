package com.blue.newsapp.ui.FavoriteNews


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.blue.newsapp.R
import com.blue.newsapp.data.loacl.entity.FavoriteNewsEntity
import com.blue.newsapp.databinding.ItemFavoriteNewsBinding
import com.bumptech.glide.Glide

class FavoriteNewsAdapter(private val OnItemClick:(FavoriteNewsEntity) -> Unit): ListAdapter<FavoriteNewsEntity, FavoriteNewsAdapter.ViewHolder>(DiffCallBack()) {

    class ViewHolder(val binding: ItemFavoriteNewsBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: FavoriteNewsEntity, onItemClick:(FavoriteNewsEntity) -> Unit){
            binding.tvNewsTitle.text = item.title
            binding.tvNewsSource.text = "来源：${item.sourceName}"
            binding.tvPublishedAt.text = "发布时间：${item.publishedAt}"

            if (item.imageUrl.isEmpty()){
                binding.ivNewsImage.setImageResource(R.drawable.img_error)
            }else{
                Glide.with(binding.root.context)
                    .load(item.imageUrl)
                    .placeholder(R.drawable.img_placeholder)
                    .error(R.drawable.img_error)
                    .centerCrop()
                    .into(binding.ivNewsImage)
            }

            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemFavoriteNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val item = getItem(position)
        holder.bind(item, OnItemClick)
    }

    class DiffCallBack: DiffUtil.ItemCallback<FavoriteNewsEntity>(){
        override fun areItemsTheSame(
            oldItem: FavoriteNewsEntity,
            newItem: FavoriteNewsEntity
        ): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(
            oldItem: FavoriteNewsEntity,
            newItem: FavoriteNewsEntity
        ): Boolean {
            return oldItem == newItem
        }
    }
}