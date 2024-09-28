package br.edu.utfpr.trabalhofinal.data

import java.math.BigDecimal
import java.time.LocalDate

data class Conta(
    val id: Int = 0,
    val descricao: String = "",
    val data: LocalDate = LocalDate.now(),
    val valor: BigDecimal = BigDecimal.ZERO,
    val paga: Boolean = false,
    val tipo: TipoContaEnum = TipoContaEnum.DESPESA
)