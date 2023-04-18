package com.nathalie.replybot.viewModel

import androidx.lifecycle.MutableLiveData
import com.nathalie.replybot.data.model.User
import com.nathalie.replybot.service.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(private val auth: AuthService) : BaseViewModel() {
    val user: MutableLiveData<User> = MutableLiveData()

    //fetch current user from firebase
    suspend fun getCurrentUser() {
        val res = safeApiCall { auth.getCurrentUser() }
        res.let {
            user.value = it
        }
    }
}