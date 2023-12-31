package kr.co.bullets.part2chapter2r

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class WaveformView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {
    private val ampList = mutableListOf<Float>()
    private val rectList = mutableListOf<RectF>()
//    val rectF = RectF(20f, 30f, 20f + 30f, 30f + 60f)
    val redPaint = Paint().apply {
        color = Color.RED
    }

//    private val rectWidth = 10f
    private val rectWidth = 15f
    private var tick = 0

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

//        canvas?.drawRect(rectF, redPaint)
        for (rectF in rectList) {
            canvas?.drawRect(rectF, redPaint)
        }
    }

    fun addAmplitude(maxAmplitude: Float) {
        val height = this.height

        // 0 ~ 1
        // (maxAmplitude / Short.MAX_VALUE)
        val amplitude = (maxAmplitude / Short.MAX_VALUE) * height * 0.8f

//        ampList.add(maxAmplitude)
        ampList.add(amplitude)
        rectList.clear()

//        val rectWidth = 10f
        val maxRect = (this.width / rectWidth).toInt()

        val amps = ampList.takeLast(maxRect)

        for ((i, amp) in amps.withIndex()) {
            val rectF = RectF()

//            rectF.top = 0f
            rectF.top = (this.height / 2) - amp / 2
//            rectF.bottom = amp
            rectF.bottom = rectF.top + amp
            rectF.left = i * rectWidth
//            rectF.right = rectF.left + rectWidth
            rectF.right = rectF.left + (rectWidth - 5f)

            rectList.add(rectF)
        }

//        rectF.top = 0f
//        rectF.bottom = maxAmplitude
//        rectF.left = 0f
//        rectF.right = rectF.left + 20f

        invalidate()
    }

    fun replayAmplitude(duration: Int) {
        rectList.clear()

//        val rectWidth = 10f
        val maxRect = (this.width / rectWidth).toInt()

        val amps = ampList.take(tick).takeLast(maxRect)

        for ((i, amp) in amps.withIndex()) {
            val rectF = RectF()

//            rectF.top = 0f
            rectF.top = (this.height / 2) - amp / 2
//            rectF.bottom = amp
            rectF.bottom = rectF.top + amp
            rectF.left = i * rectWidth
//            rectF.right = rectF.left + rectWidth
            rectF.right = rectF.left + (rectWidth - 5f)

            rectList.add(rectF)
        }

        tick++

        invalidate()
    }

    fun clearData() {
        ampList.clear()
    }

    fun clearWave() {
        rectList.clear()
        tick = 0
        invalidate()
    }
}