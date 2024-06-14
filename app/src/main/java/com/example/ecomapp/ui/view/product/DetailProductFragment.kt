package com.example.ecomapp.ui.view.product

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.ecomapp.R
import com.example.ecomapp.data.Cart
import com.example.ecomapp.data.Favorite
import com.example.ecomapp.data.Product
import com.example.ecomapp.data.User
import com.example.ecomapp.databinding.FragmentDetailProductBinding
import com.example.ecomapp.network.JwtManager
import com.example.ecomapp.ui.adapter.home.SuggestProductRecyclerViewInDetailAdapter
import com.example.ecomapp.ui.viewmodel.CartViewModel
import com.example.ecomapp.ui.viewmodel.FavoriteViewModel
import com.example.ecomapp.ui.viewmodel.SuggestViewModel

class DetailProductFragment : Fragment() {

    private lateinit var binding: FragmentDetailProductBinding

    private lateinit var favoriteViewModel: FavoriteViewModel

    private lateinit var suggestViewModel : SuggestViewModel
    private lateinit var suggestRecyclerView : RecyclerView
    private lateinit var suggestAdapter : SuggestProductRecyclerViewInDetailAdapter

    private var quantity: Int = 1
    private var price: Int = 0

    private var isFavorite = false

    private val jwtManager = JwtManager

    private val cartViewModel: CartViewModel by lazy {
        ViewModelProvider(this, CartViewModel.CartViewModelFactory(requireActivity().application))
            .get(CartViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentDetailProductBinding.inflate(inflater, container, false)

        val product = arguments?.getSerializable("product") as? Product

        val userId = jwtManager.getUserId(requireContext()).toInt()

        if (product != null) {
            checkIfProductFavorite(userId, product.id)
            product.title?.let { fetchSuggestProducts(it) }
        }

        binding.apply {

            tvBookTitle.text = product?.title
            tvAuthor.text = "Tác giả: " + product?.author
            tvDescription.text = product?.description
            price = product?.price!!
            tvPrice.text = formatPrice(product.price)
            tvQuantity.text = quantity.toString()

            val imageBytes = Base64.decode(product.image, Base64.DEFAULT)
            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            ivBookCover.setImageBitmap(decodedImage)

            ivBackArrow.setOnClickListener {
                findNavController().popBackStack()
            }

            ivHeartIcon.setOnClickListener {

                if (isFavorite) {
                    deleteFavoriteDetailProduct(userId, product.id)

                } else {

                    val tmpUser = User(id = userId)
                    val tmpProduct = Product(id = product.id)
                    val favorite = Favorite(user = tmpUser, product = tmpProduct)
                    saveFavoriteProduct(favorite)

                    isFavorite = true
                    ivHeartIcon.setBackgroundResource(R.drawable.baseline_favorite_24)

                }
            }

            tvReduce.setOnClickListener {
                if (quantity > 1) {
                    quantity -= 1
                }
                tvQuantity.text = quantity.toString()
                tvPrice.text = formatPrice(price * quantity)
            }

            tvAdd.setOnClickListener {
                quantity += 1
                tvQuantity.text = quantity.toString()
                tvPrice.text = formatPrice(price * quantity)
            }

            btnAddCart.setOnClickListener {
                val cart: Cart = Cart(
                    product = Product(id = product.id),
                    quantity = tvQuantity.text.toString().toInt()
                )

                cartViewModel.addCart(cart).observe(viewLifecycleOwner, Observer {
                    if (it is Cart) {
                        Toast.makeText(
                            requireActivity(),
                            "Sản phẩm đã được thêm vào giỏ hàng",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            requireActivity(),
                            "Thêm thất bại!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }

        }

        return binding.root
    }

    private fun fetchSuggestProducts(title : String) {
        suggestViewModel = ViewModelProvider(this)[SuggestViewModel::class.java]
        suggestViewModel.fetchSuggestProductsInDetail(title)
        suggestViewModel.suggestProductsInDetail.observe(viewLifecycleOwner) {
            if (it!=null) {
                addSuggestProducts(it)
            }
        }
    }

    private fun addSuggestProducts(products : List<Product>) {
        binding.apply {
            suggestRecyclerView = rvSuggestBook
            suggestRecyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            suggestAdapter = SuggestProductRecyclerViewInDetailAdapter(products)
            suggestRecyclerView.adapter = suggestAdapter

            suggestRecyclerView.onFlingListener = null
            val pagerSnapHelper = PagerSnapHelper()
            pagerSnapHelper.attachToRecyclerView(suggestRecyclerView)

            suggestAdapter.onItemClick = {
                val bundle = bundleOf("product" to it)
                findNavController().navigate(R.id.action_detailProductFragment_self, bundle)
            }

        }
    }

    private fun deleteFavoriteDetailProduct(userId : Int, productId : Int) {

        favoriteViewModel = ViewModelProvider(this)[FavoriteViewModel::class.java]

        favoriteViewModel.checkIfProductFavorite(userId, productId)

        favoriteViewModel.favorite.observe(viewLifecycleOwner) {
            if (it != null) {
                deleteFavoriteProduct(it.id!!)
                isFavorite = false
                binding.apply {
                    ivHeartIcon.setBackgroundResource(R.drawable.favorite_icon)
                }
            }
        }

    }

    private fun checkIfProductFavorite(userId: Int, productId: Int) {

        favoriteViewModel = ViewModelProvider(this)[FavoriteViewModel::class.java]

        favoriteViewModel.checkIfProductFavorite(userId, productId)

        favoriteViewModel.favorite.observe(viewLifecycleOwner) {
            if (it != null) {
                isFavorite = true
                binding.apply {
                    ivHeartIcon.setBackgroundResource(R.drawable.baseline_favorite_24)
                }
            }
        }
    }

    private fun deleteFavoriteProduct(id: Int) {
        val favoriteViewModel = ViewModelProvider(this)[FavoriteViewModel::class.java]
        favoriteViewModel.deleteFavorite(id)
    }

    private fun saveFavoriteProduct(favorite: Favorite) {
        val favoriteViewModel = ViewModelProvider(this)[FavoriteViewModel::class.java]
        favoriteViewModel.saveFavorite(favorite)
    }

    private fun formatPrice(price: Int): String {
        return price.toString().reversed().chunked(3).joinToString(".").reversed() + " đ"
    }

}