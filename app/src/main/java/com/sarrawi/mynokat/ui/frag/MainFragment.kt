package com.sarrawi.mynokat.ui.frag

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sarrawi.mynokat.adapter.ViewPagerAdapter

import com.sarrawi.mynokat.databinding.FragmentMainBinding
import com.sarrawi.mynokat.ui.frag.tabs.ImgFragment
import com.sarrawi.mynokat.ui.frag.tabs.NokatFragment


class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ViewPagerAdapter(childFragmentManager)

        adapter.addFragment(NokatFragment(), "الكلمات")

        adapter.addFragment(ImgFragment(),"الصور")

        binding.vpager.adapter=adapter
        binding.tblayout.setupWithViewPager(binding.vpager)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}