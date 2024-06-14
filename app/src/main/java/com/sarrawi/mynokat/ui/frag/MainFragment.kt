package com.sarrawi.mynokat.ui.frag

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.sarrawi.mynokat.R
import android.animation.Animator
import android.animation.AnimatorListenerAdapter

import com.sarrawi.mynokat.databinding.FragmentMainBinding


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

//        val adapter = ViewPagerAdapter(childFragmentManager)
//
//        adapter.addFragment(NokatFragment(), "الكلمات")
//
//        adapter.addFragment(ImgFragment(),"الصور")
//
//        binding.vpager.adapter=adapter
//        binding.tblayout.setupWithViewPager(binding.vpager)

        val img:Button=view.findViewById(R.id.img)
        val words:Button=view.findViewById(R.id.words)



        img.setOnClickListener {
            it.animate().apply {
                duration = 1000  // مدة الرسوم المتحركة بالمللي ثانية
                rotationYBy(360f)  // يدور العنصر حول المحور Y بمقدار 360 درجة
                setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        // الانتقال إلى ImgFragment بعد انتهاء الرسوم المتحركة
                        val directions = MainFragmentDirections.actionMainFragment2ToImgFragment()
                        findNavController().navigate(directions)
                    }
                })
            }.start()
        }

        words.setOnClickListener {
            it.animate().apply {
                duration = 1000  // مدة الرسوم المتحركة بالمللي ثانية
                rotationYBy(360f)  // يدور العنصر حول المحور Y بمقدار 360 درجة
                setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        // الانتقال إلى ImgFragment بعد انتهاء الرسوم المتحركة
                        val directions = MainFragmentDirections.actionMainFragment2ToNokatFragment()
                        findNavController().navigate(directions)
                    }
                })
            }.start()
        }




    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}