package com.example.contactmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    private lateinit var contactAdapter: ContactAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        contactAdapter = ContactAdapter(mutableListOf(
            Contact("John Daniel", "09271779395", false)
        ))
    }
}
