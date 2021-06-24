/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package com.pintraveler.firekit

import android.util.Log
import com.pintraveler.ptkit.Observable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.pintraveler.ptkit.EmptyFieldException

enum class AuthState { AUTHENTICATED, UNAUTHENTICATED, UNINITIALIZED }

open class AuthManager: Observable<AuthState>() {
    override val TAG: String = "AuthManager"
    var state: AuthState = AuthState.UNINITIALIZED

    var currentUser: FirebaseUser? = null
        get() = FirebaseAuth.getInstance().currentUser

    override fun getObservableValue(): AuthState { return state }

    fun startAuthListener() {
        Log.d(TAG, "Auth Listener Starting...")
        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            val lastState = state
            if(auth.currentUser != null){
                state = AuthState.AUTHENTICATED
            }
            else {
                state = AuthState.UNAUTHENTICATED
            }
            Log.d(TAG, "Auth Stage Changed from $lastState to $state")
            onModify(lastState, state)
            initialized = true
        }
    }

    fun login(email: String, password: String, completion: ((String?) -> Unit)? = null){
        if(email == "" || password == ""){
            completion?.invoke("Please fill in email and password")
            return
        }
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                //if(it.)
                completion?.invoke("")
            }
            .addOnFailureListener { completion?.invoke(it.toString()) }
    }
}