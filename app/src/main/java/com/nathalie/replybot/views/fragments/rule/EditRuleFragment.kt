package com.nathalie.replybot.views.fragments.rule

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.material.button.MaterialButton
import com.google.api.Distribution.BucketOptions.Linear
import com.nathalie.replybot.R
import com.nathalie.replybot.utils.Constants
import com.nathalie.replybot.viewModel.BaseViewModel
import com.nathalie.replybot.viewModel.rule.EditRuleViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditRuleFragment : BaseRuleFragment() {
    override val viewModel: EditRuleViewModel by viewModels()

    override fun onBindView(view: View, savedInstanceState: Bundle?) {
        super.onBindView(view, savedInstanceState)
    }

    override fun onBindData(view: View) {
        super.onBindData(view)

        val navArgs: EditRuleFragmentArgs by navArgs()
        viewModel.getRuleById(navArgs.id)

        viewModel.rule.observe(viewLifecycleOwner) { rule ->
            binding?.run {
                llDeleteDisabled.isVisible = true
                etKeyword.setText(rule.keyword)
                etMsg.setText(rule.msg)
                checkWhatsapp.isChecked = rule.whatsapp
                checkFacebook.isChecked = rule.facebook
                checkSlack.isChecked = rule.slack

                isDisabled(
                    rule.disabled,
                    btnSave,
                    btnDisabled,
                    listOf(checkSlack, checkWhatsapp, checkFacebook),
                    listOf(llSlack, llWhatsapp, llFacebook)
                )

                btnSave.setOnClickListener { _ ->
                    val updatedRule = getRule()?.copy(userId = rule.userId)
                    updatedRule?.let {
                        viewModel.editRule(navArgs.id, updatedRule)
                    }
                }

                btnDelete.setOnClickListener {
                    viewModel.deleteRule(navArgs.id)
                }

                btnDisabled.setOnClickListener {
                    val disabled: Boolean = !rule.disabled
                    viewModel.disabledRule(navArgs.id, disabled)
                }
            }
        }


        lifecycleScope.launch {
            viewModel.finish.collect {
                popBackWithToast("finish_edit_rule", "Rule updated successfully!")
            }
        }

        lifecycleScope.launch {
            viewModel.finishDelete.collect {
                popBackWithToast("finish_delete_rule", "Rule deleted successfully!")
            }
        }

        lifecycleScope.launch {
            viewModel.finishDisable.collect {
                viewModel.refresh(navArgs.id)
            }
        }
    }

    //change bg color of btn save and checkboxes when btn disabled is clicked
    private fun isDisabled(
        disabled: Boolean,
        btnSave: MaterialButton,
        btnDisabled: MaterialButton,
        checkBoxes: List<CheckBox>,
        checkBoxContainers: List<LinearLayout>
    ) {
        btnSave.isEnabled = !disabled

        if (disabled) {
            btnDisabled.text = "Enable"
            checkBoxes.forEach {
                it.isEnabled = false
            }
            checkBoxContainers.forEach {
                it.isEnabled = false
            }
        } else {
            btnDisabled.text = "Disable"
            checkBoxes.forEach {
                it.isEnabled = true
            }
            checkBoxContainers.forEach {
                it.isEnabled = true
            }
        }
    }


    private fun popBackWithToast(requestKey: String, toastMsg: String) {
        val bundle = Bundle()
        bundle.putBoolean("refresh", true)
        setFragmentResult(requestKey, bundle)
        navController.popBackStack()
        Toast.makeText(requireContext(), toastMsg, Toast.LENGTH_LONG)
            .show()
    }
}