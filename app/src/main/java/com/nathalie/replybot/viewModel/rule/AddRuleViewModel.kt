package com.nathalie.replybot.viewModel.rule

import androidx.lifecycle.viewModelScope
import com.nathalie.replybot.data.model.Rule
import com.nathalie.replybot.data.repository.RuleRepository
import com.nathalie.replybot.service.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddRuleViewModel @Inject constructor(
    repo: RuleRepository,
    private val authRepo: AuthService
) : BaseRuleViewModel(repo) {

    //add rule to FireStore
    fun addRule(rule: Rule) {
        //check if keyword and msg are not empty and at least one checkbox is checked
        val isValid = validate(rule.keyword, rule.msg)
        val selected = rule.whatsapp || rule.facebook

        //if isValid and selected are true, call api and add rule to FireStore, else make toast to remind user of the insufficient inputs
        viewModelScope.launch {
            if (isValid) {
                if(!selected)  {
                    error.emit("Select at least one option.")
                } else {
                    val userId = authRepo.getUid().toString()
                    safeApiCall { repo.addRule(rule.copy(userId = userId)) }
                    finish.emit(Unit)
                }
            } else {
                error.emit("Kindly fill in every field.")
            }
        }
    }
}