package com.sarrawi.mynokat.ui.frag.nokat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sarrawi.mynokat.R
import com.sarrawi.mynokat.databinding.FragmentFavNokatBinding
import com.sarrawi.mynokat.databinding.FragmentNokatBinding


class FavNokatFragment : Fragment() {

    private var _binding: FragmentFavNokatBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFavNokatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        val bottomNav: BottomNavigationView = binding.bottomNavNokat

        // ربط BottomNavigationView مع NavController
        bottomNav.setupWithNavController(navController)

    }

}