package com.example.nfcproject.UI

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.nfcproject.Handlers.DBConnection
import com.example.nfcproject.Handlers.UserDataStorage
import com.example.nfcproject.databinding.FragmentAuthBinding
import com.example.nfcproject.model.MainViewModel

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
        val StudentCardId = binding.studentCardId.text.toString()
        val StudentFName = binding.fName.text.toString()
        val StudentLName = binding.lName.text.toString()

        if(inputValidation(StudentCardId, StudentFName, StudentLName)) {
            if(saveDataToDB()){
                sendDataViewModel()
                savePreferences()
                //findNavController().navigate(R.id.action_authFragment_to_mainFragment)
            }
       }
    }
    private fun inputValidation(StudentCardId:String, StudentFName:String, StudentLName:String): Boolean{
        val fNameRegex = """[а-яА-Я]{3,15}""".toRegex()
        val lNameRegex = """[а-яА-Я]{2,15}""".toRegex()
        val studentIdRegex = """\d{2}[а-яА-Я]\d{4}""".toRegex()
        return if(studentIdRegex.matches(StudentCardId) && fNameRegex.matches(StudentFName) && lNameRegex.matches(StudentLName)) {
            true
        }
        else {
            if (fNameRegex.matches(StudentFName) || lNameRegex.matches(StudentLName))
                showMessage("Фамилия или имя введены некоректно")
            else
                showMessage("Номер студенческого билета введен некоректно")
            false
        }
    }


    private fun saveDataToDB():Boolean{
        val StudentCardId = binding.studentCardId.text.toString().uppercase()
        val StudentFName = binding.fName.text.toString()
        val StudentLName = binding.lName.text.toString()
        val rs = DBConnection().searchStudentByCardId(StudentCardId)
        //Значит такой пользователь уже есть
        if (rs.next()){
            return if (checkCredentials(StudentCardId, StudentFName, StudentLName)){
                true
            } else{
                showMessage("Неверные имя и фамилия!")
                false
            }
        }
        else
            DBConnection().addNewStudent(StudentCardId, StudentFName, StudentLName)
        return true
    }

    private fun sendDataViewModel() {
        sharedViewModel.setStudentCardId(binding.studentCardId.text.toString())
        sharedViewModel.setStudentFName(binding.fName.text.toString())
        sharedViewModel.setStudentLName(binding.lName.text.toString())
        sharedViewModel.onNFC()
    }

    private fun savePreferences(){
        val studentSalt = DBConnection().getSault(sharedViewModel.studentCardId.value.toString())
        val studentLoginHash = DBConnection().getHash(studentSalt+sharedViewModel.studentFName.value.toString())
        val studentPasswordHash = DBConnection().getHash(studentSalt+sharedViewModel.studentLName.value.toString())
        UserDataStorage(context as Context).setPref(UserDataStorage.Prefs.USER_CARD_ID, sharedViewModel.studentCardId.value.toString())
        UserDataStorage(context as Context).setPref(UserDataStorage.Prefs.USER_LOGIN, studentLoginHash)
        UserDataStorage(context as Context).setPref(UserDataStorage.Prefs.USER_PASSWORD, studentPasswordHash)
    }

    private fun checkCredentials(StudentCardId:String, StudentFName:String, StudentLName:String):Boolean{
        val credential = DBConnection().getStudentCredentials(StudentCardId)
        val studentLoginHash = DBConnection().getHash(credential[2] + StudentFName)
        val studentPasswordHash = DBConnection().getHash(credential[2] + StudentLName)
        return (credential[0] == studentLoginHash && credential[1] == studentPasswordHash)
    }

    private fun showMessage(message: String) = Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
}