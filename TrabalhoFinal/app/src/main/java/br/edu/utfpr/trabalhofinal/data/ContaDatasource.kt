package br.edu.utfpr.trabalhofinal.data

class ContaDatasource private constructor() {
    companion object {
        val instance:ContaDatasource by lazy {
            ContaDatasource()
        }
    }

    private val contas: MutableList<Conta> = mutableListOf()
    private val contasObservers: MutableList<ContasObserver> = mutableListOf()

    fun registrarObserver(contasObserver: ContasObserver) {
        contasObservers.add(contasObserver)
    }

    fun removerObserver(contasObserver: ContasObserver) {
        contasObservers.remove(contasObserver)
    }

    private fun notificarObservers() {
        contasObservers.forEach { it.onUpdate(findAll()) }
    }

    fun salvar(conta: Conta): Conta = if (conta.id > 0) {
        val indice = contas.indexOfFirst { it.id == conta.id }
        conta.also { contas[indice] = it }
    } else {
        val maiorId = contas.maxByOrNull { it.id }?.id ?: 0
        conta.copy(id = maiorId + 1).also { contas.add(it) }
    }.also {
        notificarObservers()
    }

    fun remover(conta: Conta) {
        if (conta.id > 0) {
            contas.removeIf { it.id == conta.id }
            notificarObservers()
        }
    }

    fun findAll(): List<Conta> = contas.toList()

    fun findOne(id: Int): Conta? = contas.firstOrNull { it.id == id }
}
