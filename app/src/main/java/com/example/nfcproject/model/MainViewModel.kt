package com.example.nfcproject.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainViewModel : ViewModel() {

    //Идентификатор студента
    private val _studentId = MutableLiveData<String>()
    val studentId: LiveData<String> = _studentId

    //Идентификатор аудитории
    private val _auditoryId = MutableLiveData<String>()
    val auditoryId: LiveData<String> = _auditoryId

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

    fun resetData() {
        _studentId.value = ""
        _auditoryId.value = ""
    }
}