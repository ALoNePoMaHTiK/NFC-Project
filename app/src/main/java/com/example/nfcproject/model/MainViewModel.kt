package com.example.nfcproject.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    //Идентификатор аудитории
    private val _auditoryId = MutableLiveData<String>()
    val auditoryId: LiveData<String> = _auditoryId

    //Идентификатор студента
    private val _studentId = MutableLiveData<String>()
    val studentId: LiveData<String> = _studentId

    // Имя студента
    private val _studentLName = MutableLiveData<String>()
    val studentLName: LiveData<String> = _studentLName

    // Фамилия студента
    private val _studentFName = MutableLiveData<String>()
    val studentFName: LiveData<String> = _studentFName

    private val _stateFNC = MutableLiveData<Boolean>()
    val stateFNC: LiveData<Boolean> = _stateFNC
    init {
        resetData()
    }

    /**
     * Изменение идентификатора студента
     *
     * @param studentId идентификатор студента
     */
    fun setStudentId(studentId: String) {
        _studentId.value = studentId
    }

    /**
     * Изменение идентификатора аудитории
     * @param auditoryId идентификатор аудитории
     */
    fun setAuditoryId(auditoryId: String) {
        _auditoryId.value = auditoryId
    }
    fun setStudentFName(studentFName: String ) {
        _studentFName.value = studentFName
    }
    fun onNFC(){
        setStateNFC(true)
    }
    fun offNFC(){
        setStateNFC(false)
    }
    private fun setStateNFC(state: Boolean){
        _stateFNC.value = state
    }
    fun setStudentLName(studentLName: String ) {
        _studentLName.value = studentLName
    }
    fun resetData() {
        _studentId.value = ""
        _auditoryId.value = ""
        _studentFName.value = ""
        _studentLName.value = ""
        _stateFNC.value = false
    }
}