package com.reezkyillma.projectandroid.Notifications


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdReceiver
import com.google.firebase.messaging.FirebaseMessagingService


class MyFirebaseIdService : FirebaseMessagingService() {

    override fun onNewToken(s: String?) {
        super.onNewToken(s)
        val firebaseUser = FirebaseAuth.getInstance().currentUser

        val refreshToken = FirebaseInstanceId.getInstance().token
        if (firebaseUser != null) {
            updateToken(refreshToken)
        }
    }

    private fun updateToken(refreshToken: String?) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser

        val reference = FirebaseDatabase.getInstance().getReference("Tokens")
        val token = Token(refreshToken!!)
        reference.child(firebaseUser!!.uid).setValue(token)
    }
}
