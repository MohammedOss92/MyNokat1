package com.sarrawi.mynokat.ui.frag.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.sarrawi.mynokat.api.ApiService
import com.sarrawi.mynokat.databinding.FragmentImgFullBinding
import com.sarrawi.mynokat.model.ImgsNokatModel
import com.sarrawi.mynokat.paging.PagingAdapterFullImg
import com.sarrawi.mynokat.repository.NokatRepo
import com.sarrawi.mynokat.viewModel.MyViewModelFactory
import com.sarrawi.mynokat.viewModel.NokatViewModel

class ImgFullFragment : Fragment() {
    private lateinit var _binding: FragmentImgFullBinding
    private val binding get() = _binding

    private val retrofitService = ApiService.provideRetrofitInstance()
    private val mainRepository by lazy { NokatRepo(retrofitService) }
    private val nokatViewModel: NokatViewModel by viewModels {
        MyViewModelFactory(mainRepository, requireContext())
    }

    private val pagingAdapterImgFull by lazy { PagingAdapterFullImg(requireActivity(), this) }
    private lateinit var imgModel: ImgsNokatModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imgModel = ImgFullFragmentArgs.fromBundle(requireArguments()).fullimg
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentImgFullBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.rcImgFull.layoutManager = LinearLayoutManager(requireContext()).apply {
//            reverseLayout = true // // نفعل هذا الخيار
//            stackFromEnd = true // مع هذا ليتم تحريك موقع العناصر
        }
        binding.rcImgFull.adapter = pagingAdapterImgFull
        nokatViewModel.getAllImage().observe(viewLifecycleOwner) { pagingData ->
            pagingAdapterImgFull.submitData(viewLifecycleOwner.lifecycle, pagingData)
            scrollToSelectedImage()
        }
    }

    /// لازم ننتظر تتحمل البيانات بشكل كامل ويتم اظهارها على الشاشه

    private fun scrollToSelectedImage() {
        // اضفت viewTreeObserver  للتحقق من أن البيانات قد تم تحميلها بالكامل والعرض
        binding.rcImgFull.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {


                override fun onPreDraw(): Boolean {
                    if (pagingAdapterImgFull.itemCount > 0) {
                        val snapshot = pagingAdapterImgFull.snapshot()
                        val position = snapshot.indexOfFirst { it?.id == imgModel.id }
                        if (position != -1) {
                            binding.rcImgFull.scrollToPosition(position)
                            Toast.makeText(requireContext(), "Item found", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), "Item not found", Toast.LENGTH_SHORT).show()
                        }
                        binding.rcImgFull.viewTreeObserver.removeOnPreDrawListener(this) ///
                        return true
                    }
                    return false
                }
            }
        )
    }
}