package com.example.nfcproject

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import com.example.nfcproject.databinding.FragmentNavigationMenuBinding

class navigationMenu : Fragment() {

    private lateinit var binding:FragmentNavigationMenuBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNavigationMenuBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val drawer = binding.navigationMenu
        drawer.openDrawer(GravityCompat.START);
        super.onViewCreated(view, savedInstanceState)
    }
}