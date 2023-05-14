package com.kreg.mlkitcameraapp.cameraActivity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraProvider
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.ListenableFuture
import com.kreg.mlkitcameraapp.R
import com.kreg.mlkitcameraapp.services.ImageAnalyzer

class CameraActivity: AppCompatActivity() {
    private var cameraProvider: ProcessCameraProvider? = null
    private lateinit var cameraFuture: ListenableFuture<ProcessCameraProvider>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        cameraFuture = ProcessCameraProvider.getInstance(this)
        cameraFuture.addListener({
            cameraProvider = cameraFuture.get()
            bindPreview()
        } , ContextCompat.getMainExecutor(this))
    }
    private fun bindPreview(){
        val preview = Preview.Builder().build()
        val previewSurface = findViewById<PreviewView>(R.id.preview_view)
        preview.setSurfaceProvider(previewSurface.surfaceProvider)

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        val imageAnalyzer = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        imageAnalyzer.setAnalyzer(
            mainExecutor,
            ImageAnalyzer()
        )
        cameraProvider?.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)
    }
}