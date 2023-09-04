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
    private val _isAccepted = MutableLiveData<Boolean>()
    val isAccepted: LiveData<Boolean> = _isAccepted
    private val _isAcceptRequested = MutableLiveData<Boolean>()
    val isAcceptRequested: LiveData<Boolean> = _isAcceptRequested
    private val _userFullName = MutableLiveData<String>()
    val userFullName: LiveData<String> = _userFullName

    private val _userFirstName = MutableLiveData<String>()
    val userFirestName: LiveData<String> = _userFirstName
    private val _userSecondName = MutableLiveData<String>()
    val userSecondName: LiveData<String> = _userSecondName
    private val _userPatronymic = MutableLiveData<String>()
    val userPatronymic: LiveData<String> = _userPatronymic


    fun setStudentEmail(email: String) {_email.value = email}
    fun setStudentPassword(password: String) {_password.value = password }
    fun setUserId(userId: Int) {_userId.value = userId}
    fun setGroupId(groupId: String){ _groupId.value = groupId }
    fun setStudentId(studentId: String){ _studentId.value = studentId}
    fun setIsAccepted(isAccepted: Boolean){ _isAccepted.value = isAccepted}
    fun setIsAcceptRequested(isAcceptRequested: Boolean){ _isAcceptRequested.value = isAcceptRequested}
    fun setUserFullName(userFullName: String){ _userFullName.value = userFullName}
    private fun setUserFirestName(userFirestName: String){_userFirstName.value = userFirestName}
    private fun setUserSecondName(userSecondName: String){_userSecondName.value = userSecondName}
    private fun setUserPatronymic(userPatronymic: String){_userPatronymic.value = userPatronymic}

    fun setStudent(email: String,password: String,userId: Int,groupId: String,
                   studentId: String,isAccepted: Boolean,isAcceptRequested:
                   Boolean,userFullName: String){
        setStudentEmail(email)
        setStudentPassword(password)
        setUserId(userId)
        setGroupId(groupId)
        setStudentId(studentId)
        setIsAccepted(isAccepted)
        setIsAcceptRequested(isAcceptRequested)
        setUserFullName(userFullName)
        val fio = userFullName.split(' ')
        setUserFirestName(fio[0])
        setUserSecondName(fio[1])
        setUserPatronymic(fio[2])
    }
}