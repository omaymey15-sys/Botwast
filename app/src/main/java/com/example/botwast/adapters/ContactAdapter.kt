package com.example.botwast.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.botwast.Contact
import com.example.botwast.databinding.ItemContactBinding

class ContactAdapter(
    private var contacts: List<Contact> = emptyList(),
    private val onContactSelected: (Contact) -> Unit,
    private val onContactDeselected: (Contact) -> Unit
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    inner class ContactViewHolder(private val binding: ItemContactBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(contact: Contact) {
            binding.apply {
                contactNameText.text = contact.name
                contactPhonesText.text = contact.phones.joinToString(", ")
                contactCheckbox.isChecked = contact.isSelected

                contactCheckbox.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        onContactSelected(contact)
                    } else {
                        onContactDeselected(contact)
                    }
                }

                root.setOnClickListener {
                    contactCheckbox.toggle()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = ItemContactBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(contacts[position])
    }

    override fun getItemCount(): Int = contacts.size

    fun updateContacts(newContacts: List<Contact>) {
        contacts = newContacts
        notifyDataSetChanged()
    }

    fun addContact(contact: Contact) {
        contacts = contacts + contact
        notifyItemInserted(contacts.size - 1)
    }

    fun removeContact(contact: Contact) {
        val index = contacts.indexOf(contact)
        if (index >= 0) {
            contacts = contacts.filterIndexed { i, _ -> i != index }
            notifyItemRemoved(index)
        }
    }
}