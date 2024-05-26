package com.sarrawi.mynokat.ui.frag.img

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sarrawi.mynokat.databinding.FragmentFavBinding


class FavFragment : Fragment() {

    private var _binding: FragmentFavBinding? = null


    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFavBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        val bottomNav: BottomNavigationView = binding.bottomNavImg

        // ربط BottomNavigationView مع NavController
        bottomNav.setupWithNavController(navController)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}