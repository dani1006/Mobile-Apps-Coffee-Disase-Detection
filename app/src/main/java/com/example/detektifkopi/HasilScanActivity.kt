package com.example.detektifkopi

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView

class HasilScanActivity : AppCompatActivity() {
    private lateinit var imgResult: ImageView
    private lateinit var tvPenyakit: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hasil_scan)

        imgResult = findViewById(R.id.img_hasil)
        tvPenyakit = findViewById(R.id.tv_penyakit)


        val capturedImageBitmap = intent.getParcelableExtra<Bitmap>("capturedImage")
        val resultLabel = intent.getStringExtra("resultLabel")

        imgResult.setImageBitmap(capturedImageBitmap)

        // Set the text on the TextView based on the result label
        tvPenyakit.text = when (resultLabel) {
            "Miner" -> "Miner"
            "Sehat" -> "Sehat"
            "Phoma" -> "Phoma"
            "Rust" -> "Rust"
            else -> ""
        }

    }



    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }
}