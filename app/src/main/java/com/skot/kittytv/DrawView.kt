package com.skot.kittytv

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.media.MediaPlayer
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class DrawView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    val hexMap = HexMap(5, 5, 75)
    val paint = Paint()

    private lateinit var mediaPlayer: MediaPlayer

    init {
        paint.alpha = 255
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL

        mediaPlayer = MediaPlayer.create(context.applicationContext, R.raw.pop)

    }


    // Called when the view should render its content.
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val bitmap = hexMap.renderHexMap(width, height)
        canvas?.drawBitmap(bitmap, 0f, 0f, paint)
    }

    fun selectHex(event: MotionEvent) {
        val success = hexMap.selectHex(event.x.toInt(), event.y.toInt())
        if (success) {
            GlobalScope.launch {
                mediaPlayer.start()
            }
        }
    }


}