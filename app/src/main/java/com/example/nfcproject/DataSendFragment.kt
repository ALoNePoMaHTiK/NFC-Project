package com.example.nfcproject

import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.nfcproject.databinding.FragmentDataSendBinding


class DataSendFragment : Fragment() {

    private lateinit var binding: FragmentDataSendBinding
    private lateinit var nfcAdapter: NfcAdapter

    private var nfcTag:String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDataSendBinding.inflate(inflater,container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        a = 10
    }
}