package com.example.contactmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.os.IResultReceiver._Parcel
import android.text.Layout
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contactmanager.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var contactAdapter: ContactAdapter
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        contactAdapter = ContactAdapter(mutableListOf())

        binding.rvContactItems.adapter = contactAdapter
        binding.rvContactItems.layoutManager = LinearLayoutManager(this)

        binding.ibCreateLink.setOnClickListener {
            val newContact = Contact(binding.etNewContactName.text.toString(), binding.etNewContactNumber.text.toString())
            if (newContact.name.isNotBlank() && newContact.mobileNo.isNotBlank()) {
                contactAdapter.addContact(newContact)
                binding.etNewContactName.text.clear()
                binding.etNewContactNumber.text.clear()
            }
        }

        binding.cbShowFavorites.setOnCheckedChangeListener { _, isChecked ->
            contactAdapter.filterFavorites(isChecked)
        }
    }
}
