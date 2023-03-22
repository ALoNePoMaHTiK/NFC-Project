package com.example.nfcproject

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.nfc.NfcAdapter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.nfcproject.databinding.FragmentDataSendBinding
import com.example.nfcproject.model.MainViewModel
import java.sql.ResultSet

class DataSendFragment : Fragment() {

    private lateinit var binding: FragmentDataSendBinding
    private val sharedViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDataSendBinding.inflate(inflater,container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
        }
        sharedViewModel.studentId.value?.let { showLog("TestMVVM", it) }
        checkCredentials(sharedViewModel.studentId.value.toString(),sharedViewModel.studentFName.value.toString(),sharedViewModel.studentLName.value.toString())
        MainActivity().readNFC()

    }

    fun checkCredentials(StudentId:String,StudentFName:String,StudentLName:String){
        var rs = DBConnection().readDB(String.format("SELECT StudentLogin,StudentPassword,Sault FROM Students WHERE StudentCardId = '%s';",StudentId)) as ResultSet
        var LoginHash = ""
        var PasswordHash = ""
        var Sault = ""
        while (rs.next()) {
            LoginHash = rs.getString(1)
            PasswordHash = rs.getString(2)
            Sault = rs.getString(3)
        }
        var UserLogin = ""
        rs = DBConnection().readDB(String.format("EXEC DoubleHash '%s','%s'",Sault,StudentFName)) as ResultSet
        while (rs.next()) {
            UserLogin = rs.getString(1)
        }
        var UserPassword = ""
        rs = DBConnection().readDB(String.format("EXEC DoubleHash '%s','%s'",Sault,StudentLName)) as ResultSet
        while (rs.next()) {
            UserPassword = rs.getString(1)
        }
        Log.d("NFCProjectTestDebug","Полученный hash логина: "+UserLogin)
        Log.d("NFCProjectTestDebug","Правильный hash логина: "+LoginHash)
        Log.d("NFCProjectTestDebug","Полученный hash логина: "+UserPassword)
        Log.d("NFCProjectTestDebug","Правильный hash логина: "+PasswordHash)
    }

    fun checkAuth():Boolean{
        var prefs = context?.getSharedPreferences("UserData", Context.MODE_PRIVATE) as SharedPreferences
        if(prefs.contains("UserCardId") && prefs.contains("UserLogin") && prefs.contains("UserPassword")) {
            sharedViewModel.setStudentId(prefs.getString("UserCardId",null).toString())
            sharedViewModel.setStudentFName(prefs.getString("UserLogin",null).toString())
            sharedViewModel.setStudentLName(prefs.getString("UserPassword",null).toString())
            return true
        }
        return false
    }

    private fun showLog(tag: String, msg: String){
        Log.d(tag, msg)
    }
}