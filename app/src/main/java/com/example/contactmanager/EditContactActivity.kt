package com.example.contactmanager

import android.app.Activity
import android.content.Intent
import android.media.RingtoneManager
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
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.UUID
import java.util.regex.Pattern

class EditContactActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditContactBinding
    private lateinit var imageURI: Uri
    private lateinit var ringtoneURI: Uri
    private var avatarChanged: Boolean = false
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uri != null) {
            val fileSize = getFileSize(uri)
            val maxSizeBytes = 1 * 1024 * 1024 // 5MB

            if (fileSize > maxSizeBytes) {
                Toast.makeText(this, "Choose an image that is less than 1MB", Toast.LENGTH_SHORT).show()
                return@registerForActivityResult
            }

            binding.ibEditContactAvatar.setImageURI(uri)
            imageURI = uri
            avatarChanged = true
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }
    private val PICK_RINGTONE_REQUEST_CODE = 123

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
        val ringtone: String = intent.getStringExtra("ringtone")!!

        if (ringtone != "") {
            ringtoneURI = Uri.parse(ringtone)
            val localRingtone = RingtoneManager.getRingtone(this, ringtoneURI)
            val ringtoneTitle = localRingtone.getTitle(this)
            binding.tvRingtoneTitle.text = ringtoneTitle
        } else {
            ringtoneURI = Uri.parse("")
            binding.tvRingtoneTitle.text = "Default ringtone"
        }

        binding.tvContactId.text = id
        binding.etEditContactName.setText(name)
        binding.etEditContactMobileNumber.setText(mobileNo)

        if (avatarURL == "default") {
            imageURI = Uri.parse("default")
            binding.ibEditContactAvatar.setImageURI(Uri.parse("android.resource://com.example.contactmanager/" + R.drawable.default_avatar))
        } else {
            imageURI = Uri.parse(avatarURL)
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

        binding.tvChangeRingtone.setOnClickListener {
            showRingtonePicker()
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
                val editedContact = Contact(name = name, mobileNo = mobileNo, avatarURL = "default", ringtone = ringtoneURI.toString())

                val database = FirebaseDatabase.getInstance()
                val updateRef = database.getReference("contacts/${deviceId}/$id")
                val updatedContact = HashMap<String, Any>()
                updatedContact["name"] = editedContact.name
                updatedContact["mobileNo"] = editedContact.mobileNo
                updatedContact["ringtone"] = editedContact.ringtone
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

                        val editedContact = Contact(name = name, mobileNo = mobileNo, avatarURL = imageURL, ringtone = ringtoneURI.toString())

                        val updateRef = FirebaseDatabase.getInstance().getReference("contacts/${deviceId}/$id")
                        val updatedContact = HashMap<String, Any>()
                        updatedContact["name"] = editedContact.name
                        updatedContact["mobileNo"] = editedContact.mobileNo
                        updatedContact["avatarURL"] = editedContact.avatarURL
                        updatedContact["ringtone"] = editedContact.ringtone

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

    private fun getFileSize(uri: Uri): Long {
        var fileSize: Long = 0
        var inputStream: InputStream? = null

        try {
            inputStream = contentResolver.openInputStream(uri)
            fileSize = inputStream?.available()?.toLong() ?: 0
        } catch (e: IOException) {
            // Handle the exception
            e.printStackTrace()
        } finally {
            try {
                inputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        return fileSize
    }

    private fun showRingtonePicker() {
        val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Ringtone")
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE))

        startActivityForResult(intent, PICK_RINGTONE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_RINGTONE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val uri = data?.getParcelableExtra<android.net.Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            if (uri != null) {
                ringtoneURI = uri
                val ringtone = RingtoneManager.getRingtone(this, uri)
                val ringtoneTitle = ringtone.getTitle(this)
                binding.tvRingtoneTitle.text = ringtoneTitle
            }
        }
    }
}