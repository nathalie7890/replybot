package com.nathalie.replybot.viewModel.auth

import androidx.lifecycle.viewModelScope
import com.nathalie.replybot.data.repository.AuthRepository
import com.nathalie.replybot.utils.Utils
import com.nathalie.replybot.viewModel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val authRepo: AuthRepository) : BaseViewModel() {
    val loginFinish: MutableSharedFlow<Unit> = MutableSharedFlow()
    val email: MutableStateFlow<String> = MutableStateFlow("")
    val password: MutableStateFlow<String> = MutableStateFlow("")

    //login
    fun login() {
        //make sure email and password are not empty else display toast as indication to user
        if (Utils.validate(email.value, password.value)) {
            viewModelScope.launch {
                val res = safeApiCall {
                    authRepo.login(email.value, password.value)
                }
                if (res == true) {
                    loginFinish.emit(Unit)
                } else {
                    error.emit("Login Failed")
                }
            }
        } else {
            viewModelScope.launch {
                error.emit("Failed to Login, Please try again")
            }
        }
    }
}