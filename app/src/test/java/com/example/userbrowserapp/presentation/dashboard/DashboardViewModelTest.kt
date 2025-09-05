package com.example.userbrowserapp.presentation.dashboard

import app.cash.turbine.test
import com.example.userbrowserapp.domain.model.UserModel
import com.example.userbrowserapp.domain.repository.RandomUserListRepository
import com.example.userbrowserapp.util.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    private lateinit var repository: RandomUserListRepository
    private lateinit var viewModel: DashboardViewModel

    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    val mainDispatcherRule = object : TestWatcher() {
        override fun starting(description: Description) {
            Dispatchers.setMain(testDispatcher)
        }

        override fun finished(description: Description) {
            Dispatchers.resetMain()
        }
    }

    private val sampleUsers = listOf(
        UserModel("1", "John Doe", "jkdghdfj", "1234567890", "url1", false),
        UserModel("2", "Alberto Alberto", "iruythknohkf", "1234567890", "url2", false),
        UserModel("3", "John Doe", "bnmncvbcbm", "1234567890", "url3", false)
    )

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)

        coEvery { repository.getUsers(1) } returns sampleUsers
        coEvery { repository.getBookmarkedUsers() } returns emptyList()

        viewModel = DashboardViewModel(repository)
    }

    @Test
    fun `initial load should emit users successfully`() = runTest(testDispatcher) {
        advanceUntilIdle()

        viewModel.usersState.test {
            val state = awaitItem()
            assertTrue(state is Resource.Success)
            val data = (state as Resource.Success).data
            assertEquals(3, data?.size)
        }
    }

    @Test
    fun `loadMoreUsers should append new users`() = runTest(testDispatcher) {
        val moreUsers = listOf(
            UserModel("4", "Scooby", "email1", "1234567890", "url4", false),
            UserModel("5", "Wacky Jacky", "email2", "1234567890", "url5", false)
        )
        coEvery { repository.getUsers(2) } returns moreUsers

        advanceUntilIdle()
        viewModel.loadMoreUsers()
        advanceUntilIdle()

        viewModel.usersState.test {
            val state = awaitItem()
            assertTrue(state is Resource.Success)
            val data = (state as Resource.Success).data
            assertEquals(5, data?.size)
            assertTrue(data?.any { it.name == "Wacky Jacky" } == true)
        }
    }

    @Test
    fun `loadInitialUsers should handle error`() = runTest(testDispatcher) {
        coEvery { repository.getUsers(1) } throws RuntimeException("Network error")

        viewModel = DashboardViewModel(repository)
        advanceUntilIdle()

        viewModel.usersState.test {
            val state = awaitItem()
            assertTrue(state is Resource.Error)
            assertEquals("Network error", (state as Resource.Error).message)
        }
    }

    @Test
    fun `toggleBookmark should update user state`() = runTest(testDispatcher) {
        advanceUntilIdle()

        val user = sampleUsers[0]
        coEvery { repository.bookmarkUser(any()) } returns Unit
        coEvery { repository.getBookmarkedUsers() } returns listOf(user.copy(isBookmarked = true))

        viewModel.toggleBookmark(user)
        advanceUntilIdle()

        viewModel.usersState.test {
            val state = awaitItem()
            assertTrue(state is Resource.Success)
            val data = (state as Resource.Success).data
            assertTrue(data?.first()?.isBookmarked == true)
        }

        coVerify { repository.bookmarkUser(match { it.userId == user.userId }) }
    }
}