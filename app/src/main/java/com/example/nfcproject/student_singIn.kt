package com.example.nfcproject

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.nfcproject.databinding.FragmentStudentSingInBinding
import com.example.nfcproject.model.APIModels.AuthData
import com.example.nfcproject.model.APIModels.Student
import com.example.nfcproject.model.StudentViewModel
import kotlinx.coroutines.awaitAll
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.await
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Thread.sleep


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
        callAPI(AuthData(email,password))
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

    private fun callAPI(authData: AuthData){
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.88.21:5084/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build()
        val api = retrofit.create(DBAPI::class.java)
        api.Auth(authData)
            .enqueue(object : Callback<Student> {
                override fun onFailure(call: Call<Student>, t: Throwable) {
                    showLog("NFCProjectTestDebug :","Проблема с подключением к API")
                    showLog("NFCProjectTestDebug :",call.request().toString())
                    showLog("NFCProjectTestDebug :",t.message.toString())
                    showLog("NFCProjectTestDebug :",t.localizedMessage)
                }
                override fun onResponse(call: Call<Student>, response: Response<Student>) {
                    if (response.isSuccessful) {
                        showLog("NFCProjectTestDebug :","Success")
                        studentViewModel.setStudent(response.body()?.email.toString(),
                            response.body()?.password.toString(),
                            response.body()?.userId!!.toInt(),
                            response.body()?.groupId.toString(),
                            response.body()?.studentId.toString())
                    }
                    if (response.code() == 404) {
                        showLog("NFCProjectTestDebug :","Неверные данные")
                    }
                    showLog("NFCProjectTestDebug","Код ответа : " + response?.code().toString())
                }
            })
    }

    private fun showLog(tag: String, msg: String) = Log.d(tag, msg)
    private fun showError(tag: String, msg: String) = Log.e(tag, msg)

}