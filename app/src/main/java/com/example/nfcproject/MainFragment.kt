package com.example.nfcproject

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.nfcproject.databinding.FragmentMainBinding
import com.example.nfcproject.model.JournalViewModel
import com.example.nfcproject.model.MainViewModel

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private val sharedViewModel: MainViewModel by activityViewModels()
    private val journalViewModel: JournalViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater,container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
            jViewModel = journalViewModel
        }
    }


    private fun showLog(tag: String, msg: String) = Log.d(tag, msg)
}