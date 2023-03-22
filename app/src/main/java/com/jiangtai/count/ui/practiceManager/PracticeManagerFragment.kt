package com.jiangtai.count.ui.practiceManager

import android.graphics.Color
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.util.SparseArray
import android.view.View
import com.jiangtai.count.R
import com.jiangtai.count.base.BaseFragment
import com.jiangtai.count.ui.majorpractice.MajorPracticeFragment
import kotlinx.android.synthetic.main.fragment_practice_manager.*

/**
 * @author xiezekai
 * @des 计划管理页面 - MainActivity fragment[0]
 * @create time 2021/8/4 12:20 下午
 */
class PracticeManagerFragment : BaseFragment() {
    private var currentIndex = 0
    private var fragments: SparseArray<Fragment> = SparseArray()

    override fun attachLayoutRes(): Int {
        return R.layout.fragment_practice_manager
    }

    override fun initView(view: View) {
        initImmersionBar(fitSystem = false, dark = false)
        if (fragments.size() == 0) {
            //   fragments.put(0,PracticeFragment.getInstance())
            fragments.put(0, MajorPracticeFragment.getInstance())
        }
        viewPager.offscreenPageLimit = 1
        viewPager.adapter = TabAdapter(requireActivity().supportFragmentManager)
        viewPager.setCanScroll(true)
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                currentIndex = position
                requireActivity()?.let {
//                    if(position == 0){
//                        normal.setTextColor(it.resources.getColor(R.color.white))
//                        major.setTextColor(it.resources.getColor(R.color.black))
//                        normal.setBackgroundColor(it.resources.getColor(R.color.color_blue_2979FF))
//                        major.setBackgroundColor(it.resources.getColor(R.color.white))
//                    } else {
//                        major.setBackgroundColor(it.resources.getColor(R.color.white))
//                        normal.setBackgroundColor(it.resources.getColor(R.color.black))
                    major.setBackgroundColor(Color.parseColor("#183861"))
                    normal.setBackgroundColor(Color.parseColor("#183861"))
                    //}
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })


        normal.setOnClickListener {
            viewPager.currentItem = 0
        }
        major.setOnClickListener {
            viewPager.currentItem = 1
        }


    }

    override fun lazyLoad() {

    }

    companion object {
        fun getInstance(): PracticeManagerFragment = PracticeManagerFragment()
    }


    inner class TabAdapter(fm: FragmentManager) :
        FragmentStatePagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            return fragments!!.get(position)
        }

        override fun getCount(): Int {
            return fragments!!.size()
        }
    }


    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        initImmersionBar(dark = false)
    }
}