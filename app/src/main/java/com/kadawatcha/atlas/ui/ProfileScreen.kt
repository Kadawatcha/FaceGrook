package com.kadawatcha.atlas.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kadawatcha.atlas.viewmodel.profileViewModel
import kotlinx.coroutines.launch


@Composable
fun ProfileScreen(
    userId: String,
    modifier: Modifier = Modifier,
    viewModel: profileViewModel = viewModel(),
) {

    LaunchedEffect(userId) {
        viewModel.loadUserProfile(userId)
    }

    var repeatPassword by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color.Transparent // Utilise containerColor pour le fond du Scaffold
    ) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues) // Applique le padding du Scaffold ici
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Edit your profile",
                style = MaterialTheme.typography.headlineSmall
            )

            if (viewModel.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    // .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            CustomInput(
                modifier = Modifier.semantics {
                    contentType = ContentType.Username
                },
                value = viewModel.username,
                onValueChange = { viewModel.onUsernameChange(it) },
                label = "Username",
                isError = viewModel.usernameAlreadyTaken || viewModel.usernameFormatError || viewModel.usernameHasSpace,
                supportingText = {
                    when {
                        viewModel.usernameAlreadyTaken -> {
                            Text(
                                text = "Pseudo déjà utilisé",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        viewModel.usernameHasSpace -> {
                            Text(
                                text = "Les espaces ne sont pas autorisés",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        viewModel.usernameFormatError -> {
                            Text(
                                text = "Le pseudo doit faire au moins 3 caractères",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomInput(
                modifier = Modifier.semantics {
                    contentType = ContentType.Password
                },
                value = viewModel.password,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = "Password",
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = viewModel.passwordSameAsOld || viewModel.passwordFormatError || viewModel.passwordHasSpace,
                supportingText = {
                    when {
                        viewModel.passwordSameAsOld -> {
                            Text(
                                text = "Le nouveau mot de passe doit être différent de l'ancien",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        viewModel.passwordHasSpace -> {
                            Text(
                                text = "Les espaces ne sont pas autorisés",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        viewModel.passwordFormatError -> {
                            Text(
                                text = "Le mot de passe doit faire au moins 8 caractères",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomInput(
                value = repeatPassword,
                onValueChange = { repeatPassword = it },
                label = "Repeat password",
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (!viewModel.isLoading &&
                            viewModel.hasChanged &&
                            !viewModel.usernameAlreadyTaken &&
                            !viewModel.usernameFormatError &&
                            !viewModel.usernameHasSpace &&
                            !viewModel.passwordFormatError &&
                            !viewModel.passwordSameAsOld &&
                            !viewModel.passwordHasSpace &&
                            (viewModel.password.isEmpty() || repeatPassword == viewModel.password)
                        ) {
                            viewModel.saveUserProfile()
                        }
                    }
                ),
                isError = repeatPassword.isNotEmpty() && repeatPassword != viewModel.password,
                supportingText = {
                    if (repeatPassword.isNotEmpty() && repeatPassword != viewModel.password) {
                        Text(
                            text = "Les mots de passe ne correspondent pas",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.saveUserProfile()
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Sauvegardé !",
                            duration = SnackbarDuration.Short
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !viewModel.isLoading &&
                        viewModel.hasChanged &&
                        !viewModel.usernameAlreadyTaken &&
                        !viewModel.usernameFormatError &&
                        !viewModel.usernameHasSpace &&
                        !viewModel.passwordFormatError &&
                        !viewModel.passwordSameAsOld &&
                        !viewModel.passwordHasSpace &&
                        (viewModel.password.isEmpty() || repeatPassword == viewModel.password)
            ) {
                Text("Sauvegarder")
            }
        }

    }
}