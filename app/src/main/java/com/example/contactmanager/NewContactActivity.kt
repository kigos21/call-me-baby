package com.example.contactmanager

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.contactmanager.databinding.ActivityNewContactBinding

class NewContactActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewContactBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityNewContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ibBackButton.setOnClickListener {
            val mainActivityIntent = Intent(this, MainActivity::class.java)
            this.startActivity(mainActivityIntent)
        }
    }
}