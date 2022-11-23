package com.example.qradvertiseplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.qradvertiseplayer.databinding.ActivityMainBinding
import com.google.android.exoplayer2.Player
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder

class MainActivity : AppCompatActivity(), Player.Listener {
    private val TAG = javaClass.simpleName.trim()
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setPlayer()
    }

    private fun setPlayer() {
        binding.epvMain.init(this)
    }

    private fun startPlay(url: String) {
        binding.epvMain.setPlayMediaItem(url)
        binding.epvMain.start()
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

    override fun onPlaybackStateChanged(state: Int) {
        Log.d(TAG, "state : $state")
        when (state) {
            Player.STATE_ENDED -> {
                Log.d(TAG, "end")

            }
            Player.STATE_READY -> {}
            Player.STATE_BUFFERING -> {}
        }
    }
}