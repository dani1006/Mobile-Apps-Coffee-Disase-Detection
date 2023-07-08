package com.example.detektifkopi.fargment

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.tool.util.FileUtil
import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.detektifkopi.HasilScanActivity
import com.example.detektifkopi.R
import com.example.detektifkopi.adapter.ListArtikelAdapter
import com.example.detektifkopi.data.Artikel
import com.google.android.material.button.MaterialButton
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel


class Home : Fragment(), ListArtikelAdapter.OnItemClickCallback {
    private val CAMERA_PERMISSION_REQUEST_CODE = 100
    private val IMAGE_SIZE = 512
    private lateinit var tfliteInterpreter: Interpreter

    private lateinit var btn_scan: MaterialButton

    //    private lateinit var text_transaksi: TextView
    private lateinit var text_card: TextView

    //for MACAHINE LEARNING
    // Load the TFLite model from assets
    private fun loadModelFile(): MappedByteBuffer {
        val assetFileDescriptor = requireContext().assets.openFd("Coffee_disease_v8.tflite")
        val inputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    // Initialize the TFLite interpreter
    private fun initializeTFLiteInterpreter() {
        val options = Interpreter.Options()
        tfliteInterpreter = Interpreter(loadModelFile(), options)
    }

    // Perform inference using the loaded model
//    private fun runInference(inputFeature: TensorBuffer): TensorBuffer {
//        val inputShape = intArrayOf(
//            1,
//            150,
//            300,
//            3
//        ) // Adjust the shape according to your model input requirements
//        inputFeature.loadBuffer(inputFeature.buffer)
//
//        // Run model inference and get the result
//        val outputFeature = TensorBuffer.createFixedSize(inputShape, DataType.FLOAT32)
//        tfliteInterpreter.run(inputFeature.buffer, outputFeature.buffer)
//        return outputFeature
//    }

    // Perform inference using the loaded model
    private fun runInference(inputFeature: TensorBuffer): Int {
        val inputShape = intArrayOf(1, 150, 300, 3) // Adjust the shape according to your model input requirements
        inputFeature.loadBuffer(inputFeature.buffer)

        // Run model inference and get the result
        val outputFeature = TensorBuffer.createFixedSize(inputShape, DataType.FLOAT32)
        tfliteInterpreter.run(inputFeature.buffer, outputFeature.buffer)

        // Get the predicted label
        val predictedLabels = outputFeature.floatArray.indices.maxByOrNull { outputFeature.floatArray[it] }

        return predictedLabels ?: -1 // Return -1 if no label is found
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)


        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)

        val dataJudul = resources.getStringArray(R.array.data_judul)
        val dataSubJudul = resources.getStringArray(R.array.data_sub)
        val dataLink = resources.getStringArray(R.array.data_link)
        val dataGambar = resources.obtainTypedArray(R.array.data_photo)

        val fragmentArtikel = FragmentArtikel()

        val itemList = mutableListOf<Artikel>()
        for (i in 0 until minOf(3, dataJudul.size)) {
            val judul = dataJudul[i]
            val subJudul = dataSubJudul[i]
            val link = dataLink[i]
            val gambar = dataGambar.getResourceId(i, 0)
            val item = Artikel(judul, subJudul, gambar, link)
            itemList.add(item)
        }

        val cardTutorial: CardView = view.findViewById(R.id.card_tutorial)
        val viewAll: TextView = view.findViewById(R.id.viewall)

        val padding = resources.getDimensionPixelSize(R.dimen.item_margin)

        val recyclerView: RecyclerView? = view?.findViewById(R.id.artikel_terkait)
        recyclerView?.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView?.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                outRect.right = padding // Atur padding kanan
            }
        })
        dataGambar.recycle()

        val youtubeLink = "https://youtu.be/TXUPx7P4ttI"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(youtubeLink))

        viewAll.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.f1_wrapper, fragmentArtikel)
                .addToBackStack(null)
                .commit()

            bottomNavigationView.menu.findItem(R.id.navigation_artikel).isChecked = true

        }

        val colorStateList = ContextCompat.getColorStateList(requireContext(), R.color.color_icon)
        bottomNavigationView.itemIconTintList = colorStateList
        bottomNavigationView.itemTextColor = colorStateList


        cardTutorial.setOnClickListener {
            if (intent.resolveActivity(requireContext().packageManager) != null) {
                startActivity(intent)
            } else {
                // Handle the case where YouTube app or a web browser is not available
                Toast.makeText(
                    requireContext(),
                    "YouTube app or web browser is not available",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        val adapter = ListArtikelAdapter(itemList)
        adapter.setOnItemClickCallback(this)
        recyclerView?.adapter = adapter

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_scan = view.findViewById(R.id.btn_scan)

        btn_scan.setOnClickListener {
            if (checkCameraPermission()) {
                // Izin kamera telah diberikan, buka kamera di sini
                openCamera()
            } else {
                // Izin kamera belum diberikan, minta izin kamera
                requestCameraPermission()
            }
        }
    }

    private fun checkCameraPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        )
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Izin kamera diberikan, buka kamera di sini
                openCamera()
            } else {
                // Izin kamera ditolak, beri tahu pengguna
                Toast.makeText(requireContext(), "Izin kamera ditolak", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)

    }

    companion object {
        private const val CAMERA_REQUEST_CODE = 100
    }

    override fun onItemClicked(data: Artikel) {
        val articleLink = data.link
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(articleLink))
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        } else {
            // Handle the case where a web browser is not available
            Toast.makeText(requireContext(), "Web browser is not available", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            val capturedImage = data?.extras?.get("data") as Bitmap?

            capturedImage?.let {

                try {
                    // Initialize TFLite interpreter
                    initializeTFLiteInterpreter()

                    // Resize the captured image to the input shape of the model
                    val resizedImage = Bitmap.createScaledBitmap(it, 300, 150, true)

                    // Convert the resized image to a TensorImage
                    val tensorImage = TensorImage(DataType.FLOAT32)
                    tensorImage.load(resizedImage)

                    // Run inference and get the predicted label
                    val predictedLabel = runInference(tensorImage.tensorBuffer)

                    // Release TFLite resources
                    tfliteInterpreter.close()

                    // Proceed with the result as before
                    val intent = Intent(requireContext(), HasilScanActivity::class.java)
                    intent.putExtra("capturedImage", resizedImage)
                    // Set the result label as an extra in the intent
                    when (predictedLabel) {
                        0 -> intent.putExtra("resultLabel", "Miner")
                        1 -> intent.putExtra("resultLabel", "Sehat")
                        2 -> intent.putExtra("resultLabel", "Phoma")
                        3 -> intent.putExtra("resultLabel", "Rust")
                        else -> intent.putExtra("resultLabel", "Tidak Ada")
                    }
                    Log.d("ini Penyakit", predictedLabel.toString())
//                    intent.putExtra("resultData", predictedLabel) // Sending the predicted label as an extra
                    startActivity(intent)
                } catch (e: IOException) {
                    // Handle the exception
                    Toast.makeText(requireContext(), "Error loading model", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }


//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
//            val capturedImage = data?.extras?.get("data") as Bitmap?
//
//            capturedImage?.let {
//
//                try {
//                    // Initialize TFLite interpreter
//                    initializeTFLiteInterpreter()
//
//                    // Resize the captured image to the input shape of the model
//                    val resizedImage = Bitmap.createScaledBitmap(it, 300, 150, true)
//
//                    // Convert the resized image to a TensorImage
//                    val tensorImage = TensorImage(DataType.FLOAT32)
//                    tensorImage.load(resizedImage)
//
//                    // Run inference and get the result
//                    val outputFeature = runInference(tensorImage.tensorBuffer)
//
//                    // Release TFLite resources
//                    tfliteInterpreter.close()
//
//                    // Proceed with the result as before
//                    val intent = Intent(requireContext(), HasilScanActivity::class.java)
//                    intent.putExtra(
//                        "resultData",
//                        outputFeature.floatArray
//                    ) // Sending the scan result data as an extra
//                    Log.d("Ini hasil",outputFeature.toString())
//                    startActivity(intent)
//                } catch (e: IOException) {
//                    // Handle the exception
//                    Toast.makeText(requireContext(), "Error loading model", Toast.LENGTH_SHORT)
//                        .show()
//                }
//            }
//        }
//    }

}