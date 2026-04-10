package com.example.botwast

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.time.LocalTime

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

    // 🔥 FIX: classes locales pour éviter erreurs build
    data class Contact(val id: String, val name: String)

    data class MessageRule(
        val keyword: String,
        val response: String
    ) {
        fun matches(message: String, caseSensitive: Boolean): Boolean {
            return if (caseSensitive)
                message.contains(keyword)
            else
                message.lowercase().contains(keyword.lowercase())
        }
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

        if (!isWhatsAppNotification(sbn.packageName)) return

        val notification = sbn.notification
        val title = notification.extras.getString(Notification.EXTRA_TITLE) ?: return
        val text = notification.extras.getString(Notification.EXTRA_TEXT) ?: return

        val sender = extractSender(title) ?: return
        val messageContent = text.trim()

        val key = "$sender:$messageContent:${sbn.postTime}"
        if (isDuplicate(key)) return

        processMessage(sender, messageContent)
    }

    private fun isWhatsAppNotification(packageName: String): Boolean {
        return packageName == WHATSAPP_PACKAGE ||
               packageName == WHATSAPP_BUSINESS_PACKAGE
    }

    private fun extractSender(title: String): String? {
        return title.substringAfterLast(">").trim().takeIf { it.isNotEmpty() }
    }

    private fun processMessage(sender: String, content: String) {
        scope.launch {
            try {
                val contact = Contact(sender, sender)

                if (!dataManager.isContactSelected(contact.id)) return@launch
                if (isQuietMode()) return@launch

                val rule = findMatchingRule(content) ?: return@launch

                val delay = dataManager.getReplyDelay()
                queueReply(ReplyTask(contact, rule.response, delay))

                dataManager.incrementReplyCount(contact.id)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun findMatchingRule(message: String): MessageRule? {
        val rules = listOf(
            MessageRule("hello", "Hi!"),
            MessageRule("salut", "Bonjour!")
        )

        val caseSensitive = dataManager.isCaseSensitive()

        return rules.firstOrNull {
            it.matches(message, caseSensitive)
        }
    }

    private fun isQuietMode(): Boolean {
        if (!dataManager.isQuietModeEnabled()) return false

        val now = LocalTime.now()
        val start = LocalTime.parse(dataManager.getQuietModeStart())
        val end = LocalTime.parse(dataManager.getQuietModeEnd())

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
        try {
            scope.launch {
                simulateTyping(1000)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun simulateTyping(duration: Long) {
        delay(duration)
    }

    private fun isDuplicate(key: String): Boolean {
        if (processedNotifications.contains(key)) return true

        processedNotifications.add(key)

        if (processedNotifications.size > MAX_CACHE_SIZE) {
            processedNotifications.clear()
        }

        return false
    }
}