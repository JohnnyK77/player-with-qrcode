package com.example.qradvertiseplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.qradvertiseplayer.databinding.ActivityMainBinding
import com.example.qradvertiseplayer.vo.Advertise
import com.example.qradvertiseplayer.vo.ContentsVo
import com.google.android.exoplayer2.Player
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder

class MainActivity : AppCompatActivity(), Player.Listener {
    private val TAG = javaClass.simpleName.trim()
    private lateinit var binding: ActivityMainBinding
    private var adList: ArrayList<Advertise>? = null
    private var adIdx = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        init()
    }

    private fun init() {
        //player init
        binding.epvMain.init(this)
        //get schedule.json
        val assetManager = resources.assets
        val inputStream = assetManager.open("schedule.json")
        val jsonString = inputStream.bufferedReader()
        //parsing
        val vo = Gson().fromJson(jsonString, ContentsVo::class.java)
        adList = vo.ad_list
        setNextSchedule()
    }

    private fun setNextSchedule() {
        if (!adList.isNullOrEmpty()) {
            if (adIdx >= adList!!.size) {
                adIdx = 0
            }
            startPlay(adList!![adIdx])
            adIdx++
        }
    }

    private fun startPlay(vo: Advertise) {
        setQrCodeImage(Gson().toJson(vo))
        binding.epvMain.setPlayMediaItem(vo.ad_url)
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
                setNextSchedule()
            }
            Player.STATE_READY -> {}
            Player.STATE_BUFFERING -> {}
        }
    }
}