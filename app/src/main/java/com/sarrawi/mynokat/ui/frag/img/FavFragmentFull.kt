package com.sarrawi.mynokat.ui.frag.img

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.sarrawi.mynokat.R
import com.sarrawi.mynokat.api.ApiService
import com.sarrawi.mynokat.databinding.FragmentFavBinding
import com.sarrawi.mynokat.databinding.FragmentImgFullBinding
import com.sarrawi.mynokat.db.LocaleSource
import com.sarrawi.mynokat.db.PostDatabase
import com.sarrawi.mynokat.model.FavImgModel
import com.sarrawi.mynokat.model.ImgsNokatModel
import com.sarrawi.mynokat.paging.PagingAdapterImgFav
import com.sarrawi.mynokat.paging.PagingAdapterImgFavFull
import com.sarrawi.mynokat.repository.NokatRepo
import com.sarrawi.mynokat.viewModel.MyViewModelFactory
import com.sarrawi.mynokat.viewModel.NokatViewModel

class FavFragmentFull : Fragment() {
    private var _binding: FragmentImgFullBinding? = null
    private val binding get() = _binding!!

    private val retrofitService = ApiService.provideRetrofitInstance()
    private val mainRepository by lazy {
        NokatRepo(
            retrofitService, LocaleSource(requireContext()),
            PostDatabase.getInstance(requireContext())
        )
    }
    private val nokatViewModel: NokatViewModel by viewModels {
        MyViewModelFactory(
            mainRepository,
            requireContext(),
            PostDatabase.getInstance(requireContext())
        )
    }
    private val pagingAdapterImgFavFull by lazy { PagingAdapterImgFavFull(requireActivity(), this) }
    private lateinit var favimgModel: FavImgModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        favimgModel= FavFragmentFullArgs.fromBundle(requireArguments()).full
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentImgFullBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setuprc()
        adapterOnClick()
    }

    fun setuprc() {
        if (isAdded) {
            binding.rcImgFull.layoutManager =
                LinearLayoutManager(requireContext())

            // تحميل البيانات باستخدام Paging
            nokatViewModel.favImg2.observe(viewLifecycleOwner) { pagingData ->
                // قم بتقديم البيانات إلى ال Adapter
                pagingAdapterImgFavFull.submitData(viewLifecycleOwner.lifecycle, pagingData)
                scrollToSelectedImage()
            }

            // تحديث RecyclerView عندما تأتي بيانات جديدة
            pagingAdapterImgFavFull.addLoadStateListener { loadState ->
                if (loadState.refresh is LoadState.NotLoading) {
                    nokatViewModel.getFav().observe(viewLifecycleOwner) { favoriteImages ->
                        // قم بتحديث حالة كل صورة
//                        favPagingAdapterImg.refresh(favoriteImages)
                        pagingAdapterImgFavFull.notifyDataSetChanged()
                        // إذا لم يتم تعيين Adapter بعد، قم بتعيينه
                        if (binding.rcImgFull.adapter == null) {
                            binding.rcImgFull.adapter = pagingAdapterImgFavFull
                        }
                    }
                }
            }
        }
    }

    private fun scrollToSelectedImage() {
        binding.rcImgFull.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
//                override fun onPreDraw(): Boolean {
//                    if (pagingAdapterImg.itemCount > 0) {
//                        val snapshot = pagingAdapterImg.snapshot()
//                        val position = snapshot.indexOfFirst { item ->
//                            (item is ItemModel.ImgsItem) && (item.imgsNokatModel.id == imgModel?.id)
//                        }
//                        if (position != -1) {
//                            binding.rcImgFull.scrollToPosition(position)
//                            Toast.makeText(requireContext(), "Item found", Toast.LENGTH_SHORT).show()
//                        } else {
//                            Toast.makeText(requireContext(), "Item not found", Toast.LENGTH_SHORT).show()
//                        }
//                        binding.rcImgFull.viewTreeObserver.removeOnPreDrawListener(this)
//                        return true
//                    }
//                    return false
//                }




                override fun onPreDraw(): Boolean {
                    if (pagingAdapterImgFavFull.itemCount > 0) {
                        val snapshot = pagingAdapterImgFavFull.snapshot()
                        val position = snapshot.indexOfFirst { it?.id == favimgModel.id }
                        if (position != -1) {
                            binding.rcImgFull.scrollToPosition(position)
//                            Toast.makeText(requireContext(), "Item found", Toast.LENGTH_SHORT).show()
                        } else {
//                            Toast.makeText(requireContext(), "Item not found", Toast.LENGTH_SHORT).show()
                        }
                        binding.rcImgFull.viewTreeObserver.removeOnPreDrawListener(this) ///
                        return true
                    }
                    return false
                }
            }
        )
    }

    private fun adapterOnClick() {

        pagingAdapterImgFavFull.onbtnclick = { id, item, position ->
            item.is_fav = false
            nokatViewModel.update_favs_img(id, false)
            nokatViewModel.delete_favs_img(
                FavImgModel(
                    item.id!!,
                    item.new_img,
                    item.pic,
                    item.image_url,
                    item.createdAt
                )
            )

            val snackbar = Snackbar.make(requireView(), "تم الحذف", Snackbar.LENGTH_SHORT)
            snackbar.show()
        }

    }

}