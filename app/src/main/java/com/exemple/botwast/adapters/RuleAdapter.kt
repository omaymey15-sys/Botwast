package com.example.botwast.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.botwast.MessageRule
import com.example.botwast.databinding.ItemRuleBinding

class RuleAdapter(
    private var rules: List<MessageRule> = emptyList(),
    private val onEditRule: (MessageRule) -> Unit,
    private val onDeleteRule: (MessageRule) -> Unit
) : RecyclerView.Adapter<RuleAdapter.RuleViewHolder>() {

    inner class RuleViewHolder(private val binding: ItemRuleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(rule: MessageRule) {
            binding.apply {
                ruleText.text = "SI \"${rule.trigger}\" → \"${rule.response}\""
                rulePriority.text = "Priorité: ${rule.priority}"

                editButton.setOnClickListener {
                    onEditRule(rule)
                }

                deleteButton.setOnClickListener {
                    onDeleteRule(rule)
                }

                root.setOnLongClickListener {
                    onEditRule(rule)
                    true
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RuleViewHolder {
        val binding = ItemRuleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RuleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RuleViewHolder, position: Int) {
        holder.bind(rules[position])
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
        if (index >= 0) {
            rules = rules.filterIndexed { i, _ -> i != index }
            notifyItemRemoved(index)
        }
    }
}