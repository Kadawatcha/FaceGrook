package com.kadawatcha.atlas.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kadawatcha.atlas.ui.theme.AppTheme
import com.kadawatcha.atlas.viewmodel.MainViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    userId: String,
    username: String,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    // On passe la Factory pour que Android sache comment créer le ViewModel avec son SettingsManager
    viewModel: MainViewModel = viewModel(factory = MainViewModel.Factory)
) {

    // collectAsStateWithLifecycle est la version optimisée pour Compose qui 
    // arrête d'écouter quand l'écran n'est plus visible (gain de batterie).
    val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()

    // NAv contrller pour basculler entre les différents menus de mon app
    val navController = rememberNavController()

    // On récupère la route actuelle pour savoir quel bouton du menu "allumer" (selected)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val topAppTitle = when (currentRoute) {
        "home" -> "Atlas"
        "chat" -> "Messages"
        "friends" -> "Friends"
        "profile" -> "Atlas - your profile"
        else -> "Atlas"
    }

    AppTheme(
        darkTheme = isDarkTheme,
        dynamicColor = true
    ) { // false pour utiliser nos couleurs personnalisées
        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = {
                        Column(modifier = Modifier.padding(top = 12.dp)) {
                            PageTitle(
                                text = topAppTitle,
                                color = MaterialTheme.colorScheme.onBackground,
                                textAlign = TextAlign.Start,
                                fontSize = 36.sp,
                            )
                        }
                    },
                    actions = {
                        if (currentRoute == "home") {
                            MainTopMenu(
                                isDarkTheme = isDarkTheme,
                                onToggleTheme = { viewModel.toggleTheme() },
                                onLogoutClick = {
                                    onLogout()
                                }
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },


            bottomBar = {
                NavigationBar(
                    windowInsets = NavigationBarDefaults.windowInsets,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                ) {

                    CustomNavItem(
                        selected = currentRoute == "home",
                        icon = Icons.Default.Home,
                        contentDescription = "home",
                        onClick = { navController.navigate("home") }
                    )

                    CustomNavItem(
                        selected = currentRoute == "chat",
                        icon = Icons.Default.ChatBubbleOutline,
                        contentDescription = "chat",
                        onClick = { navController.navigate("chat") }
                    )

                    CustomNavItem(
                        selected = currentRoute == "friends",
                        icon = Icons.Default.LocalFireDepartment,
                        contentDescription = "friends",
                        onClick = { navController.navigate("friends") }
                    )
                    CustomNavItem(
                        selected = currentRoute == "profile",
                        icon = Icons.Default.AccountCircle,
                        contentDescription = "profile",
                        onClick = { navController.navigate("profile") }
                    )

                }
            }


        ) { innerPadding ->

            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                color = MaterialTheme.colorScheme.background
            ) {
                // Le NavHost définit quel écran afficher selon la "route" actuelle
                NavHost(
                    navController = navController,
                    startDestination = "home" // Route au démarrage
                ) {
                    // Route pour l'accueil
                    composable("home") {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Spacer(Modifier.height(16.dp))

                            // Un petit "widget" de bienvenue arrondi style Material You
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = RoundedCornerShape(32.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(28.dp)
                                ) {
                                    Text(
                                        text = "Hello $username !",
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        text = "Welcome to Atlas",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }

                    // Route pour le profil
                    composable("profile") {
                        ProfileScreen(userId = userId)
                    }

                    // Route chat
                    composable("chat") {
                        // .
                    }

                    // Route pour les amis
                    composable("friends") {
                        val dummyUsers = remember {
                            List(10) { i ->
                                com.kadawatcha.atlas.model.User(
                                    id = "$i",
                                    username = "Ami #$i",
                                    role = if (i % 2 == 0) "Member Atlas" else "Explorer"
                                )
                            }
                        }

                        androidx.compose.foundation.lazy.LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 16.dp)
                        ) {
                            item {
                                Text(
                                    text = "My Friends",
                                    style = MaterialTheme.typography.headlineSmall,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                            }
                            items(dummyUsers) { user ->
                                UserCard(user = user)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MainTopMenu(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onLogoutClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = RoundedCornerShape(16.dp),
            // border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            DropdownMenuItem(
                text = { Text("Settings") },
                onClick = { expanded = false },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null
                    )
                }
            )

            DropdownMenuItem(
                text = {
                    ThemeToggleRow(
                        isDark = isDarkTheme,
                        onToggle = onToggleTheme
                    )
                },
                onClick = onToggleTheme,
                leadingIcon = {
                    Icon(
                        imageVector = if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.WbSunny,
                        contentDescription = null
                    )
                }
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp))

            DropdownMenuItem(
                text = { Text("Logout", color = MaterialTheme.colorScheme.error) },
                onClick = {
                    expanded = false
                    onLogoutClick()
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            )
        }
    }
}


@Composable
fun ThemeToggleRow(
    isDark: Boolean,
    onToggle: () -> Unit // Une fonction "callback"
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = if (isDark) "Dark" else "Light")
        Spacer(Modifier.width(8.dp))
        Switch(checked = isDark, onCheckedChange = { onToggle() })
    }
}
