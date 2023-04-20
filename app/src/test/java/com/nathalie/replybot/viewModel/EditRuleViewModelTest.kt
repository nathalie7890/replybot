package com.nathalie.replybot.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nathalie.replybot.data.repository.RuleRepository
import com.nathalie.replybot.viewModel.rule.EditRuleViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import com.nathalie.replybot.data.model.Rule as RuleModel

@OptIn(ExperimentalCoroutinesApi::class)
class EditRuleViewModelTest {
    @Rule
    @JvmField
    val taskExecutorRule = InstantTaskExecutorRule()

    private lateinit var editRuleViewModel: EditRuleViewModel
    private val repo = Mockito.mock(RuleRepository::class.java)

    @Before
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        editRuleViewModel = EditRuleViewModel(repo)
    }

    @Test
    fun `update rule when keyword, message, and at least one checkbox are selected and id is provided`() =
        runTest {
            val rule = RuleModel(
                keyword = "test keyword",
                msg = "test message",
                whatsapp = true,
                facebook = false
            )

            val id = "1"

            `when`(repo.updateRule(id, rule)).thenReturn(rule)

            editRuleViewModel.editRule(id, rule)

            assertTrue(editRuleViewModel.finish.tryEmit(Unit))
        }

    @Test
    fun `user should not be able to update rule when keyword and message are missing`() = runTest {
        val rule = RuleModel(
            keyword = "",
            msg = "test message",
            whatsapp = true,
            facebook = false
        )

        val id = "1"

        `when`(repo.updateRule(id, rule)).thenReturn(rule)
        editRuleViewModel.editRule(id, rule)

        assertEquals("Kindly fill in every field.", "Kindly fill in every field.")
    }

    @Test
    fun `user should not be able to update rule when no checkbox is selected`() = runTest {
        val rule = RuleModel(
            keyword = "test keyword",
            msg = "test message",
            whatsapp = false,
            facebook = false
        )

        val id = "1"

        `when`(repo.updateRule(id, rule)).thenReturn(rule)
        editRuleViewModel.editRule(id, rule)

        assertEquals("Select at least one option.", "Select at least one option.")
    }
}