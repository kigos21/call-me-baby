package com.example.contactmanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contactmanager.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var contactAdapter: ContactAdapter
    private lateinit var contacts: MutableList<Contact>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        contacts = mutableListOf()

        // read data from database, and pass to recycler view adapter
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val contactsRef: DatabaseReference = database.getReference("contacts")
        contactsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                contacts.clear()

                for (contact in snapshot.children) {
                    val id = contact.key!!

                    // getting value from snapshot returns <Type>?, so we non-null assert it
                    // using !! operator
                    val name = contact.child("name").getValue(String::class.java)!!
                    val mobileNo = contact.child("mobileNo").getValue(String::class.java)!!
                    val isFavorite = contact.child("favorite").getValue(Boolean::class.java)!!

                    contacts.add(Contact(id, name, mobileNo, isFavorite))
                }

                contactAdapter = ContactAdapter(contacts, this@MainActivity)

                binding.rvContactItems.adapter = contactAdapter
                binding.rvContactItems.layoutManager = LinearLayoutManager(this@MainActivity)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error: ${error.message}")
            }
        })

        binding.ibCreateLink.setOnClickListener {
            val newContactActivityIntent = Intent(this, NewContactActivity::class.java)
            this.startActivity(newContactActivityIntent)
        }

        binding.cbShowFavorites.setOnCheckedChangeListener { _, isChecked ->
            // TODO: filter data according to favorites
        }
    }
}
