package com.example.goffee.Models


data class MenuOrder(
    val idItemHistory: Int,
    val idHistory: Int,
    val menu_name: String,
    val variant: String,
    val price: Int,
    val amount: Int
)
