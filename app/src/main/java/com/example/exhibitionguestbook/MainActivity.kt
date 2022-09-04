package com.example.exhibitionguestbook

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.example.exhibitionguestbook.databinding.ActivityMainBinding
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var canvasView : CanvasView
    private var previousColor = "black"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        canvasView = CanvasView(this, attrs = null)
        binding.canvas.isDrawingCacheEnabled = true

        binding.colorBlack.setOnClickListener { canvasView.drawColorSet("black"); previousColor = "black" }
        binding.colorBlue.setOnClickListener { canvasView.drawColorSet("blue"); previousColor = "blue" }
        binding.colorGreen.setOnClickListener { canvasView.drawColorSet("green"); previousColor = "green" }
        binding.colorOrange.setOnClickListener { canvasView.drawColorSet("orange"); previousColor = "orange" }
        binding.colorPink.setOnClickListener { canvasView.drawColorSet("pink"); previousColor = "pink" }
        binding.colorRed.setOnClickListener { canvasView.drawColorSet("red"); previousColor = "red" }

        binding.erase.setOnClickListener { canvasView.drawColorSet("white"); previousColor = "white" }
        binding.reset.setOnClickListener { resetDialogShowAndClickListener() }

        binding.upload.setOnClickListener {
            val storage: FirebaseStorage? = FirebaseStorage.getInstance()

            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val imageFileName = "$timestamp.png"
            val stoargeRef = storage?.reference?.child("images")?.child(imageFileName)

            val baos = ByteArrayOutputStream()
            val bitmap = getBitmap()
            Log.d(TAG, "imageWidth : ${bitmap.width}, imageHeight : ${bitmap.height}")
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
    private fun restartActivity() = startActivity(Intent(this, MainActivity::class.java))
    private fun resetDialogShowAndClickListener() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.reset_dialog, null)
        val mBuilder = AlertDialog.Builder(this)
            .setView(dialogView)

        val alertDialog = mBuilder.show()

        val cancelBtn = dialogView.findViewById<Button>(R.id.cancel_button)
        val deleteBtn = dialogView.findViewById<Button>(R.id.delete_button)

        cancelBtn.setOnClickListener { alertDialog.dismiss() }
        deleteBtn.setOnClickListener { restartActivity() }
    }
}

