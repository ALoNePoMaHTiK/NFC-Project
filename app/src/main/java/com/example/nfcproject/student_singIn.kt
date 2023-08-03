package com.example.nfcproject

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.nfcproject.databinding.FragmentStudentSingInBinding
import com.example.nfcproject.model.StudentViewModel


class student_singIn : Fragment() {


    private lateinit var binding: FragmentStudentSingInBinding
    private val studentViewModel: StudentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStudentSingInBinding.inflate(inflater,container, false)
        return binding.root
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
        }
        binding.SingInButton.setOnClickListener { sendDataSingIn() }
    }

    private fun sendDataSingIn() {
        sendDataViewModel()
        val email = studentViewModel.email.value.toString()
        val password = studentViewModel.password.value.toString()
        if(checkingCoorrectString(email,1)&&
            isCorrectEmail(email) && isCorrectEmail(email)) showMessage(email)
        else{
            showMessage("Введен не корректный адрес электроннйо почты")
            return
        }
        if(checkingCoorrectString(password,1)) showMessage(email)
        else {
            showMessage("Введен не корректный адрес пароль")
            return
        }
    }
    private fun checkingCoorrectString(string: String, minLength: Int ): Boolean{
        return !string.isNullOrEmpty() && string.length >= minLength
    }
    private fun isCorrectEmail(email: String): Boolean{
        val indexDog = email.indexOf('@')
        val indexDot = email.indexOf('.')
        return (indexDog < indexDot && indexDog > 3 && indexDot <= email.length - 2)
    }

    private fun sendDataViewModel(){
        studentViewModel.setStudentEmail(binding.email.text.toString())
        studentViewModel.setStudentPassword(binding.password.text.toString())
    }
    private fun showMessage(message: String) = Toast.makeText(activity, message, Toast.LENGTH_LONG).show()



}