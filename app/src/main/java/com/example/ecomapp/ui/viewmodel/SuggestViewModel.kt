package com.example.ecomapp.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecomapp.data.Product
import com.example.ecomapp.network.JwtManager
import com.example.ecomapp.repositories.FavoriteRepository
import com.example.ecomapp.repositories.ProductRepository
import com.example.ecomapp.repositories.SuggestRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.util.Random

class SuggestViewModel() : ViewModel() {

    private val suggestRepository = SuggestRepository()
    private val productRepository = ProductRepository();
    private val favoriteRepository = FavoriteRepository();

    private val _suggestProducts = MutableLiveData<List<Product>>()
    private val _suggestProductsInDetail = MutableLiveData<List<Product>>()
    private val _suggestProductsForSpecialDay = MutableLiveData<List<Product>>()

    val suggestProducts: MutableLiveData<List<Product>>
        get() = _suggestProducts

    val suggestProductsInDetail: MutableLiveData<List<Product>>
        get() = _suggestProductsInDetail

    val suggestProductsForSpecialDay: MutableLiveData<List<Product>>
        get() = _suggestProductsForSpecialDay

    fun fetchSuggestProducts(userId: Int) {

        val bookTitles = mutableListOf<String>()
        val suggestBooks = mutableSetOf<Product>()

        viewModelScope.launch(Dispatchers.IO) {

            val favoriteResponse = favoriteRepository.fetchFavoriteForSuggest(userId, 1)

            if (favoriteResponse.isSuccessful) {
                val favoriteBooks = favoriteResponse.body()
                favoriteBooks?.let {
                    for (favorite in favoriteBooks) {
//                        Log.i("FAVORITE", favorite.product.title.toString())
                        bookTitles.add(favorite.product.title.toString())
                    }
                }
            } else {
                Log.i(
                    "Error",
                    "Error fetching favorite products: code = ${favoriteResponse.code()} and message = ${favoriteResponse.message()}"
                )
            }

            if (bookTitles.isEmpty()) {
                val popularResponse = productRepository.fetchProductByTypeWithLimit(1, 1)
                if (popularResponse.isSuccessful) {
                    val popularBooks = popularResponse.body()
                    popularBooks?.let {
//                        val random = Random()
//                        val shuffledBooks = it.shuffled(random)
//                        val selectedBooks = shuffledBooks.take(2)
                        for (popular in popularBooks) {
//                        Log.i("POPULAR", popular.title.toString())
                            bookTitles.add(popular.title.toString())
                        }
                    }
                } else {
                    Log.i(
                        "Error",
                        "Error fetching popular products: code = ${popularResponse.code()} and message = ${popularResponse.message()}"
                    )
                }
            }

            val secondResponse = suggestRepository.getSuggestProducts(bookTitles)
            if (secondResponse.isSuccessful) {
                val books = secondResponse.body()
                books?.let {
                    for (book in it) {
                        //Log.i("BOOK", book.id.toString())
                        val thirdResponse = productRepository.fetchProductById(book.id)
                        if (thirdResponse.isSuccessful) {
                            suggestBooks.add(thirdResponse.body()!!)
                        } else {
                            Log.i(
                                "Error",
                                "Error fetching product with id ${book.id}: code = ${thirdResponse.code()} and message = ${thirdResponse.message()}"
                            )
                        }
                    }
                    suggestProducts.postValue(suggestBooks.toList())
                }
            } else {
                Log.i(
                    "Error",
                    "Error fetching suggested products: code = ${secondResponse.code()} and message = ${secondResponse.message()}"
                )
            }
        }
    }

    fun fetchSuggestProductsInDetail(title: String) {

        val suggestBooks = mutableSetOf<Product>()

        viewModelScope.launch(Dispatchers.IO) {

            val response = suggestRepository.getSuggestProducts(listOf(title))
            if (response.isSuccessful) {
                val books = response.body()
                books?.let {
                    for (book in it) {
                        //Log.i("BOOK", book.id.toString())
                        val secondResponse = productRepository.fetchProductById(book.id)
                        if (secondResponse.isSuccessful) {
                            suggestBooks.add(secondResponse.body()!!)
                        } else {
                            Log.i(
                                "Error",
                                "Error fetching product with id ${book.id}: code = ${secondResponse.code()} and message = ${secondResponse.message()}"
                            )
                        }
                    }
                    suggestProductsInDetail.postValue(suggestBooks.toList())
                }
            } else {
                Log.i(
                    "Error",
                    "Error fetching suggested products: code = ${response.code()} and message = ${response.message()}"
                )
            }
        }
    }

    fun fetchSuggestProductsForSpecialDay(date : String, lunar : String) {

        val suggestBooks = mutableSetOf<Product>()

        viewModelScope.launch(Dispatchers.IO) {

            val response = suggestRepository.getSuggestProductsForSpecialDay(date, lunar)
            if (response.isSuccessful) {
                var books = response.body()
                var dayTitle = ""
                if (!books.isNullOrEmpty()) {
                    dayTitle = books[0].title.toString()
                    books = books.subList(1, books.size)
                    books.let {
                        for (book in it) {
                            //Log.i("BOOK", book.id.toString())
                            val secondResponse = productRepository.fetchProductById(book.id)
                            if (secondResponse.isSuccessful) {
                                suggestBooks.add(secondResponse.body()!!)
                            } else {
                                Log.i(
                                    "Error",
                                    "Error fetching product with id ${book.id}: code = ${secondResponse.code()} and message = ${secondResponse.message()}"
                                )
                            }
                        }
                        suggestBooks.add(Product(id = 0, title = dayTitle))
                        suggestProductsForSpecialDay.postValue(suggestBooks.toList())
                    }
                }
            } else {
                Log.i(
                    "Error",
                    "Error fetching suggested products: code = ${response.code()} and message = ${response.message()}"
                )
            }
        }
    }

}