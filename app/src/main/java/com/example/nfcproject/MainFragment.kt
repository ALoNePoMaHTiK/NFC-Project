package com.example.nfcproject

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.nfcproject.databinding.FragmentMainBinding
import com.example.nfcproject.model.MainViewModel

class MainFragment : Fragment() {


    private lateinit var binding: FragmentMainBinding
    private val sharedViewModel: MainViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
        }
        binding.SaveButton.setOnClickListener {saveButtonHendler()}

    }
    private fun saveButtonHendler(){
        if(inputValidation(binding.studentId.text.toString())) {
            sendDataViewModel()
            findNavController().navigate(R.id.action_mainFragment2_to_dataSendFragment)
        }

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

    private fun sendDataViewModel() {
        sharedViewModel.setStudentId(binding.studentId.text.toString())
        sharedViewModel.setStudentFName(binding.fName.text.toString())
        sharedViewModel.setStudentLName(binding.lName.text.toString())

        sharedViewModel.onNFC()
    }
    private fun showLog(tag: String, msg: String){
        Log.d(tag, msg)
    }
}