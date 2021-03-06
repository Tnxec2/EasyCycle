package com.kontranik.easycycle.components.circularTextView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet

import androidx.appcompat.widget.AppCompatTextView

class CircularTextView : AppCompatTextView {
    private var strokeWidth = 0f
    private var strokeColor = 0
    private var solidBackgroundColor = 0

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun draw(canvas: Canvas) {
        val circlePaint = Paint()
        circlePaint.color = solidBackgroundColor
        circlePaint.flags = Paint.ANTI_ALIAS_FLAG
        val strokePaint = Paint()
        strokePaint.color = strokeColor
        strokePaint.flags = Paint.ANTI_ALIAS_FLAG
        val h = this.height
        val w = this.width
        val diameter = if (h > w) h else w
        val radius = diameter / 2F
        this.height = diameter
        this.width = diameter
        canvas.drawCircle(diameter / 2F, diameter / 2F, radius, strokePaint)
        canvas.drawCircle(diameter / 2F, diameter / 2F, radius - strokeWidth, circlePaint)
        super.draw(canvas)
    }

    fun setStrokeWidth(dp: Int) {
        val scale = context.resources.displayMetrics.density
        strokeWidth = dp * scale
    }

    fun setStrokeColor(colorHex: String?) {
        strokeColor = Color.parseColor(colorHex)
    }

    fun setSolidColor(colorHex: String?) {
        solidBackgroundColor = Color.parseColor(colorHex)
    }

    fun clearSolidColor() {
        solidBackgroundColor = Color.TRANSPARENT
    }
}