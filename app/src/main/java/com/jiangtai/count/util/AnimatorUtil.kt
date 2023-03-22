package com.jiangtai.count.util

import android.animation.ValueAnimator
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.ScaleAnimation

class AnimatorUtil {
    /**
     * View缩放动画
     *
     * @param view
     * @param time
     * @param count
     */
    fun scaleViewAnimator(view: View, time: Int, count: Int) {
        val scaleAnimation = ScaleAnimation(
            0f,
            1f,
            0f,
            1f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        scaleAnimation.duration = time.toLong()
        scaleAnimation.repeatCount = count
        scaleAnimation.fillAfter = true
        view.startAnimation(scaleAnimation)
    }

    companion object {
        private var animatorUtil: AnimatorUtil? = null
        val instance: AnimatorUtil?
            get() {
                if (animatorUtil == null) {
                    animatorUtil = AnimatorUtil()
                }
                return animatorUtil
            }

        fun getValueAnimator(
            min: Int,
            max: Int,
            animatorUpdateListener: ValueAnimator.AnimatorUpdateListener?
        ): ValueAnimator {
            val valueAnimator = ValueAnimator.ofInt(min, max)
            valueAnimator.duration = 3000
            valueAnimator.interpolator = LinearInterpolator()
            valueAnimator.addUpdateListener(animatorUpdateListener)
            //无限循环
            valueAnimator.repeatCount = ValueAnimator.INFINITE
            //从头开始动画
            valueAnimator.repeatMode = ValueAnimator.RESTART
            valueAnimator.start()
            return valueAnimator
        }
    }
}