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

    fun addRule(rule: Rule) {
        val isValid = validate(rule.keyword, rule.msg)

        viewModelScope.launch {
            if (isValid) {
                val userId = authRepo.getUid().toString()
                safeApiCall { repo.addRule(rule.copy(userId = userId)) }
                finish.emit(Unit)
            } else {
                error.emit("Kindly fill in every field.")
            }
        }
    }
}