package com.example.contactmanager

data class Contact(
    var id: String = "",
    var name: String,
    var mobileNo: String,
    var isFavorite: Boolean = false,
)