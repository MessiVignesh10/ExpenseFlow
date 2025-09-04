package com.example.expenseflow.core.utils

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun formattedDescription(input : String):String{
    if (input.length >1) {
        return input.take(1).toUpperCase() + input.substring(1..input.length - 1)
    } else return input
}

fun dateFormatter(input : Long) : String{
    val zoneId = ZoneId.systemDefault()
    val localDate = Instant.ofEpochMilli(input).atZone(zoneId).toLocalDate()
    val formatter = DateTimeFormatter.ofPattern("MMM d ,yyyy", Locale.getDefault())
    return localDate.format(formatter)
}
