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
import com.example.nfcproject.Handlers.DBConnection
import com.example.nfcproject.R
import com.example.nfcproject.Handlers.StudentDataStorage
import com.example.nfcproject.Handlers.UserDataStorage
import com.example.nfcproject.databinding.FragmentStartBinding
import com.example.nfcproject.model.APIModels.DBAPI.AuthData
import com.example.nfcproject.model.MainViewModel
import com.example.nfcproject.model.StudentViewModel
import com.example.nfcproject.Services.DBAPI
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class StartFragment : Fragment() {

    private lateinit var binding: FragmentStartBinding
    private val sharedViewModel: MainViewModel by activityViewModels()
    private val studentViewModel: StudentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStartBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Navigate()
    }
    private fun goToAuthFragment(){
        findNavController().navigate(R.id.action_startFragment_to_authFragment)
    }
    private fun goToMainFragment(){
        findNavController().navigate(R.id.action_startFragment_to_profile)
    }

    private fun Navigate(){
        var sds = StudentDataStorage(context as Context)
        if(sds.contains(StudentDataStorage.Prefs.IS_ACCEPT_REQUESTED)){
            studentViewModel.setStudent(
                sds.getPref(StudentDataStorage.Prefs.EMAIL),
                sds.getPref(StudentDataStorage.Prefs.PASSWORD),
                sds.getPref(StudentDataStorage.Prefs.USER_ID).toInt(),
                sds.getPref(StudentDataStorage.Prefs.GROUP_ID),
                sds.getPref(StudentDataStorage.Prefs.STUDENT_ID),
                sds.getPref(StudentDataStorage.Prefs.IS_ACCEPTED).toBoolean(),
                sds.getPref(StudentDataStorage.Prefs.IS_ACCEPT_REQUESTED).toBoolean(),
            )
            if(checkAuth()){
                if(sds.getPref(StudentDataStorage.Prefs.IS_ACCEPT_REQUESTED).toBoolean())
                    goToWaitingAcceptFragment()
                if(sds.getPref(StudentDataStorage.Prefs.IS_ACCEPTED).toBoolean())
                    goToMainFragment()
            }
        }
        else
            goToSignInFragment()
    }

    //TODO добавить проверку isAccepted
    private fun checkAuth() : Boolean{
        val api = RetrofitHelper().getInstance().create(DBAPI::class.java)
        var result = false
        runBlocking {
            launch {
                val response = api.CheckAuth(AuthData(studentViewModel.email.value.toString(),studentViewModel.password.value.toString()))
                if (response != null){
                    result = true
                }
                else{
                    showLog("Сохраненные данные не соотвествуют актуальным данным!")
                }
            }.join()
        }

        return result
    }

    private fun getAuth(){
        if (UserDataStorage(context as Context).contains(UserDataStorage.Prefs.USER_CARD_ID)){
            val StudentCardId = UserDataStorage(context as Context).getPref(UserDataStorage.Prefs.USER_CARD_ID)
            val StudentFName = UserDataStorage(context as Context).getPref(UserDataStorage.Prefs.USER_LOGIN)
            val StudentLName = UserDataStorage(context as Context).getPref(UserDataStorage.Prefs.USER_PASSWORD)
            if (checkCredentials(StudentCardId,StudentFName,StudentLName)) {
                sharedViewModel.setStudentCardId(StudentCardId)
                sharedViewModel.setStudentFName(StudentFName)
                sharedViewModel.setStudentLName(StudentLName)
                sharedViewModel.onNFC()
                goToMainFragment()
            }
            else{
                goToAuthFragment()
            }
        }
        else{
            goToAuthFragment()
        }
    }
    private fun checkCredentials(StudentId: String, StudentLoginHash: String, StudentPasswordHash: String): Boolean{
        val credential = DBConnection().getStudentCredentials(StudentId)
        return (credential[0] != "" && credential[0] == StudentLoginHash && credential[1] == StudentPasswordHash)
    }

    private fun goToWaitingAcceptFragment(){
        findNavController().navigate(R.id.action_student_singIn_to_waitingAccept)
    }
    private fun goToSignInFragment(){
        findNavController().navigate(R.id.action_startFragment_to_student_singIn)
    }

    private fun showLog(msg: String) = Log.d("NFCProjectTestDebug", msg)
}