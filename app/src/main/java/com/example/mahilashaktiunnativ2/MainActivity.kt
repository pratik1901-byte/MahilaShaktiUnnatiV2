package com.example.mahilashaktiunnativ2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mahilashaktiunnativ2.ui.AdminLoginScreen
import com.example.mahilashaktiunnativ2.ui.AdminSetupScreen
import com.example.mahilashaktiunnativ2.ui.MainScreen
import com.example.mahilashaktiunnativ2.ui.SplashScreen
import com.example.mahilashaktiunnativ2.ui.database.AdminViewModel
import com.example.mahilashaktiunnativ2.ui.theme.AppBackground
import com.example.mahilashaktiunnativ2.ui.theme.PrimaryGreen
import com.example.mahilashaktiunnativ2.ui.theme.MahilaShaktiUnnatiV2Theme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {

            MahilaShaktiUnnatiV2Theme {

                val adminViewModel: AdminViewModel =
                    viewModel()

                val adminCount by
                adminViewModel.adminCount.collectAsState()

                var showSplash by remember {

                    mutableStateOf(true)
                }

                if (showSplash) {

                    SplashScreen(

                        onNavigate = {

                            showSplash = false
                        }
                    )

                } else {

                    when {
                        adminCount < 0 -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(AppBackground),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = PrimaryGreen
                                )
                            }
                        }

                        adminCount == 0 -> {
                            AdminSetupScreen(
                                onCreateAdmin = { admin ->
                                    adminViewModel.createAdmin(admin)
                                }
                            )
                        }

                        adminViewModel.loggedInAdminId == 0 -> {
                            AdminLoginScreen(
                                onLogin = { phoneNumber, password, onResult ->
                                    adminViewModel.login(
                                        phoneNumber = phoneNumber,
                                        password = password,
                                        onResult = onResult
                                    )
                                }
                            )
                        }

                        else -> {
                            MainScreen()
                        }
                    }
                }
            }
        }
    }

}
