package com.example.nfcproject

import android.content.Context
import android.content.SharedPreferences

class StudentDataStorage(context: Context) {

    enum class Prefs(val text:String){
        PASSWORD("password"),
        EMAIL("email"),
        STUDENT_ID("studentId"),
        USER_ID("userId"),
        GROUP_ID("groupId"),
        IS_ACCEPTED("isAccepted"),
        IS_ACCEPT_REQUESTED("isAcceptRequested"),
    }
    private var context = context
    private val Path = "StudentData"

    private fun getSharedPrefs() : SharedPreferences{
        return context?.getSharedPreferences(Path, Context.MODE_PRIVATE) as SharedPreferences
    }

    fun getPref(pref: StudentDataStorage.Prefs):String{
        val prefs = getSharedPrefs()
        if (prefs.contains(pref.text)) {
            return prefs.getString(pref.text,"").toString()
        }
        return ""
    }

    fun setPref(pref: StudentDataStorage.Prefs, data:String){
        val prefs = getSharedPrefs()
        val prefsEdit = prefs.edit()
        prefsEdit.putString(pref.text,data)
        prefsEdit.commit()
    }

    fun contains(pref: StudentDataStorage.Prefs):Boolean{
        val prefs = getSharedPrefs()
        return prefs.contains(pref.text)
    }
}