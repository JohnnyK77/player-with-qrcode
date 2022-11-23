package com.example.qradvertiseplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.qradvertiseplayer.databinding.ActivityMainBinding
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    private fun setQrCodeImage(content: String) {
        val barcodeEncoder = BarcodeEncoder()
        val bitmap = barcodeEncoder.encodeBitmap(
            content,
            BarcodeFormat.QR_CODE,
            binding.ivQrCode.layoutParams.width,
            binding.ivQrCode.layoutParams.height
        )
        binding.ivQrCode.setImageBitmap(bitmap)
    }
}