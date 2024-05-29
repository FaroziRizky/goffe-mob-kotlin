package com.example.goffee

class PriceFormatter {
    companion object {
        fun format(price: String): String {
            return try {
                val number = price.replace("[^\\d]".toRegex(), "").toLong()
                String.format("%,d", number).replace(',', '.')
            } catch (e: NumberFormatException) {
                price
            }
        }
    }
}
