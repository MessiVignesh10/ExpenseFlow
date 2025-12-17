package com.example.expenseflow.data.repository

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth : FirebaseAuth = FirebaseAuth.getInstance()

    suspend fun signUp(email : String , pass : String){
        auth.createUserWithEmailAndPassword(email , pass).await()
    }

    suspend fun signIn(email: String , pass: String){
        auth.signInWithEmailAndPassword(email , pass).await()
    }

    fun getCurrentUser() = auth.currentUser

    fun signOut(){
        auth.signOut()
    }
}