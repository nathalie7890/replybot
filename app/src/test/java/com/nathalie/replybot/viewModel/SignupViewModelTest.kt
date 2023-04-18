package com.nathalie.replybot.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nathalie.replybot.data.repository.AuthRepository
import com.nathalie.replybot.viewModel.auth.LoginViewModel
import com.nathalie.replybot.viewModel.auth.SignupViewModel
import junit.framework.TestCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

@OptIn(ExperimentalCoroutinesApi::class)
class SignupViewModelTest {
    @Rule
    @JvmField
    val taskExecuterRule = InstantTaskExecutorRule()

    private lateinit var signupViewModel: SignupViewModel
    private val authRepo = Mockito.mock(AuthRepository::class.java)

    @Before
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        signupViewModel = SignupViewModel(authRepo)
    }
}