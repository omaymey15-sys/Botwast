package com.example.botwast

data class MessageRule(
    val id: String = "",
    val contactId: String,
    val trigger: String,
    val response: String,
    val isRegex: Boolean = false,
    val priority: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun matches(message: String, caseSensitive: Boolean = false): Boolean {
        val msg = if (caseSensitive) message else message.lowercase()
        val trigger = if (caseSensitive) this.trigger else this.trigger.lowercase()
        
        return if (isRegex) {
            try {
                Regex(trigger).containsMatchIn(msg)
            } catch (e: Exception) {
                false
            }
        } else {
            msg.contains(trigger)
        }
    }
}

data class Contact(
    val id: String,
    val name: String,
    val phones: List<String> = emptyList(),
    val isSelected: Boolean = false
)

data class Statistics(
    val totalReceived: Int = 0,
    val totalReplied: Int = 0,
    val lastReset: Long = System.currentTimeMillis(),
    val contactStats: Map<String, ContactStat> = emptyMap()
)

data class ContactStat(
    val received: Int = 0,
    val replied: Int = 0,
    val lastMessage: Long = System.currentTimeMillis()
)