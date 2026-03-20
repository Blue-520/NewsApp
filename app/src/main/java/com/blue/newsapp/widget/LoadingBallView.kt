package com.blue.newsapp.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.ContextCompat
import com.blue.newsapp.R

class LoadingBallView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr) {

    //画笔：用来画圆
    private val ballPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.black)
        style = Paint.Style.FILL
    }

    //小球半径
    private var ballRadius = 20f

    //3个小球之间的间距
    private var ballSpacing = 40f

    //每个球当前的Y方向偏移量
    private val offsets = floatArrayOf(0f, 0f, 0f)

    //跳动高度
    private var jumpHeight = 30f

    //动画集合，方便后面停止
    private val animators = mutableListOf<ValueAnimator>()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //给控件一个默认建议大小
        val desiredWidth = (ballRadius * 6 + ballSpacing *2 + paddingStart + paddingEnd).toInt()
        val desiredHeight = (ballRadius * 4 + jumpHeight + paddingTop + paddingBottom).toInt()

        val measuredWidth = resolveSize(desiredWidth, widthMeasureSpec)
        val measuredHeight = resolveSize(desiredHeight, heightMeasureSpec)

        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val contentWidth = width - paddingStart - paddingEnd
        val contentHeight = height - paddingTop - paddingBottom

        //三个球整体居中
        val totalBallsWidth = ballRadius * 2 * 3 + ballSpacing * 2
        val startX = paddingStart + (contentWidth - totalBallsWidth) / 2f + ballRadius
        val centerY = paddingTop + contentHeight / 2f

        for (i in  0..2){
            val cx = startX + i * (ballRadius * 2 + ballSpacing)
            val cy = centerY + offsets[i]
            canvas.drawCircle(cx, cy, ballRadius, ballPaint)
        }
    }

    private fun startBallAnim(index: Int, delay: Long){
        val animator = ValueAnimator.ofFloat(0f, -jumpHeight, 0f).apply {
            duration = 600
            startDelay = delay
            repeatCount = ValueAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()

            addUpdateListener {
                offsets[index] = it.animatedValue as Float
                invalidate()   //动画变化时，通知系统重新绘制
            }
        }

        animators.add(animator)
        animator.start()
    }

    fun startLoading(){
        if (animators.isNotEmpty()) return

        startBallAnim(index = 0, delay = 0)
        startBallAnim(index = 1, delay = 120)
        startBallAnim(index = 2, delay = 240)
    }

    fun stopLoading(){
        animators.forEach { it.cancel() }
        animators.clear()

        for (i in offsets.indices){
            offsets[i] = 0f
        }
        invalidate()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startLoading()
    }

    override fun onDetachedFromWindow() {
        stopLoading()
        super.onDetachedFromWindow()
    }
}