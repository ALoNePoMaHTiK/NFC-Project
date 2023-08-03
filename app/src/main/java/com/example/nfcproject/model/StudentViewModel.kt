package com.example.nfcproject.model
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
class StudentViewModel : ViewModel() {

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email
    private val _password = MutableLiveData<String>()
    val password: LiveData<String> = _password

    fun setStudentEmail(email: String)
    {
        _email.value = email
    }
    fun setStudentPassword(password: String){
        _password.value = password
    }

}