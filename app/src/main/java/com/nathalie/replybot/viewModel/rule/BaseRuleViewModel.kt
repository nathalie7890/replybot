package com.nathalie.replybot.viewModel.rule

import androidx.lifecycle.viewModelScope
import com.nathalie.replybot.data.repository.RuleRepository
import com.nathalie.replybot.utils.Utils
import com.nathalie.replybot.viewModel.BaseViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

abstract class BaseRuleViewModel(val repo: RuleRepository) : BaseViewModel() {
    val finish: MutableSharedFlow<Unit> = MutableSharedFlow()
    val finishDelete: MutableSharedFlow<Unit> = MutableSharedFlow()

    fun validate(
        keyword: String,
        message: String,
    ): Boolean {
        return if (Utils.validate(keyword, message)
        ) {
            true
        } else {
            viewModelScope.launch {
                error.emit("")
            }
            false
        }
    }
}