package com.example.botwast

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.botwast.adapters.ContactAdapter
import com.example.botwast.adapters.RuleAdapter
import com.example.botwast.databinding.ActivityMainBinding
import com.example.botwast.dialogs.AddRuleDialog
import com.example.botwast.dialogs.EditRuleDialog
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dataManager: DataManager
    private lateinit var contactAdapter: ContactAdapter
    private lateinit var ruleAdapter: RuleAdapter

    private val contactPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) loadContacts()
        else Toast.makeText(this, "Permission refusée", Toast.LENGTH_SHORT).show()
    }

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(this, "Permission notifications requise", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataManager = DataManager(this)

        setupUI()
        checkPermissions()
        loadContacts()
        loadRules()
        loadStatistics()
    }

    private fun setupUI() {

        binding.botSwitch.isChecked = dataManager.isBotEnabled()
        updateBotStatus()

        binding.botSwitch.setOnCheckedChangeListener { _, isChecked ->
            dataManager.setBotEnabled(isChecked)
            updateBotStatus()
        }

        // ✅ CONTACT ADAPTER (type ajouté)
        contactAdapter = ContactAdapter(
            contacts = emptyList(),
            onContactSelected = { contact: Contact ->
                dataManager.addSelectedContact(contact.id)
                Toast.makeText(this, "${contact.name} sélectionné", Toast.LENGTH_SHORT).show()
            },
            onContactDeselected = { contact: Contact ->
                dataManager.removeSelectedContact(contact.id)
                Toast.makeText(this, "${contact.name} désélectionné", Toast.LENGTH_SHORT).show()
            }
        )

        binding.contactsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = contactAdapter
        }

        // ✅ RULE ADAPTER (type ajouté)
        ruleAdapter = RuleAdapter(
            rules = emptyList(),
            onEditRule = { rule: MessageRule -> showEditRuleDialog(rule) },
            onDeleteRule = { rule: MessageRule -> deleteRule(rule) }
        )

        binding.rulesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = ruleAdapter
        }

        binding.addRuleButton.setOnClickListener {
            showAddRuleDialog()
        }

        binding.settingsButton.setOnClickListener {
            openSettings()
        }

        binding.statsButton.setOnClickListener {
            showStatisticsDialog()
        }
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            contactPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun loadContacts() {
        lifecycleScope.launch {
            val contacts = ContactHelper.getContacts(this@MainActivity)
                .map { contact ->
                    contact.copy(
                        isSelected = dataManager.isContactSelected(contact.id)
                    )
                }

            contactAdapter.updateContacts(contacts)
        }
    }

    private fun loadRules() {
        lifecycleScope.launch {
            val rules = dataManager.getRules()
            ruleAdapter.updateRules(rules)
        }
    }

    private fun loadStatistics() {
        lifecycleScope.launch {
            val stats = dataManager.getStatistics()

            binding.statsText.text = """
                📊 Statistiques
                Messages reçus: ${stats.totalReceived}
                Réponses envoyées: ${stats.totalReplied}
                Taux de réponse: ${
                if (stats.totalReceived > 0)
                    (stats.totalReplied * 100 / stats.totalReceived)
                else 0
            }%
            """.trimIndent()
        }
    }

    private fun showAddRuleDialog() {
        val dialog = AddRuleDialog(this) { trigger: String, response: String ->

            val selectedContacts = dataManager.getSelectedContacts()

            if (selectedContacts.isEmpty()) {
                Toast.makeText(this, "Sélectionnez d'abord un contact", Toast.LENGTH_SHORT).show()
                return@AddRuleDialog
            }

            selectedContacts.forEach { contactId ->
                val rule = MessageRule(
                    contactId = contactId,
                    trigger = trigger,
                    response = response,
                    priority = dataManager.getRulesForContact(contactId).size
                )

                dataManager.addRule(rule)
            }

            loadRules()
            Toast.makeText(this, "Règle ajoutée", Toast.LENGTH_SHORT).show()
        }

        dialog.show()
    }

    private fun showEditRuleDialog(rule: MessageRule) {
        val dialog = EditRuleDialog(this, rule) { trigger: String, response: String ->

            val updatedRule = rule.copy(
                trigger = trigger,
                response = response
            )

            dataManager.updateRule(rule, updatedRule)
            loadRules()

            Toast.makeText(this, "Règle modifiée", Toast.LENGTH_SHORT).show()
        }

        dialog.show()
    }

    private fun deleteRule(rule: MessageRule) {
        dataManager.deleteRule(rule)
        loadRules()
        Toast.makeText(this, "Règle supprimée", Toast.LENGTH_SHORT).show()
    }

    private fun updateBotStatus() {
        binding.botStatusText.text =
            if (dataManager.isBotEnabled()) "✅ Bot activé"
            else "❌ Bot désactivé"
    }

    private fun openSettings() {
        startActivity(Intent(this, SettingsActivity::class.java))
    }

    private fun showStatisticsDialog() {
        Toast.makeText(this, "Statistiques", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        loadContacts()
        loadRules()
        loadStatistics()
    }
}