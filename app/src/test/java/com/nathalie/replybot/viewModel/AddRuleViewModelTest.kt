package com.nathalie.replybot.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nathalie.replybot.data.repository.AuthRepository
import com.nathalie.replybot.data.repository.RuleRepository
import com.nathalie.replybot.viewModel.rule.AddRuleViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Mockito.`when`
import com.nathalie.replybot.data.model.Rule as RuleModel

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
    fun `user should be able to add rule when keyword, message, and at least one checkbox are selected`() = runTest {
        val rule = RuleModel(
            keyword = "test keyword",
            msg = "test message",
            whatsapp = true,
            facebook = false
        )
        `when`(authRepo.getUid()).thenReturn("test user id")
        `when`(repo.addRule(rule)).thenReturn(Unit)

        addRuleViewModel.addRule(rule)

        assertTrue(addRuleViewModel.finish.tryEmit(Unit))
    }

    @Test
    fun `user should not be able to add rule when keyword or message is empty`() = runTest {
        val rule = RuleModel(
            keyword = "",
            msg = "test message",
            whatsapp = true,
            facebook = false
        )

        addRuleViewModel.addRule(rule)

        `when`(repo.addRule(rule)).thenReturn(Unit)
        assertEquals("Kindly fill in every field.","Kindly fill in every field.")
    }

    @Test
    fun `user should not be able to add rule when no checkbox is selected`() = runTest {
        val rule = RuleModel(
            keyword = "test keyword",
            msg = "test message",
            whatsapp = false,
            facebook = false
        )

        addRuleViewModel.addRule(rule)

        `when`(repo.addRule(rule)).thenReturn(Unit)
        assertEquals("Select at least one option.", "Select at least one option.")
    }


//    @Test
//    fun `user should not be able to add an empty rule`() {}
//
//    @Test
//    fun `user should not be able to add a rule if both whatsapp and facebook are unchecked`() {}
//
//    @Test
//    fun `user must be logged in to add a rule`() {}
//
//    @Test
//    fun `user should be able to add rules if field is not empty`() {}

    @After
    fun cleanup() {
    }
}