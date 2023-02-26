package com.example.nfcproject

import android.os.Bundle
import android.os.Message
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.nfcproject.databinding.FragmentMainBinding
class MainFragment : Fragment() {


    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.SaveButton.setOnClickListener {saveButtonHendler()}
    }
    private fun saveButtonHendler(){
        if(inputValidation(binding.editTextStudentNumber.text.toString()))findNavController()
            .navigate(R.id.action_mainFragment2_to_dataSendFragment)
    }
    private fun saveUserInputData(inputData:String){
        if(inputValidation(inputData)){
            studNumber = inputData.uppercase()
            Log.d("NFCProjectTestDebug","Номер студенческого: "+studNumber)
            /// запись в файл!!!!
            showMessage("Данные сохранены")
        }
    }
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
    private fun showMessage(message: String) = Toast.makeText(activity, message, Toast.LENGTH_LONG).show()

}