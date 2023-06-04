package com.example.nfcproject.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class JournalViewModel: ViewModel() {
    //Нвазвания пары
    private val _nameLesson  = MutableLiveData<String>()
    val nameLesson: LiveData<String> = _nameLesson

    //Время начала пары
    private val _timeStartLesson = MutableLiveData<String>()
    val timeStartLesson: LiveData<String> = _timeStartLesson

    // Время конца пары
    private val _timeEndtLesson = MutableLiveData<String>()
    val timeEndtLesson: LiveData<String> = _timeEndtLesson

    // сообщение
    private val _мessage = MutableLiveData<String>()
    val mes: LiveData<String> = _мessage

    fun setTextMessage(text: String){
        _мessage.value = text
    }
    init {
        resetData()
    }
    fun setLesson(name: String, timeStart: String, timeEnd: String ){
        setNameLesson(name)
        setTimeStartLesson(timeStart)
        setTimeEndLesson(timeEnd)

    }
    fun setLesson (name: String, timeLesson: Pair<String, String>){
        setNameLesson(name)
        setTimeStartLesson(timeLesson.first)
        setTimeEndLesson(timeLesson.second)
    }
    fun setNameLesson(name: String){
        _nameLesson.value = name
    }
    fun setTimeEndLesson(timeEnd: String){
        _timeEndtLesson.value = timeEnd
    }
    fun setTimeStartLesson(timeStart: String){
        _timeStartLesson.value = timeStart
    }
    fun resetData(){
        _nameLesson.value = "Нет занятий"
        _timeStartLesson.value = "--:--"
        _timeEndtLesson.value = "--:--"
        _мessage.value = "Дождитесь начала пары"
    }
}