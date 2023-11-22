package com.example.contactmanager

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.contactmanager.databinding.ContactItemBinding

class ContactAdapter(
    private var contacts: MutableList<Contact>
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
        holder.binding.tvContactTitle.text = currentContact.name
        holder.binding.cbFavorite.isChecked = currentContact.isFavorite
        holder.binding.cbFavorite.setOnCheckedChangeListener { _, isChecked -> currentContact.isFavorite = isChecked }
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    class ContactViewHolder(val binding: ContactItemBinding) : RecyclerView.ViewHolder(binding.root) { }

    fun addContact(newContact: Contact) {
//        contacts.add(newContact)
//        notifyItemInserted(contacts.size - 1)
    }

    fun deleteContact(contact: Contact) {
        // TODO: delete item in database, then notifyChanges -> notifyDataSetChanged()
    }

    fun filterFavorites() {

    }
}




















