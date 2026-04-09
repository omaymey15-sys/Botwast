package com.example.botwast

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import androidx.core.content.ContextCompat

object ContactHelper {
    private const val CACHE_DURATION = 60000L // 1 minute
    private var lastCacheTime = 0L
    private val contactCache = mutableMapOf<String, Contact>()

    fun getContacts(context: Context): List<Contact> {
        if (!hasContactPermission(context)) return emptyList()

        val contacts = mutableListOf<Contact>()
        val resolver = context.contentResolver

        val cursor: Cursor? = resolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            arrayOf(
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME
            ),
            null,
            null,
            ContactsContract.Contacts.DISPLAY_NAME
        )

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getString(it.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                val name = it.getString(it.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))

                val phones = getPhoneNumbers(resolver, id)
                if (phones.isNotEmpty()) {
                    contacts.add(Contact(id = id, name = name, phones = phones))
                }
            }
        }

        return contacts
    }

    fun getContactById(context: Context, id: String): Contact? {
        return getContacts(context).find { it.id == id }
    }

    fun getContactByName(context: Context, name: String): Contact? {
        return getContacts(context).find { it.name.equals(name, ignoreCase = true) }
    }

    fun searchContacts(context: Context, query: String): List<Contact> {
        return getContacts(context).filter {
            it.name.contains(query, ignoreCase = true)
        }
    }

    private fun getPhoneNumbers(resolver: ContentResolver, contactId: String): List<String> {
        val phones = mutableListOf<String>()
        val cursor: Cursor? = resolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
            "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
            arrayOf(contactId),
            null
        )

        cursor?.use {
            while (it.moveToNext()) {
                val phone = it.getString(
                    it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)
                )
                phones.add(formatPhoneNumber(phone))
            }
        }

        return phones
    }

    private fun formatPhoneNumber(number: String): String {
        return number.replace(Regex("[^\\d+]"), "")
    }

    fun hasContactPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.READ_CONTACTS
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    fun clearCache() {
        contactCache.clear()
        lastCacheTime = 0L
    }
}