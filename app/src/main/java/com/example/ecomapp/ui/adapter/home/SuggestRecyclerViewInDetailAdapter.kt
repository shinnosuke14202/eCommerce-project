package com.example.ecomapp.ui.adapter.home

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ecomapp.data.Product
import com.example.ecomapp.databinding.ViewholderSuggestItemBinding


class SuggestProductRecyclerViewInDetailAdapter (

    private val products: List<Product>

) : RecyclerView.Adapter<SuggestViewHolderInDetail>() {

    lateinit var onItemClick: (Product) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestViewHolderInDetail {
        val binding = ViewholderSuggestItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SuggestViewHolderInDetail(binding)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(holder: SuggestViewHolderInDetail, position: Int) {
        val product = products[position]

        holder.bind(product)

        holder.itemView.setOnClickListener {
            onItemClick.invoke(product)
        }

    }
}

class SuggestViewHolderInDetail(private val binding: ViewholderSuggestItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(product: Product) {

        binding.apply {

            tvTitle.text = product.title
            tvAuthor.text = product.author
            tvPrice.text = formatPrice(product.price)

            val imageBytes = Base64.decode(product.image, Base64.DEFAULT)
            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            ivBookCover.setImageBitmap(decodedImage)

        }
    }

    private fun formatPrice(price: Int): String {
        return price.toString().reversed().chunked(3).joinToString(".").reversed() + " đ"
    }

}