package com.nathalie.replybot.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nathalie.replybot.data.repository.AuthRepository
import com.nathalie.replybot.data.repository.RuleRepository
import com.nathalie.replybot.viewModel.rule.AddRuleViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

@OptIn(ExperimentalCoroutinesApi::class)
class AddRuleViewModelTest {
    @Rule
    @JvmField
    val taskExecutorRule = InstantTaskExecutorRule()

    private lateinit var addRuleViewModel: AddRuleViewModel
    private val repo = Mockito.mock(RuleRepository::class.java)
    private val authRepo = Mockito.mock(AuthRepository::class.java)

    @Before
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        addRuleViewModel = AddRuleViewModel(repo, authRepo)
    }

    @Test
    fun `user should not be able to add an empty rule`() {}

    @Test
    fun `user should not be able to add a rule if both whatsapp and facebook are unchecked`() {}

    @Test
    fun `user must be logged in to add a rule`() {}

    @Test
    fun `user should be able to add rules if field is not empty`() {}

    @After
    fun cleanup() {
    }
}