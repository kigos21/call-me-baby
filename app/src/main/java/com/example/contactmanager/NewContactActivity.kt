package com.example.contactmanager

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.contactmanager.databinding.ActivityNewContactBinding
import com.google.firebase.database.FirebaseDatabase
import java.io.File
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
        val SELECT_PICTURE = 200

        binding.ibBackButton.setOnClickListener {
            val mainActivityIntent = Intent(this, MainActivity::class.java)
            this.startActivity(mainActivityIntent)
        }

        val img: ImageView = findViewById(R.id.ivNewContactAvatar)

        fun getFileSize(uri: Uri): Long {
            val file = File(uri.path)
            return file.length()
        }

        val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val selectedImageUri: Uri? = result.data?.data

                selectedImageUri?.let {uri ->
                    val fileSize = getFileSize(uri)
                    val maxSizeBytes = 5 * 1024 * 1024 //5MB

                    if (fileSize <= maxSizeBytes) {
                        Glide.with(this).load(uri).into(img)
                    } else {
                        Toast.makeText(this, "Choose an image that is less than 5MB", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.ivNewContactAvatar.setOnClickListener {
            val getImageIntent = Intent(Intent.ACTION_PICK)
            getImageIntent.type = "image/*"

            pickImageLauncher.launch(getImageIntent)
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