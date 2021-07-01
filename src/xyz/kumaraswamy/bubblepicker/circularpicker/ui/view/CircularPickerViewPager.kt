package xyz.kumaraswamy.bubblepicker.circularpicker.ui.view

import android.content.Context
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager
import xyz.kumaraswamy.bubblepicker.circularpicker.ui.PickerPagerAdapter
import xyz.kumaraswamy.bubblepicker.circularpicker.pointInCircle


class CircularPickerViewPager : ViewPager {

    private var pagerAdapter = PickerPagerAdapter()

    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs)

    var swipeEnable = false

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        if (swipeEnable) return super.onTouchEvent(ev)
        else return false
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (swipeEnable) return super.onInterceptTouchEvent(ev)
        else return false
    }

    fun onAddView(view: CircularPickerView) {
        addTouchListener(view)
        this@CircularPickerViewPager.addView(view)
        pagerAdapter.addView(view)
        pagerAdapter.notifyDataSetChanged()
        adapter = pagerAdapter
    }

    fun getView(position: Int) = pagerAdapter.views[position] as CircularPickerView

    private fun addTouchListener(view: CircularPickerView) {
        view.apply {
            touchListener = object : CircularPickerView.TouchListener {
                override fun onViewTouched(pointF: PointF, event: MotionEvent?) {
                    val pickerPoint = pointInCircle(pointF, center, radius + maxPullUp) &&
                            !pointInCircle(pointF, center, (radius * swipeRadiusFactor))

                    picker = pickerPoint
                    this@CircularPickerViewPager.swipeEnable = !pickerPoint
                    this@CircularPickerViewPager.onInterceptTouchEvent(event)
                }
            }
        }
    }
}