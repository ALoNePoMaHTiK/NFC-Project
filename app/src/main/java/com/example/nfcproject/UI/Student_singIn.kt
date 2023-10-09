package com.example.nfcproject.UI

import com.example.nfcproject.Handlers.RetrofitHelper
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.nfcproject.R
import com.example.nfcproject.Handlers.StudentDataStorage
import com.example.nfcproject.databinding.FragmentStudentSingInBinding
import com.example.nfcproject.model.APIModels.DBAPI.AuthData
import com.example.nfcproject.model.APIModels.DBAPI.Student
import com.example.nfcproject.model.StudentViewModel
import com.example.nfcproject.Services.DBAPI
import com.example.nfcproject.model.APIModels.DBAPI.User
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.concurrent.thread


class student_singIn : Fragment() {


    private lateinit var binding: FragmentStudentSingInBinding
    private val studentViewModel: StudentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStudentSingInBinding.inflate(inflater,container, false)
        return binding.root
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
        }
        binding.SingInButton.setOnClickListener { sendDataSingIn() }
    }

    private fun sendDataSingIn() {
        sendDataViewModel()
        val email = studentViewModel.email.value.toString()
        val password = studentViewModel.password.value.toString()
        if(checkingCoorrectString(email,1)&&
            isCorrectEmail(email) && isCorrectEmail(email)) showMessage(email)
        else{
            showMessage("Введен не корректный адрес электроннйо почты")
            return
        }
        if(checkingCoorrectString(password,1)) showMessage(email)
        else {
            showMessage("Введен не корректный адрес пароль")
            return
        }
        val api = RetrofitHelper().getInstance().create(DBAPI::class.java)
        var student = Student()
        var user= User()
        var isSuccess = false
        runBlocking {
            GlobalScope.launch {
                var result1 = api.AuthSuspend(AuthData(email,password))
                var result2 = api.GetUserById(result1.body()!!.userId)
                showLog("Student = " + result1.body().toString())
                showLog("User = " + result2.body().toString())
                if (result2.isSuccessful && result1.isSuccessful){
                    isSuccess = true
                    student = result1.body()!!
                    user = result2.body()!!
                }
            }.join()
        }

        if (isSuccess){
            studentViewModel.setStudent(
                student.email,
                student.password,
                student.userId,
                student.groupId,
                student.studentId,
                student.isAccepted,
                student.isAcceptRequested,
                user.name
            )
            saveStudentData()
            goToWaitingAccept()
        }
        else{
            showMessage("Пользователь не найден")
            showLog("Пользователь не найден")
        }
    }
    private fun checkingCoorrectString(string: String, minLength: Int ): Boolean{
        return !string.isNullOrEmpty() && string.length >= minLength
    }
    private fun isCorrectEmail(email: String): Boolean{
        val indexDog = email.indexOf('@')
        val indexDot = email.indexOf('.')
        return (indexDog < indexDot && indexDog > 3 && indexDot <= email.length - 2)
    }

    private fun sendDataViewModel(){
        studentViewModel.setStudentEmail(binding.email.text.toString())
        studentViewModel.setStudentPassword(binding.password.text.toString())
    }
    private fun showMessage(message: String) = Toast.makeText(activity, message, Toast.LENGTH_LONG).show()

    private fun showLog(msg: String) = Log.d("NFCProjectTestDebug", msg)

    private fun saveStudentData(){
        var sds = StudentDataStorage(context as Context)
        sds.setPref(StudentDataStorage.Prefs.STUDENT_ID,studentViewModel.studentId.value.toString())
        sds.setPref(StudentDataStorage.Prefs.PASSWORD,studentViewModel.password.value.toString())
        sds.setPref(StudentDataStorage.Prefs.EMAIL,studentViewModel.email.value.toString())
        sds.setPref(StudentDataStorage.Prefs.USER_ID,studentViewModel.userId.value.toString())
        sds.setPref(StudentDataStorage.Prefs.GROUP_ID,studentViewModel.groupId.value.toString())
        sds.setPref(StudentDataStorage.Prefs.IS_ACCEPTED,studentViewModel.isAccepted.value.toString())
        sds.setPref(StudentDataStorage.Prefs.IS_ACCEPT_REQUESTED,studentViewModel.isAcceptRequested.value.toString())
        sds.setPref(StudentDataStorage.Prefs.USER_FULL_NAME,studentViewModel.userFullName.value.toString())
    }

    private fun goToWaitingAccept(){
        findNavController().navigate(R.id.action_student_singIn_to_waitingAccept)
    }

}