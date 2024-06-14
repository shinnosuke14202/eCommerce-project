package com.example.ecomapp.ui.adapter.display

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ecomapp.R
import com.example.ecomapp.data.Category
import com.example.ecomapp.databinding.ViewholderCategoryItemBinding

class CategoryRecyclerViewAdapter(

    private val categories: List<Category>,
    private val currentCategoryIds: ArrayList<Int>

) : RecyclerView.Adapter<CategoryRecyclerViewAdapter.CategoryViewHolder>() {

    var listCategoryId = currentCategoryIds

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ViewholderCategoryItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {

        val category = categories[position]

        holder.bind(category)

        val isSelected = listCategoryId.contains(category.id)

        holder.itemView.setOnClickListener {
            if (isSelected) {
                listCategoryId.remove(category.id)
            } else {
                listCategoryId.add(category.id)
            }
            notifyDataSetChanged()
        }
    }

    inner class CategoryViewHolder(val binding: ViewholderCategoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(category: Category) {
            binding.apply {
                tvCategory.text = category.categoryName
                tvCategory.setBackgroundResource(if (currentCategoryIds.contains(category.id)) R.drawable.blue_circle_background else R.drawable.white_circle_background)
                tvCategory.setTextColor(if (currentCategoryIds.contains(category.id)) Color.WHITE else Color.BLACK)
            }
        }
    }

}