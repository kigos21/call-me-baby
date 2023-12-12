package com.example.contactmanager

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.contactmanager.databinding.ActivityNewContactBinding
import com.google.firebase.database.FirebaseDatabase
import java.util.regex.Pattern

class NewContactActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewContactBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // get device UUID
        val sharedPreferences = getSharedPreferences("DEVICE_SETTINGS", MODE_PRIVATE)
        val deviceId = sharedPreferences.getString("device_id", "")

        binding.ibBackButton.setOnClickListener {
            val mainActivityIntent = Intent(this, MainActivity::class.java)
            this.startActivity(mainActivityIntent)
        }

        binding.ibCreateContactButton.setOnClickListener {
            val newContact = Contact(name = binding.etNewContactName.text.toString().trim(), mobileNo = binding.etNewContactMobileNumber.text.toString().trim())

            // check if fields are blank
            if (newContact.name.isBlank() || newContact.mobileNo.isBlank()) {
                Toast.makeText(this, "Fill in the fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // check regex and format
            val pattern = Pattern.compile("^\\d{11}\$")
            if (!newContact.mobileNo.matches(pattern.toRegex())) {
                Toast.makeText(this, "Contact number must match this pattern: 09123456789", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            try {
                val database = FirebaseDatabase.getInstance()
                val contactRef = database.getReference("contacts").child(deviceId!!)
                contactRef.push().setValue(newContact).addOnSuccessListener {
                    Toast.makeText(this, "Contact added", Toast.LENGTH_SHORT).show()

                    // redirect to main activity
                    val mainActivityIntent = Intent(this, MainActivity::class.java)
                    this.startActivity(mainActivityIntent)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}