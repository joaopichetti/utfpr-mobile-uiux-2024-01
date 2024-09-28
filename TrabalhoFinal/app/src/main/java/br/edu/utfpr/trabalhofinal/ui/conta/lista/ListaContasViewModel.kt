package br.edu.utfpr.trabalhofinal.ui.conta.lista

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.utfpr.trabalhofinal.data.Conta
import br.edu.utfpr.trabalhofinal.data.ContaDatasource
import br.edu.utfpr.trabalhofinal.data.ContasObserver
import kotlinx.coroutines.launch

class ListaContasViewModel : ViewModel(), ContasObserver {
    var state: ListaContasState by mutableStateOf(ListaContasState())
        private set

    init {
        ContaDatasource.instance.registrarObserver(this)
        carregarContas()
    }

    override fun onCleared() {
        ContaDatasource.instance.removerObserver(this)
        super.onCleared()
    }

    fun carregarContas() {
        state = state.copy(
            carregando = true,
            erroAoCarregar = false
        )
        val contas = ContaDatasource.instance.findAll()
        state = state.copy(
            carregando = false,
            contas = contas
        )
    }

    override fun onUpdate(contasAtualizadas: List<Conta>) {
        state = state.copy(contas = contasAtualizadas)
    }
}