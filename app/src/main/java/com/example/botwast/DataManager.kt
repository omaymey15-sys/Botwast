package com.example.botwast

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DataManager(context: Context) {
    private val prefs: SharedPreferences = 
        context.getSharedPreferences("botwast_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_BOT_ENABLED = "bot_enabled"
        private const val KEY_RULES = "rules"
        private const val KEY_SELECTED_CONTACTS = "selected_contacts"
        private const val KEY_CUSTOM_MESSAGES = "custom_messages"
        private const val KEY_REPLY_DELAY = "reply_delay"
        private const val KEY_CASE_SENSITIVE = "case_sensitive"
        private const val KEY_RANDOM_REPLY = "random_reply"
        private const val KEY_STATISTICS = "statistics"
        private const val KEY_QUIET_MODE = "quiet_mode"
        private const val KEY_QUIET_START = "quiet_start"
        private const val KEY_QUIET_END = "quiet_end"
    }

    // ========== Bot State ==========
    fun isBotEnabled(): Boolean = prefs.getBoolean(KEY_BOT_ENABLED, false)
    
    fun setBotEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_BOT_ENABLED, enabled).apply()
    }

    // ========== Contacts Management ==========
    fun getSelectedContacts(): Set<String> = 
        prefs.getStringSet(KEY_SELECTED_CONTACTS, emptySet()) ?: emptySet()
    
    fun saveSelectedContacts(contacts: Set<String>) {
        prefs.edit().putStringSet(KEY_SELECTED_CONTACTS, contacts).apply()
    }
    
    fun addSelectedContact(contactId: String) {
        val contacts = getSelectedContacts().toMutableSet()
        contacts.add(contactId)
        saveSelectedContacts(contacts)
    }
    
    fun removeSelectedContact(contactId: String) {
        val contacts = getSelectedContacts().toMutableSet()
        contacts.remove(contactId)
        saveSelectedContacts(contacts)
    }
    
    fun isContactSelected(contactId: String): Boolean = 
        getSelectedContacts().contains(contactId)

    // ========== Rules Management ==========
    fun getRules(): List<MessageRule> {
        val json = prefs.getString(KEY_RULES, "[]") ?: "[]"
        return try {
            gson.fromJson(json, object : TypeToken<List<MessageRule>>() {}.type)
                ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun getRulesForContact(contactId: String): List<MessageRule> {
        return getRules().filter { it.contactId == contactId }
            .sortedBy { it.priority }
    }
    
    fun saveRules(rules: List<MessageRule>) {
        val json = gson.toJson(rules)
        prefs.edit().putString(KEY_RULES, json).apply()
    }
    
    fun addRule(rule: MessageRule) {
        val rules = getRules().toMutableList()
        rules.add(rule.copy(id = System.currentTimeMillis().toString()))
        saveRules(rules)
    }
    
    fun updateRule(oldRule: MessageRule, newRule: MessageRule) {
        val rules = getRules().toMutableList()
        val index = rules.indexOfFirst { it.id == oldRule.id }
        if (index != -1) {
            rules[index] = newRule
            saveRules(rules)
        }
    }
    
    fun deleteRule(rule: MessageRule) {
        val rules = getRules().filter { it.id != rule.id }
        saveRules(rules)
    }

    // ========== Custom Messages ==========
    fun getCustomMessages(): List<String> {
        val json = prefs.getString(KEY_CUSTOM_MESSAGES, "[]") ?: "[]"
        return try {
            gson.fromJson(json, object : TypeToken<List<String>>() {}.type)
                ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun saveCustomMessages(messages: List<String>) {
        val json = gson.toJson(messages)
        prefs.edit().putString(KEY_CUSTOM_MESSAGES, json).apply()
    }
    
    fun addCustomMessage(message: String) {
        val messages = getCustomMessages().toMutableList()
        messages.add(message)
        saveCustomMessages(messages)
    }
    
    fun removeCustomMessage(index: Int) {
        val messages = getCustomMessages().toMutableList()
        if (index in messages.indices) {
            messages.removeAt(index)
            saveCustomMessages(messages)
        }
    }

    // ========== Settings ==========
    fun getReplyDelay(): Long = prefs.getLong(KEY_REPLY_DELAY, 2000L)
    
    fun setReplyDelay(delay: Long) {
        prefs.edit().putLong(KEY_REPLY_DELAY, delay).apply()
    }
    
    fun isCaseSensitive(): Boolean = prefs.getBoolean(KEY_CASE_SENSITIVE, false)
    
    fun setCaseSensitive(sensitive: Boolean) {
        prefs.edit().putBoolean(KEY_CASE_SENSITIVE, sensitive).apply()
    }
    
    fun isRandomReplyEnabled(): Boolean = prefs.getBoolean(KEY_RANDOM_REPLY, false)
    
    fun setRandomReplyEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_RANDOM_REPLY, enabled).apply()
    }

    // ========== Quiet Mode ==========
    fun isQuietModeEnabled(): Boolean = prefs.getBoolean(KEY_QUIET_MODE, false)
    
    fun setQuietModeEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_QUIET_MODE, enabled).apply()
    }
    
    fun getQuietModeStart(): String = prefs.getString(KEY_QUIET_START, "23:00") ?: "23:00"
    
    fun setQuietModeStart(time: String) {
        prefs.edit().putString(KEY_QUIET_START, time).apply()
    }
    
    fun getQuietModeEnd(): String = prefs.getString(KEY_QUIET_END, "08:00") ?: "08:00"
    
    fun setQuietModeEnd(time: String) {
        prefs.edit().putString(KEY_QUIET_END, time).apply()
    }

    // ========== Statistics ==========
    fun getStatistics(): Statistics {
        val json = prefs.getString(KEY_STATISTICS, null)
        return if (json != null) {
            try {
                gson.fromJson(json, Statistics::class.java) ?: Statistics()
            } catch (e: Exception) {
                Statistics()
            }
        } else {
            Statistics()
        }
    }
    
    fun saveStatistics(stats: Statistics) {
        val json = gson.toJson(stats)
        prefs.edit().putString(KEY_STATISTICS, json).apply()
    }
    
    fun incrementReplyCount(contactId: String) {
        val stats = getStatistics()
        val newStats = stats.copy(
            totalReceived = stats.totalReceived + 1,
            contactStats = stats.contactStats.toMutableMap().apply {
                val contactStat = get(contactId) ?: ContactStat()
                put(contactId, contactStat.copy(replied = contactStat.replied + 1))
            }
        )
        saveStatistics(newStats)
    }
    
    fun resetStatistics() {
        saveStatistics(Statistics())
    }

    // ========== Export/Import ==========
    fun exportConfiguration(): String {
        val data = mapOf(
            "rules" to getRules(),
            "messages" to getCustomMessages(),
            "contacts" to getSelectedContacts(),
            "settings" to mapOf(
                "reply_delay" to getReplyDelay(),
                "case_sensitive" to isCaseSensitive(),
                "random_reply" to isRandomReplyEnabled()
            )
        )
        return gson.toJson(data)
    }
    
    fun importConfiguration(json: String): Boolean {
        return try {
            val data = gson.fromJson(json, Map::class.java)
            // Parse and import data
            true
        } catch (e: Exception) {
            false
        }
    }
}