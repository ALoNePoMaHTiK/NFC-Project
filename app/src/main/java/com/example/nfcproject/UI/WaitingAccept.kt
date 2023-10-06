package com.example.nfcproject.UI

import com.example.nfcproject.Handlers.RetrofitHelper
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.nfcproject.Handlers.StudentDataStorage
import com.example.nfcproject.R
import com.example.nfcproject.databinding.FragmentWaitingAcceptBinding
import com.example.nfcproject.model.StudentViewModel
import com.example.nfcproject.Services.DBAPI
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

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
        binding.UpdateButton.setOnClickListener {
            if (resetStudent()){
                chageStudentState()
                goToMainFragment()
            }}
//        GlobalScope.launch (Dispatchers.Main) {
//            var result = false
//            while(!result){
//                Thread.sleep(3000)
//                result = resetStudent()
//            }
//            chageStudentState()
//            goToMainFragment()
//        }
    }

    fun resetStudent() : Boolean{
        val api = RetrofitHelper().getInstance().create(DBAPI::class.java)
        var result = false
        runBlocking{
            GlobalScope.launch {
                var result1 = api.GetStudentByIdSuspend(studentViewModel.studentId.value.toString())
                if (result1.isSuccessful)
                    result = result1.body()?.isAccepted!!
            }.join()
        }
        return result
    }

    private fun goToMainFragment(){
        findNavController().navigate(R.id.action_waitingAccept_to_profile)
    }

    private fun chageStudentState(){
        showLog("chageStudentState")
        try{
            studentViewModel.setIsAccepted(true)
            studentViewModel.setIsAcceptRequested(false)
        }
        catch(e:Exception){
            showLog(e.stackTraceToString())
        }
        saveStudentData()
    }

    private fun saveStudentData(){
        showLog("saveStudentData")
        var sds = StudentDataStorage(context as Context)
        sds.setPref(StudentDataStorage.Prefs.STUDENT_ID,studentViewModel.studentId.value.toString())
        sds.setPref(StudentDataStorage.Prefs.PASSWORD,studentViewModel.password.value.toString())
        sds.setPref(StudentDataStorage.Prefs.EMAIL,studentViewModel.email.value.toString())
        sds.setPref(StudentDataStorage.Prefs.USER_ID,studentViewModel.userId.value.toString())
        sds.setPref(StudentDataStorage.Prefs.GROUP_ID,studentViewModel.groupId.value.toString())
        sds.setPref(StudentDataStorage.Prefs.IS_ACCEPTED,studentViewModel.isAccepted.value.toString())
        sds.setPref(StudentDataStorage.Prefs.IS_ACCEPT_REQUESTED,studentViewModel.isAcceptRequested.value.toString())
    }
    private fun showLog(msg: String) = Log.d("NFCProjectTestDebug", msg)
}
