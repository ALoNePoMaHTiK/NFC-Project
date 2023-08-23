package com.example.nfcproject.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nfcproject.model.APIModels.DBAPI.Tag

class MainViewModel : ViewModel() {

    //Идентификатор аудитории
    private val _auditoryId = MutableLiveData<String>()
    val auditoryId: LiveData<String> = _auditoryId

    //Номер студенческого билета
    private val _studentCardId = MutableLiveData<String>()
    val studentCardId: LiveData<String> = _studentCardId

    // Имя студента
    private val _studentLName = MutableLiveData<String>()
    val studentLName: LiveData<String> = _studentLName

    // Фамилия студента
    private val _studentFName = MutableLiveData<String>()
    val studentFName: LiveData<String> = _studentFName

    private val _stateNFC = MutableLiveData<Boolean>()
    val stateNFC: LiveData<Boolean> = _stateNFC

    private val _tag = MutableLiveData<Tag?>()
    val tag: LiveData<Tag?> = _tag

    init {
        resetData()
    }

    /**
     * Изменение номера студенческого билета
     *
     * @param studentId номер студенческого билета
     */
    fun setStudentCardId(studentCardId: String) {
        _studentCardId.value = studentCardId
    }

    /**
     * Изменение идентификатора аудитории
     * @param auditoryId идентификатор аудитории
     */

    fun setTag(tag: Tag? ) {
        _tag.value = tag
    }

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
        _stateNFC.value = state
    }
    fun setStudentLName(studentLName: String ) {
        _studentLName.value = studentLName
    }
    private fun resetData() {
        _studentCardId.value = ""
        _auditoryId.value = ""
        _studentFName.value = ""
        _studentLName.value = ""
        onNFC()
        //TODO добавить определение включенность NFC службы
    }
}