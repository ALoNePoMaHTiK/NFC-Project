package com.example.nfcproject

import android.content.Context
import android.content.SharedPreferences
import android.provider.ContactsContract.Data

class UserDataStorage {

    enum class Prefs(val text:String){
        USER_PASSWORD("UserPassword"),
        USER_LOGIN("UserLogin"),
        USER_CARD_ID("UserCardId")
    }

    private val USER_DATA = "UserData"

    private fun getSharedPrefs(context: Context) : SharedPreferences{
        return context?.getSharedPreferences(USER_DATA, Context.MODE_PRIVATE) as SharedPreferences
    }

    fun getPref(context: Context,pref:Prefs):String{
        val prefs = getSharedPrefs(context)
        if (prefs.contains(pref.text)) {
            return prefs.getString(pref.text,"").toString()
        }
        return ""
    }

    fun setPref(context: Context,pref:Prefs,data:String){
        val prefs = getSharedPrefs(context)
        val prefsEdit = prefs.edit()
        prefsEdit.putString(pref.text,data)
    }
}