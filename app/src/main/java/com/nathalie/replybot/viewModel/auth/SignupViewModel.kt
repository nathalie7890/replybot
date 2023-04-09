package com.nathalie.replybot.viewModel.auth

import androidx.lifecycle.viewModelScope
import com.nathalie.replybot.data.model.User
import com.nathalie.replybot.service.AuthService
import com.nathalie.replybot.utils.Utils
import com.nathalie.replybot.viewModel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(private val authRepo: AuthService): BaseViewModel() {
    val signupFinish: MutableSharedFlow<Unit> = MutableSharedFlow()
    val name: MutableStateFlow<String> = MutableStateFlow("")
    val email: MutableStateFlow<String> = MutableStateFlow("")
    val password : MutableStateFlow<String> = MutableStateFlow("")

    fun signup() {
        if (Utils.validate(name.value, email.value, password.value)) {
            viewModelScope.launch {
                safeApiCall {
                    authRepo.register(User(name = name.value, email = email.value, password = password.value))
                    signupFinish.emit(Unit)
                }
            }
        } else {
            viewModelScope.launch {
               error.emit("Please fill in all information")
            }
        }
    }
}