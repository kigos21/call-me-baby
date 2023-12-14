package com.example.contactmanager

import android.net.Uri

data class Contact(
    var id: String = "", // useless in terms of record on Firebase, but is important in view binding in ContactAdapter to link to dynamic pages
    var name: String,
    var avatarURL: String,
    var mobileNo: String,
    var isFavorite: Boolean = false,
    var ringtone: String = ""
)