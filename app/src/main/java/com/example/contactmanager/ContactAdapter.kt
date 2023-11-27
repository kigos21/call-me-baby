package com.example.contactmanager

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.contactmanager.databinding.ContactItemBinding
import com.google.firebase.database.FirebaseDatabase

class ContactAdapter(
    private var contacts: MutableList<Contact>,
    private var context: Context
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = ContactItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ContactViewHolder(binding);
    }

    override fun onBindViewHolder(holder: ContactViewHolder, i: Int) {
        val currentContact = contacts[i]
        holder.binding.tvContactId.text = currentContact.id
        holder.binding.tvContactTitle.text = currentContact.name
        holder.binding.cbFavorite.isChecked = currentContact.isFavorite
        holder.binding.tvContactTitle.setOnClickListener {
            val viewContactActivityIntent = Intent(context, ViewContactActivity::class.java)
            viewContactActivityIntent.putExtra("id", currentContact.id)
            viewContactActivityIntent.putExtra("name", currentContact.name)
            viewContactActivityIntent.putExtra("number", currentContact.mobileNo)
            context.startActivity(viewContactActivityIntent)
        }

        holder.binding.cbFavorite.setOnCheckedChangeListener { _, isChecked ->
            try {
                val database = FirebaseDatabase.getInstance()
                val updateRef = database.getReference("contacts/${currentContact.id}")
                val updatedContact = HashMap<String, Any>()
                updatedContact["favorite"] = isChecked
                updateRef.updateChildren(updatedContact)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    class ContactViewHolder(val binding: ContactItemBinding) : RecyclerView.ViewHolder(binding.root) { }
}




















