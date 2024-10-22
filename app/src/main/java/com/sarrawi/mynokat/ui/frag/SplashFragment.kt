package com.sarrawi.mynokat.ui.frag

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.sarrawi.mynokat.R

class SplashFragment : Fragment() {

    private lateinit var emojiTextView: TextView
    private val emojis = listOf("😀", "😂", "😅", "😍", "😎", "😭", "😡")
    private var currentIndex = 0
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var animJo: AnimationDrawable
    private lateinit var imageView: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        emojiTextView = view.findViewById(R.id.emojiTextView)

         imageView = view.findViewById(R.id.imageView2)
        imageView.setBackgroundResource(R.drawable.anim)
        animJo = imageView.background as AnimationDrawable

        // ابدأ تشغيل الأنيميشن هنا مباشرةً
        animJo.start()

        // تشغيل الأنيميشن الأول
        animateEmoji()

        // بدء التبديل بين الرموز التعبيرية
        handler.postDelayed(object : Runnable {
            override fun run() {
                // تغيير الرمز التعبيري
                currentIndex = (currentIndex + 1) % emojis.size
                emojiTextView.text = emojis[currentIndex]
                // إعادة تشغيل الأنيميشن
                animateEmoji()
                // التبديل بين الرموز التعبيرية كل 3 ثوانٍ
                handler.postDelayed(this, 1000)
            }
        }, 1000)

        // الانتقال إلى الشاشة الرئيسية بعد 5 ثوانٍ
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            Handler(Looper.getMainLooper()).postDelayed({
                findNavController()
                    .navigate(
                        R.id.action_splashFragment_to_mainFragment2,
                        null,
                        NavOptions.Builder()
                            .setPopUpTo(
                                R.id.splashFragment,
                                true
                            ).build()
                    )
            }, 5000)
        }
    }

    private fun animateEmoji() {
        // تحميل ملف الأنيميشن وتطبيقه
//        val animator = ObjectAnimator.ofFloat(emojiTextView, "translationY", 0f, 500f)
//        animator.duration = 2000  // مدة الأنيميشن (بالمللي ثانية)
//        animator.start()
        val animator = AnimatorInflater.loadAnimator(requireContext(), R.animator.emoji_shake_animation) as AnimatorSet
        animator.setTarget(imageView)
        animator.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        // إيقاف تشغيل الـ handler عند تدمير النشاط
        handler.removeCallbacksAndMessages(null)
    }

    /*
             val imageView = findViewById<ImageView>(R.id.imageView)
        Glide.with(this).load(R.drawable.your_gif).into(imageView)

        // الانتقال إلى النشاط الرئيسي بعد 3 ثوانٍ
        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 3000) // 3000 مللي ثانية = 3 ثوانٍ
    }
*/
}
