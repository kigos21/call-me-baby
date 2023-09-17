package com.example.contactmanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.contactmanager.databinding.ContactItemBinding

class ContactAdapter(
    private var contacts: MutableList<Contact>
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    private lateinit var favorites: MutableList<Contact>
    private lateinit var unfilteredContacts: MutableList<Contact>
    class ContactViewHolder(val binding: ContactItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvContactTitle = binding.tvContactTitle
        val cbFavorite = binding.cbFavorite
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = ContactItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ContactViewHolder(binding);
//        return ContactViewHolder(
//            LayoutInflater.from(parent.context).inflate(
//                R.layout.contact_item,
//                parent,
//                false,
//            )
//        )
    }

    fun addContact(newContact: Contact) {
        contacts.add(newContact)
        notifyItemInserted(contacts.size - 1)
    }

    fun deleteContact(contact: Contact) {
        TODO("Delete the the contact (by reference in memory??)")
    }

    fun filterFavorites(isFiltering: Boolean) {
        if (isFiltering) {
            // save a copy of contacts somewhere
            unfilteredContacts = contacts

            // create the filtered contacts, and load it to the adapter
            favorites = contacts.filter { contact: Contact -> contact.isFavorite }.toMutableList()
            contacts = favorites
        } else {
            // filter is out, load the original contact list
            contacts = unfilteredContacts
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    override fun onBindViewHolder(holder: ContactViewHolder, i: Int) {
        val currentContact = contacts[i]
//        holder.binding.apply {
//            tvContactTitle.text = currentContact.name
//            cbFavorite.isChecked = currentContact.isFavorite
//            cbFavorite.setOnCheckedChangeListener { _, isChecked ->
//                currentContact.isFavorite = isChecked
//            }
//        }
        holder.binding.tvContactTitle.text = currentContact.name
        holder.binding.cbFavorite.isChecked = currentContact.isFavorite
        holder.binding.cbFavorite.setOnCheckedChangeListener { _, isChecked -> currentContact.isFavorite = isChecked }
    }
}




















