package com.example.nfcproject

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.nfcproject.databinding.FragmentStartBinding
import com.example.nfcproject.model.MainViewModel
import java.sql.ResultSet


class StartFragment : Fragment() {

    private lateinit var binding: FragmentStartBinding
    private val sharedViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStartBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getAuth()
    }
    fun goToAuthFragment(){
        findNavController().navigate(R.id.action_startFragment_to_authFragment)
    }
    fun goToMainFragment(){
        findNavController().navigate(R.id.action_startFragment_to_mainFragment)
    }

    private fun getAuth(){
        if (UserDataStorage(context as Context).contains(UserDataStorage.Prefs.USER_CARD_ID)){
            val StudentId = UserDataStorage(context as Context).getPref(UserDataStorage.Prefs.USER_CARD_ID)
            val StudentFName = UserDataStorage(context as Context).getPref(UserDataStorage.Prefs.USER_LOGIN)
            val StudentLName = UserDataStorage(context as Context).getPref(UserDataStorage.Prefs.USER_PASSWORD)
            if (checkCredentials(StudentId,StudentFName,StudentLName)) {
                sharedViewModel.setStudentId(StudentId)
                sharedViewModel.setStudentFName(StudentFName)
                sharedViewModel.setStudentLName(StudentLName)
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
    private fun checkCredentials(StudentId:String,StudentLoginHash:String,StudentPasswordHash:String):Boolean{
        var rs = DBConnection().readDB(String.format("SELECT StudentLogin,StudentPassword FROM Students WHERE StudentCardId = '%s';",StudentId)) as ResultSet
        var LoginHash = ""
        var PasswordHash = ""
        while (rs.next()) {
            LoginHash = rs.getString(1)
            PasswordHash = rs.getString(2)
        }
        //TODO Исправить сравнение
        // Хэш сохраняется нормально, но проблемы с кодировкой
        //? != �
        if (LoginHash != ""){
            Log.d("NFCProjectTestDebug","Полученный hash логина: "+StudentLoginHash)
            Log.d("NFCProjectTestDebug","Правильный hash логина: "+LoginHash)
            Log.d("NFCProjectTestDebug","Сравнение логинов: "+(StudentLoginHash==LoginHash))

            Log.d("NFCProjectTestDebug","Полученный hash логина: "+StudentPasswordHash)
            Log.d("NFCProjectTestDebug","Правильный hash логина: "+PasswordHash)
            Log.d("NFCProjectTestDebug","Сравнение логинов: "+(StudentPasswordHash==PasswordHash))
            if (LoginHash == StudentLoginHash && PasswordHash == StudentPasswordHash)
                return true
        }
        return false
    }
}