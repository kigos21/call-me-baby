package com.example.contactmanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import com.example.contactmanager.databinding.ActivityEditContactBinding
import com.google.firebase.database.FirebaseDatabase
import java.util.regex.Pattern

class EditContactActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditContactBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // put dynamic data
        val id: String = intent.getStringExtra("id")!!
        val name: String = intent.getStringExtra("name")!!
        val mobileNo: String = intent.getStringExtra("number")!!

        binding.tvContactId.text = id
        binding.etEditContactName.setText(name)
        binding.etEditContactMobileNumber.setText(mobileNo)

        // set back button listener
        binding.ibBackButton.setOnClickListener {
            val mainActivityIntent = Intent(this, MainActivity::class.java)
            this.startActivity(mainActivityIntent)
        }

        // on submit click, validate input, then update it in Firebase by referencing its
        // id in database.getReference("contacts/$id")
        binding.ibEditContactButton.setOnClickListener {
            val editedContact = Contact(name = binding.etEditContactName.text.toString().trim(), mobileNo = binding.etEditContactMobileNumber.text.toString().trim())

            // check if fields are blank
            if (editedContact.name.isBlank() || editedContact.mobileNo.isBlank()) {
                Toast.makeText(this, "Fill in the fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // check regex and format
            val pattern = Pattern.compile("^\\d{4} \\d{3} \\d{4}$")
            if (!editedContact.mobileNo.matches(pattern.toRegex())) {
                Toast.makeText(this, "Contact number must match this pattern: 0912 345 6789", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            try {
                val database = FirebaseDatabase.getInstance()
                val updateRef = database.getReference("contacts/$id")
                val updatedContact = HashMap<String, Any>()
                updatedContact["name"] = editedContact.name
                updatedContact["mobileNo"] = editedContact.mobileNo
                updateRef.updateChildren(updatedContact).addOnSuccessListener {
                    Toast.makeText(this, "Changes saved", Toast.LENGTH_SHORT).show()

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