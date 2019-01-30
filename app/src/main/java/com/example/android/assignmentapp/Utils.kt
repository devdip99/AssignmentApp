package com.example.android.assignmentapp

import android.content.Context
import android.content.Context.MODE_PRIVATE


fun Context.saveUserEmail(email: String) {
    val pref = this.getSharedPreferences("User_pref", MODE_PRIVATE)
    val editor = pref.edit().putString("user_email", email).apply()
}

fun Context.getUserEmail(): String? {
    val pref = this.getSharedPreferences("User_pref", MODE_PRIVATE)
    return pref.getString("user_email", null)
}

fun Context.removeUserEmail() {
    val pref = getSharedPreferences("User_pref", MODE_PRIVATE)
    val editor = pref.edit().clear().apply()
}