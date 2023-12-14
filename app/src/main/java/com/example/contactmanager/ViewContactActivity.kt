package com.example.contactmanager

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.contactmanager.databinding.ActivityViewContactBinding
import com.google.firebase.database.FirebaseDatabase


class ViewContactActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewContactBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // get device UUID
        val sharedPreferences = getSharedPreferences("DEVICE_SETTINGS", MODE_PRIVATE)
        val deviceId = sharedPreferences.getString("device_id", "")

        // unpack intent, and put dynamic data
        val id: String = intent.getStringExtra("id")!!
        val name: String = intent.getStringExtra("name")!!
        val avatarURL: String = intent.getStringExtra("avatarURL")!!
        val mobileNo: String = intent.getStringExtra("number")!!
        val ringtone: String = intent.getStringExtra("ringtone")!!
        val REQUEST_CODE = 123

        binding.tvContactId.text = id
        binding.tvContactName.text = name
        binding.tvContactNumber.text = "${mobileNo.substring(0,4)} ${mobileNo.substring(4,7)} ${mobileNo.substring(7)}"

        if (avatarURL == "default") {
            binding.ibContactAvatar.setImageURI(Uri.parse("android.resource://com.example.contactmanager/" + R.drawable.default_avatar))
        } else {
            Glide.with(this).load(avatarURL).into(binding.ibContactAvatar)
        }

        // set different listeners
        binding.ibBackButton.setOnClickListener {
            val mainActivityIntent = Intent(this, MainActivity::class.java)
            this.startActivity(mainActivityIntent)
        }

        binding.ibEditButton.setOnClickListener {
            val editContactActivityIntent = Intent(this, EditContactActivity::class.java)
            editContactActivityIntent.putExtra("id", id)
            editContactActivityIntent.putExtra("name", name)
            editContactActivityIntent.putExtra("avatarURL", avatarURL)
            editContactActivityIntent.putExtra("number", mobileNo)
            editContactActivityIntent.putExtra("ringtone", ringtone)
            this.startActivity(editContactActivityIntent)
        }

        binding.ibDeleteButton.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Confirmation")
            builder.setMessage("Delete the contact?")
            builder.setPositiveButton("Yes") { _, _ ->
                try {
                    val database = FirebaseDatabase.getInstance()
                    val contactRef = database.getReference("contacts").child(deviceId!!)
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
            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
        }

        binding.ibCallButton.setOnClickListener {
            val callIntent: Intent = Uri.parse("tel:$mobileNo").let { number ->
                Intent(Intent.ACTION_CALL, number)
            }

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CALL_PHONE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf<String>(Manifest.permission.CALL_PHONE),
                    REQUEST_CODE
                )
            } else {
                val ringtoneURI = Uri.parse(ringtone)
                val localRingtone = RingtoneManager.getRingtone(this, ringtoneURI)

                // Play the ringtone after starting the call
                localRingtone.play()

                // Schedule a task to stop the ringtone after 10 seconds
                val handler = Handler()
                handler.postDelayed({
                    localRingtone.stop()
                }, 15000)

                startActivity(callIntent)
            }
        }
    }
}