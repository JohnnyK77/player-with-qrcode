package com.example.qradvertiseplayer.player

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerView

class ExoPlayerView : StyledPlayerView {
    private val TAG = javaClass.simpleName.trim()
    private var mPlayer: ExoPlayer? = null
    private var mListener: Player.Listener? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun init(listener: Player.Listener) {
        mListener = listener
        //영상만 나오게 player 설정
        setShutterBackgroundColor(Color.TRANSPARENT)
        useController = false
        mPlayer = ExoPlayer.Builder(context).build()
        this.player = mPlayer
        if (mListener != null) {
            mPlayer!!.addListener(mListener!!)
        }
    }

    fun setPlayMediaItem(url: String) {
        if (mPlayer != null) {
            val mediaItem = MediaItem.fromUri(url)
            mPlayer!!.setMediaItem(mediaItem)
            mPlayer!!.prepare()
        }
    }

    fun isPlaying(): Boolean {
        return mPlayer?.isPlaying == true
    }

    fun pause() {
        if (isPlaying()) {
            Log.d(TAG, "pause called")
            mPlayer?.pause()
        }
    }

    fun start() {
        if (!isPlaying()) {
            Log.d(TAG, "start called")
            mPlayer?.play()
        }
    }

    //동영상 시간 설정
    fun seekTo(position: Int) {
        mPlayer?.seekTo(position.toLong())
    }

    //동영상 해제
    fun releasePlayer() {
        Log.d(TAG, "releasePlayer called!")
        mPlayer?.stop();
        mPlayer?.release()
        mPlayer = null
    }

    fun seekToEnd() {
        mPlayer?.seekTo(mPlayer!!.duration)
//        mPlayer.stop(true);
    }
}