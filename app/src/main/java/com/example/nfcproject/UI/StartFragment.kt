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
import com.example.nfcproject.R
import com.example.nfcproject.Handlers.StudentDataStorage
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
                sds.getPref(StudentDataStorage.Prefs.USER_FULL_NAME),
            )
            if(checkAuth()){
                if(sds.getPref(StudentDataStorage.Prefs.IS_ACCEPT_REQUESTED).toBoolean())
                    goToWaitingAcceptFragment()
                if(sds.getPref(StudentDataStorage.Prefs.IS_ACCEPTED).toBoolean())
                    goToMainFragment()
            }
            else{
                clearSharedPreferenses()
                goToSignInFragment()
            }
        }
        else
            goToSignInFragment()
    }

    //TODO использовать worker для последовательных запросов
    private fun checkAuth() : Boolean{
        val api = RetrofitHelper().getInstance().create(DBAPI::class.java)
        var result = false
        runBlocking {
            launch {
                val response = api.CheckAuth(AuthData(studentViewModel.email.value.toString(),studentViewModel.password.value.toString()))
                if (response != null &&
                        response.body()?.email == studentViewModel.email.value &&
                        response.body()?.password == studentViewModel.password.value &&
                        response.body()?.groupId == studentViewModel.groupId.value &&
                        response.body()?.userId == studentViewModel.userId.value &&
                        response.body()?.isAccepted == studentViewModel.isAccepted.value &&
                        response.body()?.isAcceptRequested == studentViewModel.isAcceptRequested.value
                        ){
                    result = true
                }
                else{
                    showLog("Сохраненные данные не соотвествуют актуальным данным!")
                }
            }.join()
        }

        return result
    }

    private fun clearSharedPreferenses(){
        StudentDataStorage(context as Context).clear(StudentDataStorage.Prefs.STUDENT_ID)
        StudentDataStorage(context as Context).clear(StudentDataStorage.Prefs.USER_FULL_NAME)
        StudentDataStorage(context as Context).clear(StudentDataStorage.Prefs.EMAIL)
        StudentDataStorage(context as Context).clear(StudentDataStorage.Prefs.GROUP_ID)
        StudentDataStorage(context as Context).clear(StudentDataStorage.Prefs.USER_ID)
        StudentDataStorage(context as Context).clear(StudentDataStorage.Prefs.IS_ACCEPTED)
        StudentDataStorage(context as Context).clear(StudentDataStorage.Prefs.IS_ACCEPT_REQUESTED)
        StudentDataStorage(context as Context).clear(StudentDataStorage.Prefs.PASSWORD)
    }

    private fun goToWaitingAcceptFragment(){
        findNavController().navigate(R.id.action_startFragment_to_waitingAccept)
    }
    private fun goToSignInFragment(){
        findNavController().navigate(R.id.action_startFragment_to_student_singIn)
    }

    private fun showLog(msg: String) = Log.d("NFCProjectTestDebug", msg)
}