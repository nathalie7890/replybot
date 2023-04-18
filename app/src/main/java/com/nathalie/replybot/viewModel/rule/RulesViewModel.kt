package com.nathalie.replybot.viewModel.rule

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nathalie.replybot.data.model.Rule
import com.nathalie.replybot.data.repository.RuleRepository
import com.nathalie.replybot.service.AuthService
import com.nathalie.replybot.viewModel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RulesViewModel @Inject constructor(
    private val repo: RuleRepository,
) : BaseViewModel() {
    val rules: MutableLiveData<List<Rule>> = MutableLiveData()

    override fun onViewCreated() {
        super.onViewCreated()
        getRules()
    }


    //fetch every rule that matches the current user's id from FireStoreRuleRepository
    fun getRules() {
        viewModelScope.launch {
            val res = safeApiCall { repo.getAllRules() }
            res?.let {
                rules.value = it
            }
        }
    }

    //call getRules(), called after change is applied to re-fetch all rule
    fun onRefresh() {
        getRules()
    }
}