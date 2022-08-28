package com.example.exhibitionguestbook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.SYSTEM_UI_FLAG_FULLSCREEN

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val canvasView = CanvasView(this)
        canvasView.systemUiVisibility = SYSTEM_UI_FLAG_FULLSCREEN
        setContentView(canvasView)
    }
}