package com.example.contactmanager

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.contactmanager.databinding.ActivityEditContactBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID
import java.util.regex.Pattern

class EditContactActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditContactBinding
    private lateinit var imageURI: Uri
    private var avatarChanged: Boolean = false
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uri != null) {
            binding.ibEditContactAvatar.setImageURI(uri)
            imageURI = uri
            avatarChanged = true
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // get device UUID
        val sharedPreferences = getSharedPreferences("DEVICE_SETTINGS", MODE_PRIVATE)
        val deviceId = sharedPreferences.getString("device_id", "")

        // put dynamic data
        val id: String = intent.getStringExtra("id")!!
        val name: String = intent.getStringExtra("name")!!
        val avatarURL: String = intent.getStringExtra("avatarURL")!!
        val mobileNo: String = intent.getStringExtra("number")!!

        binding.tvContactId.text = id
        binding.etEditContactName.setText(name)
        binding.etEditContactMobileNumber.setText(mobileNo)

        if (avatarURL == "default") {
            imageURI = Uri.parse("default")
            binding.ibEditContactAvatar.setImageURI(Uri.parse("android.resource://com.example.contactmanager/" + R.drawable.default_avatar))
        } else {
            Glide.with(this).load(avatarURL).into(binding.ibEditContactAvatar)
        }

        // set back button listener
        binding.ibBackButton.setOnClickListener {
            val mainActivityIntent = Intent(this, MainActivity::class.java)
            this.startActivity(mainActivityIntent)
        }

        binding.ibEditContactAvatar.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        // on submit click, validate input, then update it in Firebase by referencing its
        // id in database.getReference("contacts/$id")
        binding.ibEditContactButton.setOnClickListener {
            val name = binding.etEditContactName.text.toString().trim()
            val mobileNo = binding.etEditContactMobileNumber.text.toString().trim()

            // check if fields are blank
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

            // if default avatar, or avatar did not change, don't upload anything
            if (imageURI.toString() == "default" || !avatarChanged) {
                val editedContact = Contact(name = name, mobileNo = mobileNo, avatarURL = "default")

                val database = FirebaseDatabase.getInstance()
                val updateRef = database.getReference("contacts/${deviceId}/$id")
                val updatedContact = HashMap<String, Any>()
                updatedContact["name"] = editedContact.name
                updatedContact["mobileNo"] = editedContact.mobileNo
                // We do not update the avatarURL

                updateRef.updateChildren(updatedContact).addOnSuccessListener {
                    Toast.makeText(this, "Changes saved", Toast.LENGTH_SHORT).show()

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

                        val editedContact = Contact(name = name, mobileNo = mobileNo, avatarURL = imageURL)

                        val updateRef = FirebaseDatabase.getInstance().getReference("contacts/${deviceId}/$id")
                        val updatedContact = HashMap<String, Any>()
                        updatedContact["name"] = editedContact.name
                        updatedContact["mobileNo"] = editedContact.mobileNo
                        updatedContact["avatarURL"] = editedContact.avatarURL

                        updateRef.updateChildren(updatedContact).addOnSuccessListener {
                            Toast.makeText(this, "Changes saved", Toast.LENGTH_SHORT).show()

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