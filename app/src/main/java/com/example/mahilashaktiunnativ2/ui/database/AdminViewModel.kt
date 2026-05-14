package com.example.mahilashaktiunnativ2.ui.database

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AdminViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val database =
        AppDatabase.getDatabase(application)

    private val adminDao =
        database.adminDao()

    private val preferences =
        application.getSharedPreferences(
            "admin_session",
            Context.MODE_PRIVATE
        )

    var loggedInAdminId by mutableIntStateOf(
        preferences.getInt("logged_in_admin_id", 0)
    )
        private set

    val adminCount =
        adminDao
            .getAdminCount()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = -1
            )

    val currentAdmin =
        adminDao
            .getFirstAdmin()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )

    fun createAdmin(
        admin: AdminEntity
    ) {
        viewModelScope.launch {
            val newId =
                adminDao.insertAdmin(admin).toInt()

            setLoggedInAdmin(newId)
        }
    }

    fun login(
        phoneNumber: String,
        password: String,
        onResult: (String?) -> Unit
    ) {
        viewModelScope.launch {
            val validPhone =
                phoneNumber.length == 10 &&
                        phoneNumber.all { it.isDigit() }

            if (!validPhone) {
                onResult("Enter a valid 10 digit phone number.")
                return@launch
            }

            if (password.isBlank()) {
                onResult("Enter your password.")
                return@launch
            }

            val admin =
                adminDao.getAdminByCredentials(
                    phoneNumber = phoneNumber,
                    password = password
                )

            if (admin == null) {
                onResult("Phone number or password is incorrect.")
            } else {
                setLoggedInAdmin(admin.id)
                onResult(null)
            }
        }
    }

    fun updateAdmin(
        admin: AdminEntity
    ) {
        viewModelScope.launch {
            adminDao.updateAdmin(
                admin.copy(updatedAt = System.currentTimeMillis())
            )
        }
    }

    fun logout() {
        setLoggedInAdmin(0)
    }

    private fun setLoggedInAdmin(
        adminId: Int
    ) {
        loggedInAdminId = adminId
        preferences
            .edit()
            .putInt("logged_in_admin_id", adminId)
            .apply()
    }
}
