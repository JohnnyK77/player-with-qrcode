package com.example.qradvertiseplayer.vo

class ContentsVo {
    val ad_list: ArrayList<Advertise>? = null
}

data class Advertise(
    val id: String,
    val type: String,
    val date: String,
    val start_time: String,
    val end_time: String,
    var limit_count: Int,
    val ad_url: String
)