package com.nathalie.replybot.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nathalie.replybot.data.model.User
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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.verify

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

    @Test
   fun `signup with valid input`() = runTest{
        val name = "John"
        val email = "john@example.com"
        val password = "password"
        val user = User(name, email, password)

        Mockito.`when`(authRepo.register(user)).thenReturn(Unit)

        signupViewModel.name.value = name
        signupViewModel.email.value = email
        signupViewModel.password.value = password
        signupViewModel.signup()

        assertEquals(signupViewModel.signupFinish.first(), Unit)
    }

    @Test
    fun `signup with invalid input`() = runTest {
        val name = "John"
        val email = ""
        val password = "password"
        val user = User(name, email, password)

        signupViewModel.name.value = name
        signupViewModel.email.value = email
        signupViewModel.password.value = password
        signupViewModel.signup()

        Mockito.`when`(authRepo.register(user)).thenReturn(Unit)

        assertEquals(signupViewModel.error.first(), "Please fill in all information")
    }
}