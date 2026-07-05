package com.kadawatcha.atlas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kadawatcha.atlas.ui.LoginScreen
import com.kadawatcha.atlas.ui.MainScreen
import com.kadawatcha.atlas.ui.NewAccountScreen
import com.kadawatcha.atlas.ui.theme.AppTheme
import com.kadawatcha.atlas.utils.SessionManager
import com.kadawatcha.atlas.utils.SettingsManager
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val settingsManager = remember { SettingsManager(context) }
            val sessionManager = remember { SessionManager(context) }
            
            val isDarkMode by settingsManager.isDarkMode.collectAsState(initial = isSystemInDarkTheme())
            
            val loggedInUser by sessionManager.loggedInUser.collectAsState("LOADING")
            val loggedInUserId by sessionManager.loggedInUserId.collectAsState(null)

            AppTheme(darkTheme = isDarkMode) {
                val navController = rememberNavController()

                Surface(color = MaterialTheme.colorScheme.background) {

                    if (loggedInUser == "LOADING") {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {
                        val startDest = if (loggedInUser != null) {
                            "mainpage/$loggedInUser"
                        } else {
                            "login"
                        }
                        NavHost(
                            navController = navController,
                            startDestination = startDest,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            composable("login") {
                                LoginScreen(
                                    onLoginSuccess = { loggedInUsername ->
                                        navController.navigate("mainpage/$loggedInUsername") {
                                            popUpTo("login") { inclusive = true }
                                            launchSingleTop = true
                                        }
                                    },

                                    onNavigateToCreateAccount = {
                                        navController.navigate("create_account") {
                                            launchSingleTop = true
                                        }
                                    }
                                )
                            }
                            composable("create_account") {
                                NewAccountScreen(
                                    onAccountCreated = {
                                        navController.popBackStack("login", inclusive = false)
                                    },
                                    onBackToLogin = {
                                        if (navController.previousBackStackEntry != null) {
                                            navController.popBackStack()
                                        }
                                    }
                                )
                            }
                            composable("mainpage/{username}") { backStackEntry ->
                                val username = backStackEntry.arguments?.getString("username") ?: ""
                                MainScreen(
                                    userId = loggedInUserId ?: "",
                                    username = username,
                                    onLogout = {
                                        lifecycleScope.launch {
                                            sessionManager.clearSession()
                                        }
                                        navController.navigate("login") {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
