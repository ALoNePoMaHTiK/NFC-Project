package com.example.nfcproject

import RetrofitHelper
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.nfcproject.databinding.FragmentWaitingAcceptBinding
import com.example.nfcproject.model.APIModels.Student
import com.example.nfcproject.model.StudentViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class WaitingAccept : Fragment() {
    private lateinit var binding: FragmentWaitingAcceptBinding
    private val studentViewModel: StudentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWaitingAcceptBinding.inflate(inflater,container, false)
        return binding.root
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            sviewModel = studentViewModel
        }
        binding.UpdateButton.setOnClickListener { resetStudent()
        if (studentViewModel.isAccepted.value!!){
            goToMainFragment()
        }}
    }

    fun resetStudent(){
        val api = RetrofitHelper().getInstance().create(DBAPI::class.java)
        api.GetStudentById(studentViewModel.studentId.value.toString())
            .enqueue(object : Callback<Student> {
                override fun onFailure(call: Call<Student>, t: Throwable) {
                    showLog("NFCProjectTestDebug","Проблема с подключением к API")
                    showLog("NFCProjectTestDebug",call.request().toString())
                    showLog("NFCProjectTestDebug",t.message.toString())
                    showLog("NFCProjectTestDebug",t.localizedMessage)
                }
                override fun onResponse(call: Call<Student>, response: Response<Student>) {
                    if (response.isSuccessful) {
                        showLog("NFCProjectTestDebug","Success")
                        studentViewModel.setStudent(
                            response.body()?.email.toString(),
                            response.body()?.password.toString(),
                            response.body()?.userId!!.toInt(),
                            response.body()?.groupId.toString(),
                            response.body()?.studentId.toString(),
                            response.body()?.isAccepted!!,
                            response.body()?.isAcceptRequested!!
                        )
                        saveStudentData()
                    }
                    if (response.code() == 404) {
                        showLog("NFCProjectTestDebug :","Неверные данные")
                    }
                    showLog("NFCProjectTestDebug","Код ответа : " + response?.code().toString())
                }
            })
    }

    private fun goToMainFragment(){
        findNavController().navigate(R.id.action_waitingAccept_to_mainFragment)
    }

    private fun saveStudentData(){
        var sds = StudentDataStorage(context as Context)
        sds.setPref(StudentDataStorage.Prefs.STUDENT_ID,studentViewModel.studentId.value.toString())
        sds.setPref(StudentDataStorage.Prefs.PASSWORD,studentViewModel.password.value.toString())
        sds.setPref(StudentDataStorage.Prefs.EMAIL,studentViewModel.email.value.toString())
        sds.setPref(StudentDataStorage.Prefs.USER_ID,studentViewModel.userId.value.toString())
        sds.setPref(StudentDataStorage.Prefs.GROUP_ID,studentViewModel.groupId.value.toString())
        sds.setPref(StudentDataStorage.Prefs.IS_ACCEPTED,studentViewModel.isAccepted.value.toString())
        sds.setPref(StudentDataStorage.Prefs.IS_ACCEPT_REQUESTED,studentViewModel.isAcceptRequested.value.toString())
    }
    private fun showLog(tag: String, msg: String) = Log.d(tag, msg)
}
