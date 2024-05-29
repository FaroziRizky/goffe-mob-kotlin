package com.example.goffee.Utils

import java.text.SimpleDateFormat
import java.util.*

object DateTimeFormatter {

    fun formatDateTime(input: String): String? {
        return try {
            // Define input and output formats with Indonesian locale
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale("id", "ID"))
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")

            val outputFormat = SimpleDateFormat("HH.mm EEEE, dd MMMM yyyy", Locale("id", "ID"))

            // Parse the input date string
            val date = inputFormat.parse(input)

            // Format the date to the desired output string
            date?.let {
                outputFormat.format(date)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
