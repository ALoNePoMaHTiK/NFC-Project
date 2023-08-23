package com.example.nfcproject.Hendlers

import android.content.Context
import android.content.SharedPreferences

class UserDataStorage(context: Context) {

    enum class Prefs(val text:String){
        USER_PASSWORD("UserPassword"),
        USER_LOGIN("UserLogin"),
        USER_CARD_ID("UserCardId")
    }
    private var context = context
    private val USER_DATA = "UserData"

    private fun getSharedPrefs() : SharedPreferences{
        return context?.getSharedPreferences(USER_DATA, Context.MODE_PRIVATE) as SharedPreferences
    }

    fun getPref(pref: Prefs):String{
        val prefs = getSharedPrefs()
        if (prefs.contains(pref.text)) {
            return prefs.getString(pref.text,"").toString()
        }
        return ""
    }

    fun setPref(pref: Prefs, data:String){
        val prefs = getSharedPrefs()
        val prefsEdit = prefs.edit()
        prefsEdit.putString(pref.text,data)
        prefsEdit.commit()
    }

    fun contains(pref: Prefs):Boolean{
        val prefs = getSharedPrefs()
        return prefs.contains(pref.text)
    }
}