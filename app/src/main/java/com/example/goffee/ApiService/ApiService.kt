package com.example.goffee.ApiService

import com.example.goffee.Models.AkunModel
import com.example.goffee.Models.CartResponse
import com.example.goffee.Models.LoginRequest
import com.example.goffee.Models.LoginResponse
import com.example.goffee.Models.MenuModel
import com.example.goffee.Models.MenuOrder
import com.example.goffee.Models.Order
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @POST("/api/user/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @GET("/api/user/products/recommended")
    fun recommendedProducts(@Header("Authorization") token: String): Call<recomendedResponse>
    data class recomendedResponse(val status: Int, val values: List<MenuModel>)

    @GET("/api/user/products/recommended/ice")
    fun recommendedIceProducts(@Header("Authorization") token: String): Call<recomendedIceResponse>
    data class recomendedIceResponse(val status: Int, val values: List<MenuModel>)

    @GET("/api/user/products/recommended/hot")
    fun recommendedHotProducts(@Header("Authorization") token: String): Call<recomendedHotResponse>
    data class recomendedHotResponse(val status: Int, val values: List<MenuModel>)

    @GET("/api/user/profile")
    fun profile(@Header("Authorization") token: String): Call<profileResponse>
    data class profileResponse(val status: Int, val values: List<AkunModel>)


    @GET("/api/user/products/ice")
    fun menuIce(@Header("Authorization") token: String): Call<menuIceResponse>
    data class menuIceResponse(val status: Int, val values: List<MenuModel>)

    @GET("/api/user/products/hot")
    fun menuHot(@Header("Authorization") token: String): Call<menuHotResponse>
    data class menuHotResponse(val status: Int, val values: List<MenuModel>)

    @GET("/api/user/product/{id_menu}")
    fun getProductDetails(
        @Header("Authorization") token: String,
        @Path("id_menu") idMenu: Int
    ): Call<productDetail>

    data class productDetail(val status: Int, val values: List<MenuModel>)

    @POST("/api/user/cart/item/add-to-cart")
    fun addToCart(
        @Header("Authorization") token: String,
        @Body request: addToCartRequest
    ): Call<addToCartResponse>

    data class addToCartRequest(val id_menu: Int)
    data class addToCartResponse(val status: Int, val message: String)

    @GET("/api/user/cart")
    fun getCartItems(
        @Header("Authorization") token: String
    ): Call<CartResponse>


    @PUT("/api/user/cart/item/setamount")
    fun setCartItemAmount(
        @Header("Authorization") token: String,
        @Body request: Map<String, Int>
    ): Call<setCartItemAmountResponse>

    data class setCartItemAmountResponse(
        val status: Int,
        val message: String
    )

    @PUT("/api/user/confirm/{id_history}")
    fun confPay(
        @Header("Authorization") token: String,
        @Path("id_history") id_history: Int
    ): Call<confPayResponse>

    data class confPayResponse(
        val status: Int,
        val message: String
    )

    @PUT("/api/user/cancel/{id_history}")
    fun cancelOrder(
        @Header("Authorization") token: String,
        @Path("id_history") id_history: Int
    ): Call<cancelOrderResponse>

    data class cancelOrderResponse(
        val status: Int,
        val message: String
    )

    @DELETE("/api/user/cart/item/delete/{id_cart_item}")
    fun deleteItemCart(
        @Path("id_cart_item") idCartItem: Int,
        @Header("Authorization") token: String,
    ): Call<deleteItemCartResponse>

    data class deleteItemCartResponse(
        val status: Int,
        val message: String
    )

    @POST("/api/user/checkout")
    fun checkout(
        @Header("Authorization") token: String,
        @Body request: checkoutRequest
    ): Call<checkoutResponse>

    data class checkoutRequest(val address: String, val user_notes: String)
    data class checkoutResponse(
        val status: Int,
        val message: String,
        val bank_name: String,
        val bank_account: String,
        val total: Int,
        val iHistory: Int
    )

    @GET("/api/user/orders/pending")
    fun unpaidHistory(
        @Header("Authorization") token: String
    ): Call<HistoryResponse>

    data class HistoryResponse(val status: Int, val values: List<Order>)

    @GET("/api/user/orders")
    fun allHistory(
        @Header("Authorization") token: String
    ): Call<HistoryResponse>

    @GET("/api/user/orders/process")
    fun processHistory(
        @Header("Authorization") token: String
    ): Call<HistoryResponse>

    @GET("/api/user/order/{id_history}")
    fun detailHistory(
        @Header("Authorization") token: String,
        @Path("id_history") id_history: Int
    ): Call<detailResponse>

    data class detailResponse(
        val status: Int,
        val values: ValuesDetail
    )

    data class ValuesDetail(
        @SerializedName("history") val history: Order,
        @SerializedName("menu") val menu: List<MenuOrder>
    )

    @GET("/api/user/information")
    fun infoPayment(
        @Header("Authorization") token: String,
    ): Call<infoPaymentResponse>

    data class infoPaymentResponse(
        val status: Int,
        val values: infoPaymentDetail
    )

    data class infoPaymentDetail(
        val id_information: Int,
        val bank_name: String,
        val bank_account: String
    )

    @PUT("/api/user/profile/password")
    fun updatePassword(
        @Header("Authorization") token: String,
        @Body requestBody: Map<String, String>
    ): Call<Void?>?

    @PUT("/api/user/profile/edit")
    fun updateUserProfile(
        @Header("Authorization") token: String,
        @Body requestBody: Map<String, String>
    ): Call<Void?>?

    @POST("/api/user/register")
    fun register(
        @Body requestBody: Map<String, String>
    ): Call<Void?>?

}