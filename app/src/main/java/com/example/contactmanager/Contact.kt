package com.example.contactmanager

data class Contact(
    var name: String,
    var mobileNo: String,
    var isFavorite: Boolean = false,
)