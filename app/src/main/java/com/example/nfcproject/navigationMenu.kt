package com.example.nfcproject

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.nfcproject.databinding.FragmentNavigationMenuBinding
import com.google.android.material.navigation.NavigationView

class navigationMenu : Fragment(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding:FragmentNavigationMenuBinding
    private lateinit var  navigationView: NavigationView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNavigationMenuBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val drawer = binding.navigationMenu
        drawer.openDrawer(GravityCompat.START)
        navigationView = binding.NavigationVew
        navigationView.setNavigationItemSelectedListener(this)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val navView: NavigationView = binding.NavigationVew
        val navController = findNavController(binding.FragmentContainerView)  // находим NavController
        navView.setupWithNavController(navController)
        when (item.getItemId()) {
            R.id.profile -> {
                Log.d("NFCProjectTestDebug", "profile")

            }
            else -> false
        }
        return true
    }
}