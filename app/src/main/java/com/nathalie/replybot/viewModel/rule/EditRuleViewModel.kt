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

    //fetch rule that matches the provided id from firebase
    fun getRuleById(id: String) {
        viewModelScope.launch {
            val res = safeApiCall { repo.getRuleById(id) }
            res?.let {
                rule.value = it
            }
        }
    }

    //fetch rule that matches the provided id, usually called after change is applied
    fun refresh(id: String) {
        getRuleById(id)
    }

    //update rule(match id) in firebase
    fun editRule(id: String, rule: Rule) {
        //returns true is keyword and msg are not empty
        val isValid = Utils.validate(rule.keyword, rule.msg)

        //returns true if one of the checkboxes(whatsapp & facebook) is checked
        val selected = rule.whatsapp || rule.facebook

        //if isValid & selected return true, update rule(match id) in firebase
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

    //delete rule that matches the id provided from firebase
    fun deleteRule(id: String) {
        viewModelScope.launch {
            repo.deleteRule(id)
            finishDelete.emit(Unit)
        }
    }

    //toggle rule's disable value and update it from firebase
    fun disabledRule(id: String, disabled: Boolean) {
        viewModelScope.launch {
            repo.disableRule(id, disabled)
            finishDisable.emit(Unit)
        }
    }
}