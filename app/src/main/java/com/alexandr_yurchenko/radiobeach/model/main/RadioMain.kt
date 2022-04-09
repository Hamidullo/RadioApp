package com.alexandr_yurchenko.radiobeach.model.main


import com.google.gson.annotations.SerializedName

data class RadioMain(
    @SerializedName("artist")
    val artist: String = "",
    @SerializedName("country")
    val country: String = "",
    @SerializedName("djname")
    val djname: String = "",
    @SerializedName("enabletable")
    val enabletable: Int = 0,
    @SerializedName("favorites")
    val favorites: Int = 0,
    @SerializedName("genre")
    val genre: String = "",
    @SerializedName("img")
    val img: String = "",
    @SerializedName("kbps")
    val kbps: Int = 0,
    @SerializedName("limit")
    val limit: Int = 0,
    @SerializedName("listeners")
    val listeners: Int = 0,
    @SerializedName("logo")
    val logo: String = "",
    @SerializedName("nextsongs")
    val nextsongs: List<String> = listOf(),
    @SerializedName("online")
    val online: Int = 0,
    @SerializedName("playlist")
    val playlist: String = "",
    @SerializedName("plisteners")
    val plisteners: Int = 0,
    @SerializedName("port")
    val port: String = "",
    @SerializedName("rank")
    val rank: List<List<Any>> = listOf(),
    @SerializedName("server")
    val server: String = "",
    @SerializedName("song")
    val song: String = "",
    @SerializedName("songs")
    val songs: List<List<String>> = listOf(),
    @SerializedName("songtitle")
    val songtitle: String = "",
    @SerializedName("streamname")
    val streamname: String = "",
    @SerializedName("streams")
    val streams: List<Stream> = listOf(),
    @SerializedName("title")
    val title: String = "",
    @SerializedName("turntable")
    val turntable: Int = 0,
    @SerializedName("url")
    val url: String = ""
)