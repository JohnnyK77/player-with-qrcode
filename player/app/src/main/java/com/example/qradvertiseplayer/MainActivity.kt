package com.example.qradvertiseplayer

import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.qradvertiseplayer.databinding.ActivityMainBinding
import com.example.qradvertiseplayer.vo.Advertise
import com.example.qradvertiseplayer.vo.ContentsVo
import com.google.android.exoplayer2.Player
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), Player.Listener {
    private val TAG = javaClass.simpleName.trim()
    private lateinit var binding: ActivityMainBinding
    private var adList: ArrayList<Advertise>? = null
    private var notPlayableAdMap = LinkedHashMap<String, Boolean>()
    private var adIdx = -1

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
        binding.epvMain.visibility = View.VISIBLE

        //get schedule.json
        val assetManager = resources.assets
        val inputStream = assetManager.open("schedule.json")
        val jsonString = inputStream.bufferedReader()
        //parsing
        val vo = Gson().fromJson(jsonString, ContentsVo::class.java)
        adList = vo.ad_list
        //playable ads map init
        if (adList?.isNotEmpty() == true) {
            for (adVo in adList!!) {
                notPlayableAdMap[adVo.id] = false
            }
        }
        startNextSchedule()
    }

    private fun startNextSchedule() {
        if (!adList.isNullOrEmpty()) {
            //index rolling
            adIdx++
            if (adIdx >= adList!!.size) {
                adIdx = 0
            }
            val vo = adList!![adIdx]
            val isPlayableAd = checkVerifyAdvertise(vo)
            notPlayableAdMap[vo.id] = !isPlayableAd
            if (isPlayableAd) {
                binding.tvVoInfo.text = vo.toString()
                setQrCodeImage(Gson().toJson(vo))
                binding.epvMain.setPlayMediaItem(vo.ad_url)
                binding.epvMain.start()
            } else {
                //check remain playable ad
                var isRemainPlayableAds = false
                for ((_, value) in notPlayableAdMap) {
                    if (!value) {
                        isRemainPlayableAds = true
                        break
                    }
                }
                if (isRemainPlayableAds) {
                    startNextSchedule()
                } else {
                    binding.tvVoInfo.text = "There are no playable ads"
                    releasePlayerView()
                }
            }
        }
    }

    private fun checkVerifyAdvertise(vo: Advertise): Boolean {
        val curTimeMillis = System.currentTimeMillis()
        //check date
        if (vo.date.isEmpty()) {
            return false
        }
        val adDate: Date = SimpleDateFormat("yyyy-MM-dd").parse(vo.date)
        if (curTimeMillis < adDate.time) {
            return false
        }

        //check ad in time
        if (vo.start_time.isEmpty() || vo.end_time.isEmpty()) {
            return false
        }
        var startTimeMillis = 0L
        var endTimeMillis = 0L
        val cal = Calendar.getInstance()
        val startTimeStrList = vo.start_time.split(":")
        if (startTimeStrList.size == 2) {
            cal.set(Calendar.HOUR_OF_DAY, startTimeStrList[0].toInt())
            cal.set(Calendar.MINUTE, startTimeStrList[1].toInt())
            cal.set(Calendar.SECOND, 0)
            startTimeMillis = cal.timeInMillis
        }
        val endTimeStrList = vo.end_time.split(":")
        if (endTimeStrList.size == 2) {
            cal.set(Calendar.HOUR_OF_DAY, endTimeStrList[0].toInt())
            cal.set(Calendar.MINUTE, endTimeStrList[1].toInt())
            cal.set(Calendar.SECOND, 59)
            endTimeMillis = cal.timeInMillis
        }
        if (curTimeMillis !in startTimeMillis..endTimeMillis) {
            return false
        }

        //check limit count
        if (vo.limit_count <= 0) {
            return false
        }
        return true
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

    private fun releasePlayerView(){
        binding.epvMain.visibility = View.INVISIBLE
        binding.epvMain.releasePlayer()
        binding.ivQrCode.setImageBitmap(null)
    }

    override fun onPlaybackStateChanged(state: Int) {
        Log.d(TAG, "state : $state")
        when (state) {
            Player.STATE_ENDED -> {
                Log.d(TAG, "end")
            }
            Player.STATE_READY -> {
                //limit until 30 seconds
                var remainTime = 5
                CoroutineScope(Dispatchers.Main).launch {
                    while (remainTime >= 0) {
                        binding.tvRemainTime.text = "$remainTime"
                        delay(1000)
                        remainTime--
                    }
                    if (binding.epvMain.isPlaying()) {
                        binding.epvMain.player?.stop()
                    }
                    if (!adList.isNullOrEmpty()) {
                        adList!![adIdx].limit_count--
                    }
                    startNextSchedule()
                }
            }
            Player.STATE_BUFFERING -> {}
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayerView()
    }
}