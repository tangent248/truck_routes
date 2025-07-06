package com.group_7.truck_routes.data


import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object FireBaseRepository {
    private val db = FirebaseDatabase.getInstance().getReference("UserData")

    fun addUserData(userData: UserData) {
        val id = db.push().key!!
        db.child(id).setValue(userData.copy(id=id))

    }


    fun validateUserLogin(
        email: String,
        password: String,
        onResult: (Boolean, UserData?) -> Unit
    ) {
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(UserData::class.java)
                    if (user?.email == email && user.password == password) {
                        onResult(true, user)
                        return
                    }
                }
                onResult(false, null)
            }

            override fun onCancelled(error: DatabaseError) {
                onResult(false, null)
            }
        })
    }
}