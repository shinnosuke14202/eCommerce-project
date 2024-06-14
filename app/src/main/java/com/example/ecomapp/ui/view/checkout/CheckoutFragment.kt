package com.example.ecomapp.ui.view.checkout

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.ecomapp.R
import com.example.ecomapp.data.ItemOrder
import com.example.ecomapp.data.Order
import com.example.ecomapp.data.Payment
import com.example.ecomapp.data.Product
import com.example.ecomapp.data.Shipment
import com.example.ecomapp.databinding.FragmentCheckoutBinding
import com.example.ecomapp.ui.viewmodel.CartViewModel
import com.example.ecomapp.ui.viewmodel.OrderViewModel
import com.example.ecomapp.ui.viewmodel.ShareCheckoutViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class CheckoutFragment : Fragment(), OnMapReadyCallback {
    private lateinit var binding: FragmentCheckoutBinding
    private lateinit var googleMap: GoogleMap
    private lateinit var marker: Marker
    private lateinit var geocoder: Geocoder
    private var isFirstTimeCreated = true
    private val controller by lazy {
        findNavController()
    }
    private val orderViewModel: OrderViewModel by lazy {
        ViewModelProvider(
            this,
            OrderViewModel.OrderViewModelFactory(requireActivity().application)
        )[OrderViewModel::class.java]
    }
    private val cartViewModel: CartViewModel by lazy {
        ViewModelProvider(this, CartViewModel.CartViewModelFactory(requireActivity().application))
            .get(CartViewModel::class.java)
    }
    private val shareCheckoutViewModel by lazy {
        ViewModelProvider(
            requireActivity(),
            ShareCheckoutViewModel.ShareCheckoutViewModelFactory(requireActivity().application)
        )[ShareCheckoutViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCheckoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isFirstTimeCreated) {
            shareCheckoutViewModel.fetchUserInfoDefault()
            isFirstTimeCreated = false
        }
        binding.btnBack.setOnClickListener {
            controller.popBackStack()
        }

        binding.refeshUserInfo.setOnClickListener {
            shareCheckoutViewModel.fetchUserInfoDefault()
        }

        binding.btnPayment.setOnClickListener {
            createOrder()
        }

        binding.btnEditName.setOnClickListener {
            openDialogEditName()
        }

        binding.btnEditPhone.setOnClickListener {
            openDialogEditPhone()
        }

        binding.layoutShipmentMethod.setOnClickListener {
            controller.navigate(R.id.action_checkoutFragment_to_shipmentMethodFragment)
        }

        binding.layoutPaymentMethod.setOnClickListener {
            controller.navigate(R.id.action_checkoutFragment_to_paymentMethodFragment)
        }

        shareCheckoutViewModel.name.observe(viewLifecycleOwner, Observer {
            binding.txtName.text = it
        })

        shareCheckoutViewModel.phone.observe(viewLifecycleOwner, Observer {
            binding.txtPhone.text = it
        })

        shareCheckoutViewModel.shipmentMethodSelected.observe(viewLifecycleOwner, Observer {
            binding.shipmentMethod.text = it.name
        })

        shareCheckoutViewModel.paymentMethodSelected.observe(viewLifecycleOwner, Observer {
            binding.paymentMethod.text = it.name
        })

        cartViewModel.fetchAllCart()
        cartViewModel.listCart.observe(viewLifecycleOwner, Observer {
            var totalPrice = 0
            for (cart in it) {
                totalPrice += cart.product.price * cart.quantity
            }
            binding.txtSubtotal.text = totalPrice.toString() + "₫"
            shareCheckoutViewModel.shipmentMethodSelected.observe(
                viewLifecycleOwner,
                Observer { shipMethod ->
                    binding.txtShiping.text = (it.size * shipMethod.price).toString() + "₫"
                    binding.txtTotal.text = (
                            totalPrice + it.size * shipMethod.price
                            ).toString() + "₫"
                })

        })

        geocoder = Geocoder(requireActivity())
        val mapFragment = childFragmentManager.findFragmentById(R.id.btnMap) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    private fun createOrder() {
        if (shareCheckoutViewModel.address.value.equals("Chưa có địa chỉ")) {
            Toast.makeText(requireActivity(), "Vui lòng cung cấp địa chỉ", Toast.LENGTH_SHORT)
                .show()
            return
        } else if (shareCheckoutViewModel.phone.value.equals("")) {
            Toast.makeText(requireActivity(), "Vui lòng cung cấp số điện thoại", Toast.LENGTH_SHORT)
                .show()
            return
        }
        val itemOrders: MutableList<ItemOrder> = mutableListOf()
        for (cart in cartViewModel.listCart.value!!) {
            itemOrders.add(
                ItemOrder(
                    product = Product(id = cart.product.id),
                    quantity = cart.quantity
                )
            )
        }
        val newOrder: Order = Order(
            status = "Đang giao hàng",
            address = shareCheckoutViewModel.address.value!!,
            phoneNumber = shareCheckoutViewModel.phone.value!!,
            itemOrders = itemOrders,
            name = shareCheckoutViewModel.name.value!!,
            user = null,
            shipment = Shipment(
                code = "PTIT Express - PTITEX053213030",
                shipStatus = "Chờ lấy hàng",
                shipmentMethod = shareCheckoutViewModel.shipmentMethodSelected.value!!
            ),
            payment = Payment(
                totalPrice = binding.txtTotal.text.toString().dropLast(1).toInt(),
                payStatus = if (
                    shareCheckoutViewModel.paymentMethodSelected.value!!.name == "Thanh toán trực tiếp"
                )
                    "Chờ thanh toán" else "Đã thanh toán",
                paymentMethod = shareCheckoutViewModel.paymentMethodSelected.value!!
            )
        )
        orderViewModel.createOrder(newOrder).observe(viewLifecycleOwner, Observer {
            if (it is Order) {
                openDialogNotification()
                cartViewModel.deleteCartByUser()
            } else {
                Toast.makeText(requireActivity(), it.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onMapReady(map: GoogleMap) {
        map.let {
            googleMap = it
            shareCheckoutViewModel.address.observe(viewLifecycleOwner, Observer { addr ->
                binding.txtAddress.text = addr
                var address: Address? = null
                if (addr != "Chưa có địa chỉ") {
                    address = geocoder.getFromLocationName(addr, 1)!!.get(0)
                } else {
                    address = geocoder.getFromLocationName("Hà Nội, Việt Nam", 1)!!.get(0)
                }
                val LatLng = LatLng(address.latitude, address.longitude)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng, 15f))
                googleMap.clear()
                marker = googleMap.addMarker(MarkerOptions().position(LatLng))!!
            })

            googleMap.setOnMapClickListener { latLng ->
                controller.navigate(R.id.action_checkoutFragment_to_GGMapFragment)
            }
        }
    }

    fun openDialogNotification() {
        val dialog = Dialog(requireActivity())
        dialog.setContentView(R.layout.dialog_noti_checkout)
        dialog.setCanceledOnTouchOutside(false)

        val window = dialog.window
        if (window == null) {
            return
        }
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))// Để loại bỏ viền màu trắng xung quanh dialog

        var btnBack: Button = dialog.findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            controller.navigate(R.id.action_checkoutFragment_to_orderHistoryFragment)
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun openDialogEditName() {
        val dialog = Dialog(requireActivity())
        dialog.setContentView(R.layout.dialog_edit_name)

        val window = dialog.window
        if (window == null) {
            return
        }
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))// Để loại bỏ viền màu trắng xung quanh dialog

        val btnSave: Button = dialog.findViewById(R.id.btnSave)
        val edtName: EditText = dialog.findViewById(R.id.edtName)
        edtName.setText(binding.txtName.text.toString())
        btnSave.setOnClickListener {
            val name = edtName.text.toString()
            if (name != "") {
                shareCheckoutViewModel.updateName(name)
                dialog.dismiss()
            } else {
                Toast.makeText(requireActivity(), "Vui lòng nhập họ tên", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        dialog.show()
    }

    private fun openDialogEditPhone() {
        val dialog = Dialog(requireActivity())
        dialog.setContentView(R.layout.dialog_edit_phonenumber)

        val window = dialog.window
        if (window == null) {
            return
        }
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))// Để loại bỏ viền màu trắng xung quanh dialog

        val btnSave: Button = dialog.findViewById(R.id.btnSave)
        val edtPhone: EditText = dialog.findViewById(R.id.edtPhone)
        edtPhone.setText(binding.txtPhone.text.toString())
        btnSave.setOnClickListener {
            val phone = edtPhone.text.toString()
            if (phone.length == 10) {
                shareCheckoutViewModel.updatePhone(phone)
                dialog.dismiss()
            } else {
                Toast.makeText(requireActivity(), "Please enter all 10 numbers", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        dialog.show()
    }
}