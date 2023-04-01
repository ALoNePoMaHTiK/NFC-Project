package com.example.nfcproject

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
import com.example.nfcproject.databinding.FragmentAuthBinding
import com.example.nfcproject.model.MainViewModel
import java.sql.ResultSet

class AuthFragment : Fragment() {


    private lateinit var binding: FragmentAuthBinding
    private val sharedViewModel: MainViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAuthBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
        }
        binding.SaveButton.setOnClickListener {saveButtonHandler()}

    }
    private fun saveButtonHandler(){
        if(inputValidation(binding.studentId.text.toString())) {
            saveDataToDB()
            sendDataViewModel()
            savePreferences()
            findNavController().navigate(R.id.action_authFragment_to_mainFragment)
        }

    }
    //TODO Добавить валидацию для остальных полей
    private fun inputValidation(inputData:String): Boolean{
        val countСharacter = 7
        return if(countСharacter == inputData.length) {
            true
        }
        else {
            showMessage("Номер студенческого билета введен некоректно")
            false
        }
    }


    private fun saveDataToDB():Boolean{
        var StudentId =binding.studentId.text.toString()
        var StudentFName = binding.fName.text.toString()
        var StudentLName = binding.lName.text.toString()
        var rs = DBConnection().readDB(String.format("SELECT StudentLogin,StudentPassword FROM Students WHERE StudentCardId = '%s';",StudentId)) as ResultSet
        //Значит такой пользователь уже есть
        if (rs.next()){
            if (checkCredentials(StudentId,StudentFName,StudentLName)){
                DBConnection().writeDB(String.format("INSERT INTO Students(StudentId,StudentLogin,StudentPassword) VALUES ('%s','%s','%s');",StudentId,StudentFName,StudentLName))
            }
            else{
                showMessage("Неверные имя и фамилия!")
                return false
            }
        }
        DBConnection().writeDB(String.format("INSERT INTO Students(StudentId,StudentLogin,StudentPassword) VALUES ('%s','%s','%s');",StudentId,StudentFName,StudentLName))
        return true
    }

    private fun sendDataViewModel() {
        sharedViewModel.setStudentId(binding.studentId.text.toString())
        sharedViewModel.setStudentFName(binding.fName.text.toString())
        sharedViewModel.setStudentLName(binding.lName.text.toString())
        sharedViewModel.onNFC()
    }

    private fun savePreferences(){
        UserDataStorage(context as Context).setPref(UserDataStorage.Prefs.USER_CARD_ID,sharedViewModel.studentId.value.toString())
        UserDataStorage(context as Context).setPref(UserDataStorage.Prefs.USER_LOGIN,sharedViewModel.studentFName.value.toString())
        UserDataStorage(context as Context).setPref(UserDataStorage.Prefs.USER_PASSWORD,sharedViewModel.studentLName.value.toString())
    }

    private fun checkCredentials(StudentId:String,StudentFName:String,StudentLName:String):Boolean{
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

        if (UserLogin==LoginHash && UserPassword == PasswordHash){
            return true
        }
        return false
    }

    private fun showMessage(message: String) = Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
}