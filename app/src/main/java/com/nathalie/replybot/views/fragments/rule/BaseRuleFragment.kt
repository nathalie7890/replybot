package com.nathalie.replybot.views.fragments.rule

import com.nathalie.replybot.R
import com.nathalie.replybot.data.model.Rule
import com.nathalie.replybot.databinding.FragmentAddRuleBinding
import com.nathalie.replybot.views.fragments.BaseFragment

abstract class BaseRuleFragment : BaseFragment<FragmentAddRuleBinding>() {
    override fun getLayoutResource() = R.layout.fragment_add_rule

    fun getRule(): Rule? {
        return binding?.run {
            val keyword = etKeyword.text.toString()
            val msg = etMsg.text.toString()

            Rule("", keyword, msg, "")
        }
    }
}