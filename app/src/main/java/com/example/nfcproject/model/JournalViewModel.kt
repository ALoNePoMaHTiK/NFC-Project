package com.example.nfcproject.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class JournalViewModel: ViewModel() {

    private val _titleLessons = MutableLiveData<String>()
    val titleLesons: LiveData<String> = _titleLessons

    private val _timeStartLessons = MutableLiveData<String>()
    val timeStartLessons: LiveData<String> = _timeStartLessons

    private val _timeEndLessons = MutableLiveData<String>()
    val timeEndLessons: LiveData<String> = _timeEndLessons

}