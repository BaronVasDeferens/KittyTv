package com.skot.kittytv

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.delay

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


    override fun onStart() {
        super.onStart()

        drawView.setOnTouchListener( object: View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {

                if (event != null) {

                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            println(">>> DOWN")
                            drawView.selectHex(event)
                        }
                    }
                }

                return true
            }

        })

        lifecycleScope.launchWhenResumed {
            while (true) {
                drawView.invalidate()
                delay(100)
            }
        }
    }
}
