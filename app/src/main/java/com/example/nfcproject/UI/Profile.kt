package com.example.nfcproject.UI

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.nfcproject.Handlers.WaitingAcceptWorker
import com.example.nfcproject.R
import com.example.nfcproject.databinding.FragmentProfileBinding
import com.example.nfcproject.model.APIModels.DBAPI.ProfileViewModel
import com.example.nfcproject.model.StudentViewModel
import java.util.concurrent.TimeUnit


class Profile : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var sharedViewModel: ProfileViewModel
    private val studentViewModel: StudentViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentProfileBinding.inflate(inflater,container, false)
        sharedViewModel = ViewModelProvider(requireActivity()).get(ProfileViewModel::class.java)
        sharedViewModel.setStatus()
        sharedViewModel.text.observe(viewLifecycleOwner) {
            if(it == "success") {
                // выставляем для drawer режим - Unlocked
                requireActivity().findViewById<DrawerLayout>(R.id.drawer_layout)
                    .setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                }
            requireActivity().findViewById<Toolbar>(R.id.toolbar).setVisibility(View.VISIBLE)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            sviewModel = studentViewModel
        }
        val proc3 = PeriodicWorkRequestBuilder<WaitingAcceptWorker>(3,TimeUnit.MINUTES).addTag("song").build()
        WorkManager.getInstance(requireContext()).enqueue(proc3)
    }


}