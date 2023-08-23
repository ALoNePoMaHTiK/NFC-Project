package com.example.nfcproject.model.APIModels.DBAPI

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileViewModel: ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text
    private val _status = MutableLiveData<String>().apply {
        value = "error"
    }
    val status: LiveData<String> = _status

    fun setStatus() {
        _text.value = "success"
    }
}