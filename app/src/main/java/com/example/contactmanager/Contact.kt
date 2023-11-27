package com.example.contactmanager

data class Contact(
    var id: String = "", // useless in terms of record on Firebase, but is important in view binding in ContactAdapter to link to dynamic pages
    var name: String,
    var mobileNo: String,
    var isFavorite: Boolean = false,
)