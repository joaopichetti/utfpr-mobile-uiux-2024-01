package br.edu.utfpr.trabalhofinal.ui.conta.lista

import br.edu.utfpr.trabalhofinal.data.Conta

data class ListaContasState(
    val carregando: Boolean = false,
    val erroAoCarregar: Boolean = false,
    val contas: List<Conta> = listOf()
)