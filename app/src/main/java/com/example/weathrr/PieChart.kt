package com.example.weathrr

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.util.AttributeSet
import android.view.View

class PieChart @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var aqiLevel : Int
    private var p_radius : Float

    init {
        context.theme.obtainStyledAttributes(
            attrs,R.styleable.PieGraph,0,0).apply {

                try {
                  aqiLevel = getInt(R.styleable.PieGraph_aqiValue,2)
                  p_radius = getFloat(R.styleable.PieGraph_pRadius,80f)

                } finally {
                    recycle()
                }
        }
    }

    private val circlePaint = android.graphics.Paint(ANTI_ALIAS_FLAG).apply {
        when(aqiLevel){
            1-> color = 0xff00E676.toInt()
            2-> color = 0xffeeff41.toInt()
            3-> color = 0xfff9a825.toInt()
            4-> color = 0xffff3d00.toInt()
            5-> color = 0xffa30000.toInt()
        }

        postInvalidate()
        requestLayout()
    }

    private var exteriorPaint = android.graphics.Paint(ANTI_ALIAS_FLAG).apply {
        color = 0xff101010.toInt()
        postInvalidate()
        requestLayout()
    }

    private var interiorPaint = android.graphics.Paint(ANTI_ALIAS_FLAG).apply {
        color = 0xffffffff.toInt()

        postInvalidate()
        requestLayout()
    }


    private var defaultPaint = android.graphics.Paint(ANTI_ALIAS_FLAG).apply {
        color = 0x25ffffff.toInt()
//        color = resources.getColor(R.color.material_on_background_disabled,
//            Resources.Theme)

        postInvalidate()
        requestLayout()
    }


    fun setAqiValue(aqiValue : Int){
        aqiLevel = aqiValue

        when(aqiLevel){
            1-> circlePaint.color = 0xff00E676.toInt()
            2-> circlePaint.color = 0xffeeff41.toInt()
            3-> circlePaint.color = 0xfff9a825.toInt()
            4-> circlePaint.color = 0xffff3d00.toInt()
            5-> circlePaint.color = 0xffa30000.toInt()
        }

        postInvalidate()
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val desiredWidth = 250
        val desiredHeight = 250

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width : Int
        val height : Int

        if(widthMode == MeasureSpec.EXACTLY){
            width = widthSize
        } else if (widthMode == MeasureSpec.AT_MOST){
            width = Math.min(desiredWidth,widthSize)
        } else {
            width = desiredWidth
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(desiredHeight, heightSize)
        } else {
            height = desiredHeight
        }

        setMeasuredDimension(width, height)

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.apply {

            val sweepAngle = 360f - (aqiLevel-1)*72f

            drawCircle(p_radius+20f,p_radius+20f,p_radius,exteriorPaint)
            drawCircle(p_radius+20f,p_radius+20f,p_radius-1f,interiorPaint)
            drawArc(1f+20f,1f+20f,2*p_radius-1+20f,2*p_radius-1+20f,
                -90f,sweepAngle,true,circlePaint)
            drawCircle(p_radius+20f,p_radius+20f,p_radius-21f,interiorPaint)
            drawCircle(p_radius+20f,p_radius+20f,p_radius-22f,exteriorPaint)
            drawCircle(p_radius+20f,p_radius+20f,p_radius-23f,defaultPaint)


        }
    }
}