package br.edu.utfpr.appcontatos.utils

import java.math.BigDecimal
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

//55988887777
//(55) 98888-7777
//5588887777
//(55) 8888-7777
fun String.toFormattedPhone(): String = mapIndexed { index, char ->
    when {
        index == 0 -> "($char"
        index == 2 -> ") $char"
        (index == 6 && length < 11) ||
                (index == 7 && length == 11) -> "-$char"
        else -> char
    }
}.joinToString("")

fun BigDecimal.format(): String {
    val formatter = DecimalFormat("R$#,##0.00")
    return formatter.format(this)
}

fun LocalDate.format(): String {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    return format(formatter)
}