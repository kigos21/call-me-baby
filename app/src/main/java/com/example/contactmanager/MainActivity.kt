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
    private lateinit var favorites: MutableList<Contact>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        contacts = mutableListOf()
        favorites = mutableListOf()

        // read data from database, and pass to recycler view adapter
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val contactsRef = database.getReference("contacts").orderByChild("name")
        contactsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // whenever data changes in the database, clear these lists and re-populate
                contacts.clear()
                favorites.clear()

                for (contact in snapshot.children) {
                    val id = contact.key!!

                    // getting value from snapshot returns <Type>?, so we non-null assert it
                    // using !! operator
                    val name = contact.child("name").getValue(String::class.java)!!
                    val mobileNo = contact.child("mobileNo").getValue(String::class.java)!!
                    val isFavorite = contact.child("favorite").getValue(Boolean::class.java)!!
                    val contact = Contact(id, name, mobileNo, isFavorite)

                    contacts.add(contact)
                    if (isFavorite) {
                        favorites.add(contact)
                    }
                }

                // pass the general contacts in the adapter if checkbox is not checked,
                // else pass the favorites
                contactAdapter = if (binding.cbShowFavorites.isChecked) {
                    ContactAdapter(favorites, this@MainActivity)
                } else {
                    ContactAdapter(contacts, this@MainActivity)
                }

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
            contactAdapter = if (isChecked) {
                ContactAdapter(favorites, this@MainActivity)
            } else {
                ContactAdapter(contacts, this@MainActivity)
            }

            binding.rvContactItems.adapter = contactAdapter
        }
    }
}
