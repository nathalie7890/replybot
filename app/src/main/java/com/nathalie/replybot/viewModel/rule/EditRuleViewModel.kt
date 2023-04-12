package com.nathalie.replybot.viewModel.rule

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nathalie.replybot.data.model.Rule
import com.nathalie.replybot.data.repository.RuleRepository
import com.nathalie.replybot.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditRuleViewModel @Inject constructor(repo: RuleRepository) : BaseRuleViewModel(repo) {
    val rule: MutableLiveData<Rule> = MutableLiveData()

    fun getRuleById(id: String) {
        viewModelScope.launch {
            val res = safeApiCall { repo.getRuleById(id) }
            res?.let {
                rule.value = it
            }
        }
    }

    fun refresh(id: String) {
        getRuleById(id)
    }

    fun editRule(id: String, rule: Rule) {
        val isValid = Utils.validate(rule.keyword, rule.msg)
        val selected = rule.whatsapp || rule.facebook

        viewModelScope.launch {
            if (isValid) {
                if (!selected) {
                    error.emit("Select at least one option.")
                } else {
                    repo.updateRule(id, rule)
                    finish.emit(Unit)
                }
            } else {
                error.emit("Kindly fill in every field.")
            }
        }
    }

    fun deleteRule(id: String) {
        viewModelScope.launch {
            repo.deleteRule(id)
            finishDelete.emit(Unit)
        }
    }

    fun disabledRule(id: String, disabled: Boolean) {
        viewModelScope.launch {
            repo.disableRule(id, disabled)
            finishDisable.emit(Unit)
        }
    }
}