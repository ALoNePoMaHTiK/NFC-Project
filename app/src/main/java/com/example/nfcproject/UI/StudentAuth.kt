package com.example.nfcproject.UI
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.nfcproject.databinding.FragmentStudentAuthBinding

class StudentAuth : Fragment() {

    private lateinit var binding: FragmentStudentAuthBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStudentAuthBinding.inflate(inflater,container, false)
        return binding.root
    }

}