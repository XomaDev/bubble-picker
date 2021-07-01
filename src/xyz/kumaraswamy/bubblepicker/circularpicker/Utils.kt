package xyz.kumaraswamy.bubblepicker.circularpicker

import android.graphics.PointF
import java.lang.Math.*
import kotlin.math.pow
import kotlin.math.roundToInt

fun getPointOnBorderLineOfCircle(center: PointF, radius: Float, alfa: Int = 0) =
        PointF().apply {
            x = (radius * kotlin.math.cos(toRadians(alfa - 90.0)) + center.x).toFloat()
            y = (radius * kotlin.math.sin(toRadians(alfa - 90.0)) + center.y).toFloat()
        }

fun calculateAngleWithTwoVectors(touch: PointF?, center: PointF?): Float {
    var angle = 0.0
    if (touch != null && center != null) {
        val x2 = touch.x - center.x
        val y2 = touch.y - center.y
        val d1 = kotlin.math.sqrt((center.y * center.y).toDouble())
        val d2 = kotlin.math.sqrt((x2 * x2 + y2 * y2).toDouble())
        angle = if (touch.x >= center.x) {
            toDegrees(kotlin.math.acos((-center.y * y2) / (d1 * d2)))
        } else
            360 - toDegrees(kotlin.math.acos((-center.y * y2) / (d1 * d2)))
    }
    return angle.toFloat()
}

fun distance(point1: PointF, point2: PointF): Float {
    return kotlin.math.sqrt(((point1.x - point2.x) * (point1.x - point2.x) + (point1.y - point2.y) * (point1.y - point2.y)).toDouble()).toFloat()
}

fun pointInCircle(point: PointF, pointCenter: PointF, radius: Float) =
        (point.x - pointCenter.x).toDouble().pow(2.0) +
                (point.y - pointCenter.y).toDouble().pow(2.0) <= radius * radius


fun closestValue(value: Int, step: Int): Int {
    var j = (value.toDouble().roundToInt())
    while (true) {
        if (j > 0 && step > 0) {
            if (j % step == 0)
                return j
            else
                ++j
        } else
            return j
    }
}
