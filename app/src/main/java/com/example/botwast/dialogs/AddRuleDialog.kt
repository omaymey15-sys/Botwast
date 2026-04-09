package com.example.botwast.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.botwast.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AddRuleDialog(
    context: Context,
    private val onRuleAdded: (trigger: String, response: String) -> Unit
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_add_rule)

        val triggerInput = findViewById<EditText>(R.id.trigger_input)
        val responseInput = findViewById<EditText>(R.id.response_input)
        val addButton = findViewById<Button>(R.id.add_button)
        val cancelButton = findViewById<Button>(R.id.cancel_button)

        addButton.setOnClickListener {
            val trigger = triggerInput.text.toString().trim()
            val response = responseInput.text.toString().trim()

            if (trigger.isNotEmpty() && response.isNotEmpty()) {
                onRuleAdded(trigger, response)
                dismiss()
            }
        }

        cancelButton.setOnClickListener {
            dismiss()
        }
    }
}