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
    private val _RoomId = MutableLiveData<Short>()
    val RoomId: LiveData<Short> = _RoomId

    fun set(LessonName:String, StartDateTime:String, FinishDateTime:String, RoomId: Short ){
        _LessonName.value = LessonName
        _StartDateTime.value = StartDateTime
        _FinishDateTime.value = FinishDateTime
        _RoomId.value = RoomId
    }

    fun reset(){
        _LessonName.value = "Нет пар"
        _StartDateTime.value = "--:--"
        _FinishDateTime.value = "--:--"
        _RoomId.value = 0
    }
}