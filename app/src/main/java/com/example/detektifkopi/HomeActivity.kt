package com.example.detektifkopi

import android.content.Intent
import android.content.pm.PackageManager
import android.Manifest.permission.CAMERA
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton

class HomeActivity : AppCompatActivity() {
    private val CAMERA_PERMISSION_REQUEST_CODE = 1001
    private val CAMERA_REQUEST_CODE = 1002

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val btnScan: MaterialButton = findViewById(R.id.btn_scan)
        btnScan.setOnClickListener {
            if (checkCameraPermission()) {
                openCamera()
            } else {
                requestCameraPermission()
            }
        }
    }

        private fun checkCameraPermission(): Boolean {
            val result = ContextCompat.checkSelfPermission(
                this,
                "android.permission.CAMERA"
            )
            return result == PackageManager.PERMISSION_GRANTED
        }

        private fun requestCameraPermission() {
            ActivityCompat.requestPermissions(
                this,
                arrayOf("android.permission.CAMERA"),
//                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        }

        private fun openCamera() {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
        }

        override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            when (requestCode) {
                CAMERA_PERMISSION_REQUEST_CODE -> {
                    if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        openCamera()
                    } else {
                        Toast.makeText(
                            this,
                            "Permission denied. Cannot access camera.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    return
                }
            }
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
                // Handle the captured image here
                val imageUri: Uri? = data?.data
                // Further processing...
            }
        }

    }
