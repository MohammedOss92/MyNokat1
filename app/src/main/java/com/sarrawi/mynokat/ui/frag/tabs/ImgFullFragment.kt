package com.sarrawi.mynokat.ui.frag.tabs

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.sarrawi.mynokat.R
import com.sarrawi.mynokat.api.ApiService
import com.sarrawi.mynokat.databinding.FragmentImgFullBinding

import com.sarrawi.mynokat.paging.PagingAdapterFullImg
import com.sarrawi.mynokat.paging.PagingAdapterImg
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

    private val PagingAdapterImgfull by lazy { PagingAdapterFullImg(requireActivity(),this) }

    private var ID = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
// بعد استلام المعرف
        ID = ImgFullFragmentArgs.fromBundle(requireArguments()).id

// اعثر على موضع العنصر ضمن RecyclerView وقم بالتمرير إليه
        val position = PagingAdapterImgfull.snapshot()?.indexOfFirst { it?.id == ID }
        if (position != null && position != -1) {
            binding.rcImgFull.layoutManager?.scrollToPosition(position)
        } else {
            // في حالة عدم العثور على المعرف في القائمة
            Toast.makeText(requireContext(), "Item with ID $ID not found", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding=FragmentImgFullBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ID = ImgFullFragmentArgs.fromBundle(requireArguments()).id

        setup()
    }

    private fun setup() {
        if (isAdded) {
            binding.rcImgFull.layoutManager = LinearLayoutManager(requireContext())
            binding.rcImgFull.adapter = PagingAdapterImgfull
            nokatViewModel.getAllImage().observe(viewLifecycleOwner) { pagingData ->
                PagingAdapterImgfull.submitData(viewLifecycleOwner.lifecycle, pagingData)
            }
            val snapshot = PagingAdapterImgfull.snapshot()
            if (!snapshot.isEmpty()) {
                // القائمة ليست فارغة، يمكنك استخدامها بأمان
                val position = snapshot.indexOfFirst { it?.id == ID }
                if (position != -1) {
                    // تم العثور على العنصر، يمكنك التمرير إليه
                    binding.rcImgFull.layoutManager?.scrollToPosition(position)
                } else {
                    // لم يتم العثور على العنصر، يمكنك التعامل مع هذه الحالة هنا
                    Toast.makeText(requireContext(), "Item with ID $ID not found", Toast.LENGTH_SHORT).show()
                    Log.d("aa$id", id.toString())
                }
            } else {
                // القائمة فارغة
                Toast.makeText(requireContext(), "List is empty", Toast.LENGTH_SHORT).show()
            }

            Toast.makeText(requireContext(), "Received ID: $id", Toast.LENGTH_SHORT).show()
            Log.d("aa$id","aa$id.toString()")
        }
    }
}