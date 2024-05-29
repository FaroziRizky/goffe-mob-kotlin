package com.example.goffee.Models

data class CartResponse(
    val status: Int,
    val values: List<Cart>
)

data class Cart(
    val id_cart: Int,
    val id_user: Int,
    val checkoutable: Boolean,
    val cart_item: List<CartItem>
)

data class CartItem(
    val id_cart_item: Int,
    val id_menu: Int,
    var amount: Int,
    val menu_name: String,
    val variant: String,
    val information: String,
    val picture: String,
    val price: Int,
    val status: Int,
    val checkoutable: Int
)
