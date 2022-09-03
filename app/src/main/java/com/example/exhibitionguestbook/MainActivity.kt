package com.example.exhibitionguestbook

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.graphics.Bitmap
import android.graphics.Point
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.exhibitionguestbook.databinding.ActivityMainBinding
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var canvasView : CanvasView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        canvasView = CanvasView(this, attrs = null)
        binding.canvas.isDrawingCacheEnabled = true

        binding.colorBlack.setOnClickListener { canvasView.drawColorSet("black") }
        binding.colorBlue.setOnClickListener { canvasView.drawColorSet("blue") }
        binding.colorGreen.setOnClickListener { canvasView.drawColorSet("green") }
        binding.colorOrange.setOnClickListener { canvasView.drawColorSet("orange") }
        binding.colorPink.setOnClickListener { canvasView.drawColorSet("pink") }
        binding.colorRed.setOnClickListener { canvasView.drawColorSet("red") }

        binding.erase.setOnClickListener { canvasView.drawColorSet("white") }
        binding.reset.setOnClickListener {
            val display = windowManager.defaultDisplay
            val size = Point()
            display.getRealSize(size)

            val width = size.x
            val height = size.y
            canvasView.reset(width, height)
        }

        binding.upload.setOnClickListener {
            val storage: FirebaseStorage? = FirebaseStorage.getInstance()

            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val imageFileName = "$timestamp.png"
            val stoargeRef = storage?.reference?.child("images")?.child(imageFileName)

            val baos = ByteArrayOutputStream()
            val bitmap = getBitmap()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            stoargeRef?.putBytes(data)?.addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding document", e)
            }?.addOnSuccessListener {
                Log.d(TAG, "업로드 완료")
            }
        }

        binding.plus.setOnClickListener { canvasView.strokeWidthSet("plus") }
        binding.minus.setOnClickListener { canvasView.strokeWidthSet("minus") }
    }

    private fun getBitmap() : Bitmap = binding.canvas.drawingCache
}

