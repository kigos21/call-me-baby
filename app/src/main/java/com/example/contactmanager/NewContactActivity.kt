package com.example.contactmanager

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.contactmanager.databinding.ActivityNewContactBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID
import java.util.regex.Pattern

class NewContactActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewContactBinding
    private lateinit var imageURI: Uri
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uri != null) {
            binding.ibNewContactAvatar.setImageURI(uri)
            imageURI = uri
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // get device UUID
        val sharedPreferences = getSharedPreferences("DEVICE_SETTINGS", MODE_PRIVATE)
        val deviceId = sharedPreferences.getString("device_id", "")

        // set default avatar as the imageURI, if not changed, then default avatar is uploaded
        imageURI = Uri.parse("default")

        binding.ibBackButton.setOnClickListener {
            val mainActivityIntent = Intent(this, MainActivity::class.java)
            this.startActivity(mainActivityIntent)
        }

        binding.ibNewContactAvatar.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.ibCreateContactButton.setOnClickListener {
            val name = binding.etNewContactName.text.toString().trim()
            val mobileNo = binding.etNewContactMobileNumber.text.toString().trim()

            if (name.isBlank() || mobileNo.isBlank()) {
                Toast.makeText(this, "Fill in the fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // check regex and format
            val pattern = Pattern.compile("^\\d{11}\$")
            if (!mobileNo.matches(pattern.toRegex())) {
                Toast.makeText(this, "Contact number must match this pattern: 09123456789", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // if default avatar, don't upload anything
            if (imageURI.toString() == "default") {
                val newContact = Contact(name = name, mobileNo = mobileNo, avatarURL = "default")
                val database = FirebaseDatabase.getInstance()
                val contactRef = database.getReference("contacts").child(deviceId!!)

                contactRef.push().setValue(newContact).addOnSuccessListener {
                    Toast.makeText(this, "Contact added", Toast.LENGTH_SHORT).show()

                    // redirect to main activity
                    val mainActivityIntent = Intent(this, MainActivity::class.java)
                    this.startActivity(mainActivityIntent)
                }
                return@setOnClickListener
            }

            // Upload photo to firebase storage
            try {
                // Get a reference to the Firebase Storage location using the contact ID
                val storageRef = FirebaseStorage.getInstance().getReference("avatar").child(UUID.randomUUID().toString())

                // Upload the image to Firebase Storage
                val uploadTask = storageRef.putFile(imageURI)
                Toast.makeText(this, "Saving, please wait...", Toast.LENGTH_LONG).show()

                // Listen for success or failure of the upload
                uploadTask.addOnSuccessListener {

                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        val imageURL = uri.toString()
                        // Save the imageUrl in the Realtime Database under the contact node

                        val newContact = Contact(name = name, mobileNo = mobileNo, avatarURL = imageURL)
                        val database = FirebaseDatabase.getInstance()
                        val contactRef = database.getReference("contacts").child(deviceId!!)

                        contactRef.push().setValue(newContact).addOnSuccessListener {
                            Toast.makeText(this, "Contact added", Toast.LENGTH_SHORT).show()

                            // redirect to main activity
                            val mainActivityIntent = Intent(this, MainActivity::class.java)
                            this.startActivity(mainActivityIntent)
                        }
                    }
                }.addOnFailureListener { e ->
                    Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
                    Log.e("jd", e.toString())
                    return@addOnFailureListener
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}