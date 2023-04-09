package com.nathalie.replybot.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.nathalie.replybot.data.model.Rule
import com.nathalie.replybot.databinding.ItemRuleLayoutBinding
import com.nathalie.replybot.utils.Utils.update

class RuleAdapter(private var rules: MutableList<Rule>) :
    RecyclerView.Adapter<RuleAdapter.ItemRuleHolder>() {

    var listener: Listener? = null

    class ItemRuleHolder(val binding: ItemRuleLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemRuleHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemRuleLayoutBinding.inflate(layoutInflater, parent, false)
        return ItemRuleHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemRuleHolder, position: Int) {
        val rule = rules[position]

        holder.binding.run {
            tvKeyword.text = rule.keyword
            tvMsg.text = rule.msg
            ivWhatsapp.isVisible = rule.whatsapp
            ivFacebook.isVisible = rule.facebook

            cvRule.setOnClickListener {
                listener?.onClick(rule)
            }
        }
    }

    fun setRules(rules: MutableList<Rule>) {
        val oldRules = this.rules
        this.rules = rules.toMutableList()
        update(oldRules, rules) { rule1, rule2 ->
            rule1.id == rule2.id
        }
    }

    override fun getItemCount(): Int {
        return rules.size
    }

    interface Listener {
        fun onClick(rule: Rule)
    }
}