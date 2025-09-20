package com.rio.rostry.auth

import android.content.Context
import android.content.SharedPreferences
import com.rio.rostry.domain.model.User
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.util.SessionManager
import io.mockk.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.Date

class SessionManagerTest {

    private lateinit var sessionManager: SessionManager
    private lateinit var context: Context
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    @Before
    fun setUp() {
        context = mockk()
        sharedPreferences = mockk()
        editor = mockk()

        every { context.getSharedPreferences("rostry_session", Context.MODE_PRIVATE) } returns sharedPreferences
        every { sharedPreferences.edit() } returns editor
        every { editor.putString(any(), any()) } returns editor
        every { editor.putLong(any(), any()) } returns editor
        every { editor.remove(any()) } returns editor
        every { editor.apply() } just runs

        sessionManager = SessionManager(context)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `saveUserSession should save user data to preferences`() {
        val user = User(
            id = "123",
            phone = "9876543210",
            userType = UserType.GENERAL,
            createdAt = Date(),
            updatedAt = Date()
        )

        sessionManager.saveUserSession(user)

        verify { editor.putString("user_id", "123") }
        verify { editor.putString("user_phone", "9876543210") }
        verify { editor.putString("user_type", "GENERAL") }
        verify { editor.putLong("login_time", any()) }
        verify { editor.apply() }
    }

    @Test
    fun `isLoggedIn should return false when no user is saved`() {
        every { sharedPreferences.getString("user_id", null) } returns null

        assertFalse(sessionManager.isLoggedIn())
    }

    @Test
    fun `clearUserSession should remove user data from preferences`() {
        sessionManager.clearUserSession()

        verify { editor.remove("user_id") }
        verify { editor.remove("user_phone") }
        verify { editor.remove("user_email") }
        verify { editor.remove("user_type") }
        verify { editor.remove("login_time") }
        verify { editor.apply() }
    }
}