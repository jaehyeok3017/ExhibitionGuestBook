package com.example.exhibitionguestbook

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
import android.view.ViewConfiguration
import androidx.core.content.res.ResourcesCompat
import com.example.exhibitionguestbook.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var canvasView : CanvasView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        canvasView = CanvasView(this, attrs = null)

        binding.colorBlack.setOnClickListener { canvasView.drawColorSet("black") }
        binding.colorBlue.setOnClickListener { canvasView.drawColorSet("blue") }
        binding.colorGreen.setOnClickListener { canvasView.drawColorSet("green") }
        binding.colorOrange.setOnClickListener { canvasView.drawColorSet("orange") }
        binding.colorPink.setOnClickListener { canvasView.drawColorSet("pink") }
        binding.colorRed.setOnClickListener { canvasView.drawColorSet("red") }

        binding.erase.setOnClickListener { canvasView.erase() }
        binding.delete.setOnClickListener { canvasView.reset() }
    }
}

