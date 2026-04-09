package com.example.botwast.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.botwast.MessageRule
import com.example.botwast.R

class EditRuleDialog(
    context: Context,
    private val rule: MessageRule,
    private val onRuleUpdated: (trigger: String, response: String) -> Unit
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_edit_rule)

        val triggerInput = findViewById<EditText>(R.id.trigger_input)
        val responseInput = findViewById<EditText>(R.id.response_input)
        val updateButton = findViewById<Button>(R.id.update_button)
        val cancelButton = findViewById<Button>(R.id.cancel_button)

        // Populate existing values
        triggerInput.setText(rule.trigger)
        responseInput.setText(rule.response)

        updateButton.setOnClickListener {
            val trigger = triggerInput.text.toString().trim()
            val response = responseInput.text.toString().trim()

            if (trigger.isNotEmpty() && response.isNotEmpty()) {
                onRuleUpdated(trigger, response)
                dismiss()
            }
        }

        cancelButton.setOnClickListener {
            dismiss()
        }
    }
}