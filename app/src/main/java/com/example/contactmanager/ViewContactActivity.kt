package com.example.contactmanager

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
            val editContactActivityIntent = Intent(this, EditContactActivity::class.java)
            editContactActivityIntent.putExtra("id", id)
            editContactActivityIntent.putExtra("name", name)
            editContactActivityIntent.putExtra("number", mobileNo)
            this.startActivity(editContactActivityIntent)
        }

        binding.ibDeleteButton.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Confirmation")
            builder.setMessage("Delete the contact?")
            builder.setPositiveButton("Yes") { _, _ ->
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
            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
        }

        binding.ibCallButton.setOnClickListener {
            TODO("implement a call function using native android function, " +
                    "if too difficult, just abandon the feature")
        }
    }
}