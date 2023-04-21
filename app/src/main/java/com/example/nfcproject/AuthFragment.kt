package com.example.nfcproject

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.isDigitsOnly
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
        val studentId = binding.studentId.text.toString()
        val fName = binding.fName.text.toString()
        val lName = binding.lName.text.toString()

        if(inputValidation(studentId, fName, lName)) {
            if(saveDataToDB()){
                sendDataViewModel()
                savePreferences()
                findNavController().navigate(R.id.action_authFragment_to_mainFragment)
            }
       }

    }
    private fun inputValidation(studentId:String, fName:String, lName:String): Boolean{
        val countСharacter = 7
        val fNameRegex = """[а-яА-Я]{3,15}""".toRegex()
        val lNameRegex = """[а-яА-Я]{2,15}""".toRegex()
        val studentIdRegex = """\d{2}[а-яА-Я]\d{4}""".toRegex()
        return if(studentIdRegex.matches(studentId) && fNameRegex.matches(fName) && lNameRegex.matches(lName)) {
            true
        }
        else {
            if (fNameRegex.matches(fName) || lNameRegex.matches(lName))
                showMessage("Фамилия или Имя введены некоректно")
            else
                showMessage("Номер студенческого билета введен некоректно")
            false
        }
    }


    private fun saveDataToDB():Boolean{
        var StudentId = binding.studentId.text.toString()
        var StudentFName = binding.fName.text.toString()
        var StudentLName = binding.lName.text.toString()
        var rs = DBConnection().readDB(String.format("SELECT StudentLogin,StudentPassword FROM Students WHERE StudentCardId = '%s';",StudentId)) as ResultSet
        //Значит такой пользователь уже есть
        if (rs.next()){
            if (checkCredentials(StudentId,StudentFName,StudentLName)){
                return true
            }
            else{
                showMessage("Неверные имя и фамилия!")
                return false
            }
        }
        else
            DBConnection().writeDB(String.format("INSERT INTO Students(StudentCardId,StudentLogin,StudentPassword) VALUES ('%s','%s','%s');",StudentId,StudentFName,StudentLName))
        return true
    }

    private fun sendDataViewModel() {
        sharedViewModel.setStudentCardId(binding.studentId.text.toString())
        sharedViewModel.setStudentFName(binding.fName.text.toString())
        sharedViewModel.setStudentLName(binding.lName.text.toString())
        sharedViewModel.onNFC()
    }

    private fun savePreferences(){
        val studentSalt = DBConnection().getSault(sharedViewModel.studentCardId.value.toString())
        val studentLoginHash = DBConnection().getHash(studentSalt+sharedViewModel.studentFName.value.toString())
        val studentPasswordHash = DBConnection().getHash(studentSalt+sharedViewModel.studentLName.value.toString())
        UserDataStorage(context as Context).setPref(UserDataStorage.Prefs.USER_CARD_ID,sharedViewModel.studentCardId.value.toString())
        UserDataStorage(context as Context).setPref(UserDataStorage.Prefs.USER_LOGIN,studentLoginHash)
        UserDataStorage(context as Context).setPref(UserDataStorage.Prefs.USER_PASSWORD,studentPasswordHash)
    }

    private fun checkCredentials(StudentId:String,StudentFName:String,StudentLName:String):Boolean{
        var rs = DBConnection().readDB(String.format("SELECT StudentLogin,StudentPassword,Salt FROM Students WHERE StudentCardId = '%s';",StudentId)) as ResultSet
        var DBLoginHash = ""
        var DBPasswordHash = ""
        var Sault = ""
        while (rs.next()) {
            DBLoginHash = rs.getString(1).toByteArray(Charsets.UTF_16).toString(Charsets.UTF_16)
            DBPasswordHash = rs.getString(2).toByteArray(Charsets.UTF_16).toString(Charsets.UTF_16)
            Sault = rs.getString(3)
        }
        var StudentLoginHash = DBConnection().getHash(Sault+StudentFName)
        var StudentPasswordHash = DBConnection().getHash(Sault+StudentLName)
        if (DBLoginHash==StudentLoginHash && DBPasswordHash == StudentPasswordHash){
            return true
        }
        return false
    }

    private fun showMessage(message: String) = Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
}