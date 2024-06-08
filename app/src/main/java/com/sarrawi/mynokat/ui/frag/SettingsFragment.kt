package com.sarrawi.mynokat.ui.frag

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableStringBuilder
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sarrawi.img.utils.Utils
import com.sarrawi.mynokat.BuildConfig
import com.sarrawi.mynokat.R
import com.sarrawi.mynokat.databinding.FragmentSettingsBinding


class SettingsFragment : Fragment() {

    private lateinit var _binding: FragmentSettingsBinding
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        binding.shareApp.setOnClickListener {
            Utils.shareApp(requireContext())
        }

        appVers()
        privacypoli()
        rateApp()


        return binding.root
    }

    private fun privacypoli() {
        binding.privacy.setOnClickListener {

            val url =
                "https://sites.google.com/view/mymsgs/%D8%A7%D9%84%D8%B5%D9%81%D8%AD%D8%A9-%D8%A7%D9%84%D8%B1%D8%A6%D9%8A%D8%B3%D9%8A%D8%A9" // استبدل هذا برابط سياسة الخصوصية الخاص بك
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }

    fun rateApp() {
        binding.range.setOnClickListener {
            val uri = Uri.parse("market://details?id=" + requireContext().packageName)
            val gotoMarket = Intent(Intent.ACTION_VIEW, uri)
            gotoMarket.addFlags(
                (Intent.FLAG_ACTIVITY_NO_HISTORY or
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            )

            try {
                startActivity(gotoMarket)
            } catch (e: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + requireContext().packageName)
                    )
                )
            }
        }
    }


    private fun appVers() {
        val appVersion = "${BuildConfig.VERSION_NAME}"

        // قم بدمج النصوص باستخدام SpannableStringBuilder
        val builder = SpannableStringBuilder()
        val staticText = "إصدار التطبيق\n"
        val dynamicText = appVersion
//        val redColor = ForegroundColorSpan(resources.getColor(android.R.color.holo_red_light)) // احمر
//        val blackColor = ForegroundColorSpan(resources.getColor(android.R.color.black)) // اسود
        builder.append(staticText)
        builder.append(dynamicText)
//        builder.setSpan(blackColor, 0, staticText.length, 0) // تطبيق اللون الأسود على النص الثابت
//        builder.setSpan(redColor, staticText.length, builder.length, 0) // تطبيق اللون الأحمر على النص المتغير

        // قم بتعيين النص وتنسيقه في TextView
        binding.vers.text = builder
    }
}