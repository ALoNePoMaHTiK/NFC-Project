package com.example.nfcproject.UI

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.example.nfcproject.R
import com.example.nfcproject.databinding.FragmentProfileBinding
import com.example.nfcproject.model.APIModels.DBAPI.ProfileViewModel


class Profile : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var sharedViewModel: ProfileViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentProfileBinding.inflate(inflater,container, false)
        val root: View = binding!!.root
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
        return root
    }


}