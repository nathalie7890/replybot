package com.nathalie.replybot.views.fragments.rule

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.nathalie.replybot.R
import com.nathalie.replybot.utils.Constants
import com.nathalie.replybot.viewModel.rule.AddRuleViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddRuleFragment : BaseRuleFragment() {
    override val viewModel: AddRuleViewModel by viewModels()

    override fun onBindView(view: View, savedInstanceState: Bundle?) {
        super.onBindView(view, savedInstanceState)

        binding?.run {

            //when clicked, get rule's value from edit texts and checkboxes then add to FireStore
            btnSave.setOnClickListener {
                val rule = getRule()
                rule?.let {
                    viewModel.addRule(it)
                }
            }
        }
    }

    override fun onBindData(view: View) {
        super.onBindData(view)

        //after rule is added, call setFragmentResult and pop back to previous stack
        lifecycleScope.launch {
            viewModel.finish.collect {
                Toast.makeText(requireContext(), "Rule added successfully!", Toast.LENGTH_LONG)
                    .show()
                val bundle = Bundle()
                bundle.putBoolean("refresh", true)
                setFragmentResult("finish_add_rule", bundle)
                navController.popBackStack()
            }
        }
    }
}