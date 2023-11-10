package com.example.contactmanager

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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

        binding.ibCreateContactButton.setOnClickListener {
            val newContact = Contact(binding.etNewContactName.text.toString(), binding.etNewContactMobileNumber.text.toString())
            if (newContact.name.isNotBlank() && newContact.mobileNo.isNotBlank()) {
                // implement logic to add in list, then send back to main activity for recycler view injection, HOW TO EVEN DO THAT
                binding.etNewContactName.text.clear()
                binding.etNewContactMobileNumber.text.clear()

                // redirect back
                val mainActivityIntent = Intent(this, MainActivity::class.java)
                this.startActivity(mainActivityIntent)
            } else {
                Toast.makeText(this, "Fill in the fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}