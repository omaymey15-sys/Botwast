package com.example.botwast.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.botwast.MessageRule
import com.example.botwast.R

class RuleAdapter(
    private var rules: List<MessageRule> = emptyList(),
    private val onEditRule: (MessageRule) -> Unit,
    private val onDeleteRule: (MessageRule) -> Unit
) : RecyclerView.Adapter<RuleAdapter.RuleViewHolder>() {

    class RuleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ruleText: TextView = view.findViewById(R.id.rule_text)
        val rulePriority: TextView = view.findViewById(R.id.rule_priority)
        val editButton: Button = view.findViewById(R.id.edit_button)
        val deleteButton: Button = view.findViewById(R.id.delete_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RuleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rule, parent, false)
        return RuleViewHolder(view)
    }

    override fun onBindViewHolder(holder: RuleViewHolder, position: Int) {
        val rule = rules[position]

        holder.ruleText.text = "SI \"${rule.trigger}\" → \"${rule.response}\""
        holder.rulePriority.text = "Priorité: ${rule.priority}"

        holder.editButton.setOnClickListener {
            onEditRule(rule)
        }

        holder.deleteButton.setOnClickListener {
            onDeleteRule(rule)
        }

        holder.itemView.setOnLongClickListener {
            onEditRule(rule)
            true
        }
    }

    override fun getItemCount(): Int = rules.size

    fun updateRules(newRules: List<MessageRule>) {
        rules = newRules
        notifyDataSetChanged()
    }

    fun addRule(rule: MessageRule) {
        rules = rules + rule
        notifyItemInserted(rules.size - 1)
    }

    fun removeRule(rule: MessageRule) {
        val index = rules.indexOf(rule)
        if (index != -1) {
            rules = rules.toMutableList().apply { removeAt(index) }
            notifyItemRemoved(index)
        }
    }
}