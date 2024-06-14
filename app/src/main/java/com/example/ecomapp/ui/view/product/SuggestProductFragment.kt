package com.example.ecomapp.ui.view.product

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.ecomapp.R
import com.example.ecomapp.data.Category
import com.example.ecomapp.data.Product
import com.example.ecomapp.databinding.FragmentDisplayProductBinding
import com.example.ecomapp.databinding.FragmentDisplaySuggestBinding
import com.example.ecomapp.databinding.ReuseBottombarBinding
import com.example.ecomapp.network.JwtManager
import com.example.ecomapp.ui.adapter.display.CategoryRecyclerViewAdapter
import com.example.ecomapp.ui.adapter.display.DisplayProductRecyclerViewAdapter
import com.example.ecomapp.ui.adapter.home.SuggestProductRecyclerViewAdapter
import com.example.ecomapp.ui.viewmodel.CategoryViewModel
import com.example.ecomapp.ui.viewmodel.ProductsViewModel
import com.example.ecomapp.ui.viewmodel.SuggestViewModel
import kotlin.properties.Delegates

class SuggestProductFragment : Fragment() {

    //region Create variables
    private lateinit var binding: FragmentDisplaySuggestBinding
    private lateinit var barBinding: ReuseBottombarBinding

    private lateinit var suggestRecyclerView: RecyclerView
    private lateinit var suggestAdapter: SuggestProductRecyclerViewAdapter

    private val jwtManager = JwtManager

    private lateinit var products: List<Product>
    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentDisplaySuggestBinding.inflate(inflater, container, false)

        barBinding = ReuseBottombarBinding.inflate(inflater, container, false)

        fetchSuggestProducts()

        binding.apply {

            root.addView(barBinding.root)

            //region Click Listener
            ivBackArrow.setOnClickListener {
                findNavController().popBackStack()
            }

            btnSortPriceLTH.setOnClickListener {
                val sortedList = products.sortedBy { it.price }
                addSuggestProducts(sortedList)
            }

            btnSortPriceHTL.setOnClickListener {
                val sortedList = products.sortedByDescending { it.price }
                addSuggestProducts(sortedList)
            }

            ivSearch.setOnClickListener {
                it.findNavController()
                    .navigate(R.id.action_suggestProductFragment_to_searchFragment)
            }
            //endregion

            //region Navigate to other pages
            barBinding.apply {

                llWishlist.setOnClickListener {
                    it.findNavController()
                        .navigate(R.id.action_suggestProductFragment_to_favoriteFragment)
                }

                llNoti.setOnClickListener {
                    it.findNavController()
                        .navigate(R.id.action_suggestProductFragment_to_notificationFragment)
                }

                llHome.setOnClickListener {
                    it.findNavController()
                        .navigate(R.id.action_suggestProductFragment_to_homeFragment)
                }

            }
            //endregion

        }

        return binding.root

    }

    private fun fetchSuggestProducts() {
        val suggestViewModel = ViewModelProvider(this)[SuggestViewModel::class.java]
        suggestViewModel.fetchSuggestProducts(userId = jwtManager.getUserId(requireContext()).toInt())
        suggestViewModel.suggestProducts.observe(viewLifecycleOwner) {
            if (it != null) {
                addSuggestProducts(it)
                products = it
            }
        }
    }

    private fun addSuggestProducts(products: List<Product>) {

        binding.apply {

            suggestRecyclerView = rvSuggestBook
            suggestRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
            suggestAdapter = SuggestProductRecyclerViewAdapter(products)
            suggestRecyclerView.adapter = suggestAdapter

            suggestRecyclerView.onFlingListener = null

            val pagerSnapHelper = PagerSnapHelper()
            pagerSnapHelper.attachToRecyclerView(suggestRecyclerView)

            suggestAdapter.onItemClick = {
                val bundle = bundleOf("product" to it)
                findNavController().navigate(
                    R.id.action_suggestProductFragment_to_detailProductFragment,
                    bundle
                )
            }

        }
    }

}