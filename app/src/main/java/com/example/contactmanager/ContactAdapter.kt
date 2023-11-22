package com.example.contactmanager

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.contactmanager.databinding.ContactItemBinding

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
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    class ContactViewHolder(val binding: ContactItemBinding) : RecyclerView.ViewHolder(binding.root) { }
}




















