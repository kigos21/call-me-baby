package com.example.contactmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.contactmanager.databinding.ActivityViewContactBinding

class ViewContactActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewContactBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewContactBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}