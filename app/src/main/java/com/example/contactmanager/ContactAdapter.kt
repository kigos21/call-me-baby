package com.example.contactmanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.contact_item.view.cbFavorite
import kotlinx.android.synthetic.main.contact_item.view.tvContactTitle

class ContactAdapter(
    public val contacts: MutableList<Contact>
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {
    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        return ContactViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.contact_item,
                parent,
                false,
            )
        )
    }

    fun addContact(newContact: Contact) {
        contacts.add(newContact)
        notifyItemInserted(contacts.size - 1)
    }

    fun deleteContact(contact: Contact) {
        TODO("Delete the the contact (by reference in memory??)")
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val currentContact = contacts[position]
        holder.itemView.apply {
            tvContactTitle.text = currentContact.name
            cbFavorite.isChecked = currentContact.isFavorite
            cbFavorite.setOnCheckedChangeListener { _, _ ->
                currentContact.isFavorite = !currentContact.isFavorite
            }
        }
    }
}




















