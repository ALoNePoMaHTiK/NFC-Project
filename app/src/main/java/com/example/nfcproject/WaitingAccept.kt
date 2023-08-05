package com.example.nfcproject

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.nfcproject.databinding.FragmentWaitingAcceptBinding
import com.example.nfcproject.model.StudentViewModel

class WaitingAccept : Fragment() {
    private lateinit var binding: FragmentWaitingAcceptBinding
    private val studentViewModel: StudentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWaitingAcceptBinding.inflate(inflater,container, false)
        return binding.root
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            sviewModel = studentViewModel
        }
    }


}