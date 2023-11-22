package com.example.contactmanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.contactmanager.databinding.ActivityViewContactBinding
import com.google.firebase.database.FirebaseDatabase

class ViewContactActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewContactBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // put dynamic data
        val id: String = intent.getStringExtra("id")!!
        val name: String = intent.getStringExtra("name")!!
        val mobileNo: String = intent.getStringExtra("number")!!

        binding.tvContactId.text = id
        binding.tvContactName.text = name
        binding.tvContactNumber.text = mobileNo

        // set different listeners
        binding.ibBackButton.setOnClickListener {
            val mainActivityIntent = Intent(this, MainActivity::class.java)
            this.startActivity(mainActivityIntent)
        }

        binding.ibEditButton.setOnClickListener {
            // TODO: redirect to edit page, package intent and data with the intent, and render
            // it in intent activity page
        }

        binding.ibDeleteButton.setOnClickListener {
            try {
                val database = FirebaseDatabase.getInstance()
                val contactRef = database.getReference("contacts")
                contactRef.child(id).removeValue().addOnSuccessListener {
                    Toast.makeText(this, "Contact removed", Toast.LENGTH_SHORT).show()

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