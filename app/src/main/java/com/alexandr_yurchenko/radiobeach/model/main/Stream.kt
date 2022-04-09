package com.alexandr_yurchenko.radiobeach.model.main


import com.google.gson.annotations.SerializedName

data class Stream(
    @SerializedName("format")
    val format: String = "",
    @SerializedName("kbps")
    val kbps: Int = 0,
    @SerializedName("mount")
    val mount: String = "",
    @SerializedName("off")
    val off: Int = 0,
    @SerializedName("url")
    val url: String = "",
    @SerializedName("user")
    val user: String = ""
)