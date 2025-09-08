package com.example.expenseflow.core.utils

import com.example.expenseflow.data.model.Expense
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val FMT_A = DateTimeFormatter.ofPattern("MMM D, yyyy", Locale.US)
private val FMT_B = DateTimeFormatter.ofPattern("MMM D ,yyyy", Locale.US)

private fun String.safeDateFormatter():LocalDate{
    return runCatching { LocalDate.parse(this , FMT_A) }.getOrElse {
        runCatching { LocalDate.parse(this , FMT_B ) }.getOrElse { LocalDate.now() }
    }
}

fun Expense.localDate():LocalDate{
    return date.safeDateFormatter()
}