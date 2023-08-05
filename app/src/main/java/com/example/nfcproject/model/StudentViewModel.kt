package com.example.nfcproject.model
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
class StudentViewModel : ViewModel() {

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email
    private val _password = MutableLiveData<String>()
    val password: LiveData<String> = _password
    private val _studentId = MutableLiveData<String>()
    val studentId: LiveData<String> = _studentId
    private val _userId = MutableLiveData<Int>()
    val userId: LiveData<Int> = _userId
    private val _groupId = MutableLiveData<String>()
    val groupId: LiveData<String> = _groupId

    fun setStudentEmail(email: String)
    {
        _email.value = email
    }
    fun setStudentPassword(password: String){
        _password.value = password
    }

    fun setUserId(userId: Int)
    {
        _userId.value = userId
    }

    fun setGroupId(groupId: String){
        _groupId.value = groupId
    }

    fun setStudentId(studentId: String){
        _studentId.value = studentId
    }

    fun setStudent(email: String,password: String,userId: Int,groupId: String,studentId: String){
        setStudentEmail(email)
        setStudentPassword(password)
        setUserId(userId)
        setGroupId(groupId)
        setStudentId(studentId)
    }

}