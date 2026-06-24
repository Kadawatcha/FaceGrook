package com.kadawatcha.atlas.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.kadawatcha.atlas.utils.SecurityUtils
import com.kadawatcha.atlas.utils.Validator


class profileViewModel : ViewModel() {
    private val db = Firebase.firestore

    var username by mutableStateOf("")
    private var initialUsername = ""

    var usernameAlreadyTaken by mutableStateOf(false)
    var usernameFormatError by mutableStateOf(false)
    var usernameHasSpace by mutableStateOf(false)

    var hasChanged by mutableStateOf(false)
    
    var password by mutableStateOf("")
    var passwordSameAsOld by mutableStateOf(false)
    var passwordFormatError by mutableStateOf(false)
    var passwordHasSpace by mutableStateOf(false)

    private var dbPasswordHash = ""

    // On utilise cet ID unique pour toutes les opérations Firestore
    // C'est plus fiable que le pseudo qui pourrait changer
    var userId by mutableStateOf("")

    var isLoading by mutableStateOf(false)

    fun onUsernameChange(newValue: String) {
        username = newValue
        usernameAlreadyTaken = false
        usernameFormatError = false
        usernameHasSpace = false
        hasChanged = username.trim() != initialUsername.trim()
    }
    
    fun onPasswordChange(newPassword: String){
        password = newPassword
        passwordSameAsOld = false
        passwordFormatError = false
        passwordHasSpace = false
        hasChanged = username.trim() != initialUsername.trim() || password.isNotEmpty()
    }
    
    /**
     * Charge le profil à partir de l'ID unique (récupéré au login)
     */
    fun loadUserProfile(id: String) {
        if (id.isBlank()) return
        this.userId = id
        isLoading = true
        db.collection("users").document(id).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val fetchedUsername = document.getString("username") ?: ""
                    this.username = fetchedUsername
                    this.initialUsername = fetchedUsername
                    this.dbPasswordHash = document.getString("password") ?: ""
                    this.password = ""
                    this.hasChanged = false
                }
                isLoading = false
            }
            .addOnFailureListener {
                isLoading = false
            }
    }

    /**
     * Sauvegarde les modifications en utilisant l'ID unique comme référence
     */
    fun saveUserProfile() {
        if (userId.isBlank()) return

        // Reset des erreurs
        usernameAlreadyTaken = false
        usernameFormatError = false
        usernameHasSpace = false
        passwordFormatError = false
        passwordHasSpace = false
        passwordSameAsOld = false

        // Validation Username
        if (username.contains(" ")) {
            usernameHasSpace = true
            return
        }
        if (!Validator.isUsernameValid(username)) {
            usernameFormatError = true
            return
        }
        
        // Validation Password (uniquement s'il n'est pas vide)
        if (password.isNotEmpty()) {
            if (password.contains(" ")) {
                passwordHasSpace = true
                return
            }
            if (!Validator.isPasswordValid(password)) {
                passwordFormatError = true
                return
            }
        }

        isLoading = true
        // On utilise update pour ne modifier que les champs nécessaires sans écraser le reste du document
        db.collection("users").document(userId).get().addOnSuccessListener { document ->
            val oldUsername = document.getString("username") ?: ""

            if (username.trim() == oldUsername) {
                performSave()
            } else {
                Validator.checkUsernameAvailability(db, username) { isAvailable ->
                    if (isAvailable) {
                        performSave()
                    } else {
                        usernameAlreadyTaken = true
                        isLoading = false
                    }
                }
            }
        }
    }


    private fun performSave() {
        isLoading = true
        val savedUsername = username.trim()
        val updates = mutableMapOf<String, Any>("username" to savedUsername)
        
        if (password.isNotEmpty()) {
            val hashedPassword = SecurityUtils.hashPassword(password)
            if (hashedPassword == dbPasswordHash) {
                passwordSameAsOld = true
                isLoading = false
                return
            }
            updates["password"] = hashedPassword
        }

        db.collection("users").document(userId)
            .update(updates)
            .addOnCompleteListener {
                isLoading = false
                if (it.isSuccessful) {
                    initialUsername = savedUsername
                    if (password.isNotEmpty()) {
                        dbPasswordHash = SecurityUtils.hashPassword(password)
                    }
                    password = ""
                    hasChanged = false
                }
            }
    }
}
