package com.example.contactmanager

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.contactmanager.databinding.ContactItemBinding
import com.google.firebase.database.FirebaseDatabase

// accepts a list of contacts that will be rendered on the recyclerview,
// as well as context object, since an adapter class would not have a "this" context
class ContactAdapter(
    private var contacts: MutableList<Contact>,
    private var deviceId: String,
    private var context: Context
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = ContactItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ContactViewHolder(binding)
    }

    // bind contact list to the contact view holder (contact_item.xml)
    //
    // also include listeners for each item so view, delete, and edit will be possible
    override fun onBindViewHolder(holder: ContactViewHolder, i: Int) {
        val currentContact = contacts[i]
        holder.binding.tvContactId.text = currentContact.id
        holder.binding.tvContactTitle.text = currentContact.name
        holder.binding.cbFavorite.isChecked = currentContact.isFavorite

        // upon title click, redirect to view contact activity and
        // include the id, name, and number in the intent
        holder.binding.tvContactTitle.setOnClickListener {
            val viewContactActivityIntent = Intent(context, ViewContactActivity::class.java)
            viewContactActivityIntent.putExtra("id", currentContact.id)
            viewContactActivityIntent.putExtra("name", currentContact.name)
            viewContactActivityIntent.putExtra("avatarURL", currentContact.avatarURL)
            viewContactActivityIntent.putExtra("number", currentContact.mobileNo)
            context.startActivity(viewContactActivityIntent)
        }

        // upon checking the checkbox of the contact, update the "favorite" field in the
        // firebase database
        holder.binding.cbFavorite.setOnCheckedChangeListener { _, isChecked ->
            try {
                val database = FirebaseDatabase.getInstance()
                val updateRef = database.getReference("contacts/${deviceId}/${currentContact.id}")
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




















