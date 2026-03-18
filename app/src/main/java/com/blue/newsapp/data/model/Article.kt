package com.blue.newsapp.data.model

import Source
import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Article(

    val source: Source,           // 新闻来源对象

    val author: String?,          // 作者，可能为空

    val title: String,            // 标题

    val description: String?,     // 简介，可能为空

    val url: String,              // 新闻网页链接

    val urlToImage: String?,      // 新闻图片链接，可能为空

    val publishedAt: String?,     // 发布时间，可能为空

    val content: String?          // 正文摘要，可能为空

): Parcelable
