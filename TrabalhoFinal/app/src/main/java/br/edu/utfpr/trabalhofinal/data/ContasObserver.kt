package br.edu.utfpr.trabalhofinal.data

interface ContasObserver {
    fun onUpdate(contasAtualizadas: List<Conta>)
}