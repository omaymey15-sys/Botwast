package com.example.botwast

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentLinkedQueue

class WhatsAppListener : NotificationListenerService() {
    private lateinit var dataManager: DataManager
    private val processedNotifications = mutableSetOf<String>()
    private val replyQueue = ConcurrentLinkedQueue<ReplyTask>()
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    companion object {
        private const val WHATSAPP_PACKAGE = "com.whatsapp"
        private const val WHATSAPP_BUSINESS_PACKAGE = "com.whatsapp.w4b"
        private const val MAX_CACHE_SIZE = 100
    }

    data class ReplyTask(
        val contact: Contact,
        val message: String,
        val delay: Long
    )

    override fun onCreate() {
        super.onCreate()
        dataManager = DataManager(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (!dataManager.isBotEnabled()) return

        // Check if notification is from WhatsApp
        if (!isWhatsAppNotification(sbn.packageName)) return

        val notification = sbn.notification
        val title = notification.extras.getString(Notification.EXTRA_TITLE) ?: return
        val text = notification.extras.getString(Notification.EXTRA_TEXT) ?: return

        val sender = extractSender(title, text) ?: return
        val messageContent = extractMessageContent(text)

        // Avoid duplicates
        val notificationKey = "$sender:$messageContent:${sbn.postTime}"
        if (isDuplicate(notificationKey)) return

        processMessage(sender, messageContent)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        // Optional: Handle notification removal
    }

    private fun isWhatsAppNotification(packageName: String): Boolean {
        return packageName == WHATSAPP_PACKAGE || packageName == WHATSAPP_BUSINESS_PACKAGE
    }

    private fun extractSender(title: String, text: String): String? {
        // Extract contact name from title
        // Format typically: "Contact Name" or "Group > Contact Name"
        return title.substringAfterLast(">").trim()
            .takeIf { it.isNotBlank() }
    }

    private fun extractMessageContent(text: String): String {
        // Remove system messages and extract actual message
        return text.trim()
    }

    private fun processMessage(sender: String, content: String) {
        scope.launch {
            try {
                // Find contact
                val contact = ContactHelper.getContactByName(this@WhatsAppListener, sender)
                    ?: return@launch

                // Check if contact is selected
                if (!dataManager.isContactSelected(contact.id)) return@launch

                // Check quiet mode
                if (isQuietMode()) return@launch

                // Find matching rule
                val rule = findMatchingRule(contact.id, content) ?: return@launch

                // Queue reply
                val delay = dataManager.getReplyDelay()
                queueReply(ReplyTask(contact, rule.response, delay))

                // Update statistics
                dataManager.incrementReplyCount(contact.id)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun findMatchingRule(contactId: String, message: String): MessageRule? {
        val rules = dataManager.getRulesForContact(contactId)
        val caseSensitive = dataManager.isCaseSensitive()

        return rules.firstOrNull { rule ->
            rule.matches(message, caseSensitive)
        }
    }

    private fun isQuietMode(): Boolean {
        if (!dataManager.isQuietModeEnabled()) return false

        val now = java.time.LocalTime.now()
        val start = java.time.LocalTime.parse(dataManager.getQuietModeStart())
        val end = java.time.LocalTime.parse(dataManager.getQuietModeEnd())

        return if (start.isBefore(end)) {
            now.isAfter(start) && now.isBefore(end)
        } else {
            now.isAfter(start) || now.isBefore(end)
        }
    }

    private fun queueReply(task: ReplyTask) {
        replyQueue.add(task)
        scope.launch {
            delay(task.delay)
            sendReply(task)
        }
    }

    private fun sendReply(task: ReplyTask) {
        // Note: Sending WhatsApp messages requires accessibility service
        // This is a simplified version - actual implementation needs accessibility service
        try {
            simulateTyping(1000)
            // Send via accessibility service or WhatsApp API
            // This would require proper implementation with AccessibilityService
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun simulateTyping(duration: Long) {
        delay(duration)
    }

    private fun isDuplicate(notificationKey: String): Boolean {
        if (processedNotifications.contains(notificationKey)) return true

        processedNotifications.add(notificationKey)

        // Clean cache if too large
        if (processedNotifications.size > MAX_CACHE_SIZE) {
            cleanCache()
        }

        return false
    }

    private fun cleanCache() {
        processedNotifications.clear()
    }
}