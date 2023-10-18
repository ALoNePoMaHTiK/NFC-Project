package com.example.nfcproject.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat

class JournalViewModel: ViewModel() {

    private val _LessonName = MutableLiveData<String>()
    val LessonName: LiveData<String> = _LessonName

    private val _StartDateTime = MutableLiveData<String>()
    val StartDateTime: LiveData<String> = _StartDateTime

    private val _FinishDateTime = MutableLiveData<String>()
    val FinishDateTime: LiveData<String> = _FinishDateTime

    fun set(LessonName:String,StartDateTime:String,FinishDateTime:String){
        _LessonName.value = LessonName
        _StartDateTime.value = StartDateTime
        _FinishDateTime.value = FinishDateTime
    }

    fun reset(){
        _LessonName.value = "Нет пар"
        _StartDateTime.value = ""
        _FinishDateTime.value = ""
    }

    fun getStartTime() : String{
        if (_StartDateTime.value != null && _StartDateTime.value != "")
            return SimpleDateFormat("HH:mm").format(_StartDateTime.value)
        else
            return ""
    }

}