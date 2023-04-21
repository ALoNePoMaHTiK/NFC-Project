package com.example.nfcproject

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.nfcproject.databinding.FragmentStartBinding
import com.example.nfcproject.model.MainViewModel


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
    private fun goToAuthFragment(){
        findNavController().navigate(R.id.action_startFragment_to_authFragment)
    }
    private fun goToMainFragment(){
        findNavController().navigate(R.id.action_startFragment_to_mainFragment)
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
}