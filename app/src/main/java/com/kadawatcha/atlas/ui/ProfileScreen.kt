package com.kadawatcha.atlas.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kadawatcha.atlas.viewmodel.profileViewModel


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

    Column(
        modifier = modifier
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
            value = viewModel.password,
            onValueChange = { viewModel.onPasswordChange(it) },
            label = "Password",
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
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !viewModel.isLoading && viewModel.hasChanged && (viewModel.password.isEmpty() || repeatPassword == viewModel.password)
        ) {
            Text("Sauvegarder")
        }
    }

}
