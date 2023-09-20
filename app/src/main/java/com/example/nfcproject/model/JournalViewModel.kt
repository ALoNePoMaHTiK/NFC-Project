package com.example.nfcproject.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class JournalViewModel: ViewModel() {

    private val _cookie  = MutableLiveData<String>()
    val cookie: LiveData<String> = _cookie

    fun setCookie(cookie:String){
        _cookie.value = cookie
    }
}