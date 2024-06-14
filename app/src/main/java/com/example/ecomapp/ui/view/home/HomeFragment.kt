package com.example.ecomapp.ui.view.home

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.icu.util.ChineseCalendar
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecomapp.R
import com.example.ecomapp.auth.Resource
import com.example.ecomapp.data.Product
import com.example.ecomapp.databinding.FragmentHomeBinding
import com.example.ecomapp.databinding.ReuseBottombarBinding
import com.example.ecomapp.network.JwtManager
import com.example.ecomapp.ui.adapter.home.AllProductRecyclerViewAdapter
import com.example.ecomapp.ui.adapter.home.PopularProductRecyclerViewAdapter
import com.example.ecomapp.ui.adapter.home.SuggestProductRecyclerViewAdapter
import com.example.ecomapp.ui.viewmodel.ProductsViewModel
import com.example.ecomapp.ui.viewmodel.SuggestViewModel
import com.example.ecomapp.ui.viewmodel.UserViewModel
import com.google.android.material.R.anim.m3_side_sheet_enter_from_left
import com.google.android.material.R.anim.m3_side_sheet_exit_to_left
import com.google.android.material.navigation.NavigationView
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HomeFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener {

    //region Create variables
    private lateinit var binding: FragmentHomeBinding
    private lateinit var barBinding: ReuseBottombarBinding

    private lateinit var popularProductRecyclerView: RecyclerView
    private lateinit var popularAdapter: PopularProductRecyclerViewAdapter

    private lateinit var allProductRecyclerView: RecyclerView
    private lateinit var allAdapter: AllProductRecyclerViewAdapter

    private lateinit var suggestRecyclerView: RecyclerView
    private lateinit var suggestAdapter: SuggestProductRecyclerViewAdapter

    private lateinit var suggestRecyclerView2: RecyclerView
    private lateinit var suggestAdapter2: SuggestProductRecyclerViewAdapter

    private val jwtManager = JwtManager

    private val controller by lazy {
        findNavController()
    }

    private val viewModel: UserViewModel by lazy {
        ViewModelProvider(
            this,
            UserViewModel.UserViewModelFactory(requireActivity().application)
        )[UserViewModel::class.java]
    }
    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("PrivateResource")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        barBinding = ReuseBottombarBinding.inflate(inflater, container, false)

        binding.llSpecialDay.visibility = GONE

        getUserInfo()
        fetchSuggestProductsForSpecialDay()
        fetchPopularProducts()
        fetchSuggestProducts()
        fetchAllProducts()

        binding.apply {

            root.addView(barBinding.root)

            //region navigate to other pages

            ivBag.setOnClickListener {
                it.findNavController().navigate(R.id.action_homeFragment_to_cartFragment)
            }

            tvSeePopular.setOnClickListener {
                val bundle = bundleOf("type" to "1")
                it.findNavController()
                    .navigate(R.id.action_homeFragment_to_displayProductFragment, bundle)
            }

            tvSeeAllSuggest.setOnClickListener {
                it.findNavController()
                    .navigate(R.id.action_homeFragment_to_suggestProductFragment)
            }

            tvSeeAllBook.setOnClickListener {
                val bundle = bundleOf("type" to "0")
                it.findNavController()
                    .navigate(R.id.action_homeFragment_to_displayProductFragment, bundle)
            }

            ivBag.setOnClickListener {
                it.findNavController().navigate(R.id.action_homeFragment_to_cartFragment)
            }

            tvSearch.setOnClickListener {
                it.findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
            }

            barBinding.apply {

                llWishlist.setOnClickListener {
                    it.findNavController().navigate(R.id.action_homeFragment_to_favoriteFragment)
                }

                llNoti.setOnClickListener {
                    it.findNavController()
                        .navigate(R.id.action_homeFragment_to_notificationFragment)
                }
                llUser.setOnClickListener {
                    it.findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
                }
            }

            //endregion

            //region open menu

            val animShow = AnimationUtils.loadAnimation(activity, m3_side_sheet_enter_from_left)
            ivMenu.setOnClickListener {
                menuWrapper.visibility = View.VISIBLE
                menuWrapper.startAnimation(animShow)
            }

            val animHide = AnimationUtils.loadAnimation(activity, m3_side_sheet_exit_to_left)
            framelayout.setOnClickListener {
                menuWrapper.startAnimation(animHide)
                menuWrapper.visibility = View.GONE
            }

            //endregion

        }

        binding.navView.setNavigationItemSelectedListener(this)

        return binding.root
    }

    private fun getUserInfo() {
        viewModel.user.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    val imageBytes = Base64.decode(it.value.avatarImage, Base64.DEFAULT)
                    val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    binding.userAvt.setImageBitmap(decodedImage)
                    binding.userName.text = it.value.name
                }

                is Resource.Failure -> {
                    Toast.makeText(
                        requireContext(),
                        "Không tìm thấy thông tin người dùng!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
        viewModel.getUserInfo()
    }

    //region Fetch Products
    private fun fetchAllProducts() {

        val productsViewModel = ViewModelProvider(this)[ProductsViewModel::class.java]

        productsViewModel.fetchProductsByTypeWithLimit(0, 5)

        productsViewModel.allProducts.observe(viewLifecycleOwner) {
            if (it != null) {
                addAllProducts(it)
            }
        }
    }

    private fun fetchSuggestProducts() {

        val productsViewModel = ViewModelProvider(this)[SuggestViewModel::class.java]

        productsViewModel.fetchSuggestProducts(userId = jwtManager.getUserId(requireContext()).toInt())

        productsViewModel.suggestProducts.observe(viewLifecycleOwner) {
            if (it != null) {
                addSuggestProducts(it)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchSuggestProductsForSpecialDay() {

        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM")
        val formattedDate = currentDate.format(formatter)

        val suggestViewModel = ViewModelProvider(this)[SuggestViewModel::class.java]
        suggestViewModel.fetchSuggestProductsForSpecialDay(formattedDate, formattedDate)

        suggestViewModel.suggestProductsForSpecialDay.observe(viewLifecycleOwner) {
            if (it != null && it.isNotEmpty()) {
                val suggestProductsForSpecialDay = it.subList(0, it.size - 1)
                val dayName = it[it.size-1].title
                if (dayName != null) {
                    addSuggestProductsForSpecialDay(suggestProductsForSpecialDay, dayName)
                }
            }
        }

    }

    private fun fetchPopularProducts() {

        val productsViewModel = ViewModelProvider(this)[ProductsViewModel::class.java]

        productsViewModel.fetchProductsByTypeWithLimit(1, 5)

        productsViewModel.popularProducts.observe(viewLifecycleOwner) {
            if (it != null) {
                addPopularProducts(it)
            }
        }

    }
    //endregion

    //region Add data to adapter
    private fun addPopularProducts(products: List<Product>) {

        binding.apply {

            popularProductRecyclerView = rvPopularBooks
            popularProductRecyclerView.layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            popularAdapter = PopularProductRecyclerViewAdapter(products)
            popularProductRecyclerView.adapter = popularAdapter

            popularAdapter.onItemClick = {
                val bundle = bundleOf("product" to it)
                findNavController().navigate(
                    R.id.action_homeFragment_to_detailProductFragment,
                    bundle
                )
            }

        }
    }

    private fun addAllProducts(products: List<Product>) {
        binding.apply {

            allProductRecyclerView = rvAllBook
            allProductRecyclerView.layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            allAdapter = AllProductRecyclerViewAdapter(products)
            allProductRecyclerView.adapter = allAdapter

            allAdapter.onItemClick = {
                val bundle = bundleOf("product" to it)
                findNavController().navigate(
                    R.id.action_homeFragment_to_detailProductFragment,
                    bundle
                )
            }

        }
    }

    private fun addSuggestProducts(products: List<Product>) {

        binding.apply {

            suggestRecyclerView = rvSuggestBook
            suggestRecyclerView.layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            suggestAdapter = SuggestProductRecyclerViewAdapter(products)
            suggestRecyclerView.adapter = suggestAdapter

//            suggestRecyclerView.onFlingListener = null
//            val pagerSnapHelper = PagerSnapHelper()
//            pagerSnapHelper.attachToRecyclerView(suggestRecyclerView)

            suggestAdapter.onItemClick = {
                val bundle = bundleOf("product" to it)
                findNavController().navigate(
                    R.id.action_homeFragment_to_detailProductFragment,
                    bundle
                )
            }

        }
    }

    private fun addSuggestProductsForSpecialDay(products: List<Product>, dayName : String) {

        binding.apply {

            llSpecialDay.visibility = View.VISIBLE

            tvSpecialDay.text = dayName

            suggestRecyclerView2 = rvSpecialDays
            suggestRecyclerView2.layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            suggestAdapter2 = SuggestProductRecyclerViewAdapter(products)
            suggestRecyclerView2.adapter = suggestAdapter2

            suggestAdapter2.onItemClick = {
                val bundle = bundleOf("product" to it)
                findNavController().navigate(
                    R.id.action_homeFragment_to_detailProductFragment,
                    bundle
                )
            }

        }
    }

    //endregion

    //region Navigate pages in menu
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_user -> controller.navigate(R.id.action_homeFragment_to_profileFragment)
            R.id.nav_cart -> controller.navigate(R.id.action_homeFragment_to_cartFragment)
            R.id.nav_favorite -> controller.navigate(R.id.action_homeFragment_to_favoriteFragment)
            R.id.nav_order -> controller.navigate(R.id.action_homeFragment_to_orderHistoryFragment)
            R.id.nav_notification -> controller.navigate(R.id.action_homeFragment_to_notificationFragment)
            R.id.nav_dashboard -> controller.navigate(R.id.action_homeFragment_to_dashboardFragment)
            R.id.nav_setting -> controller.navigate(R.id.action_homeFragment_to_settingFragment)
            R.id.nav_logout -> signOut()

        }
        return true
    }
    //endregion

    private fun signOut() {
        JwtManager.removeJwtToken(requireContext())
        JwtManager.CURRENT_JWT = JwtManager.getJwtToken(requireActivity().application)
        controller.navigate(R.id.action_homeFragment_to_loginFragment)
    }

}