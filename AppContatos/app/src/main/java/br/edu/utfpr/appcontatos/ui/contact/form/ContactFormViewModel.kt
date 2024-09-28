package br.edu.utfpr.appcontatos.ui.contact.form

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.utfpr.appcontatos.data.ContactDatasource
import br.edu.utfpr.appcontatos.ui.Arguments
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ContactFormViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val contactId: Int = savedStateHandle
        .get<String>(Arguments.CONTACT_ID)
        ?.toIntOrNull() ?: 0
    var uiState: ContactFormState by mutableStateOf(
        ContactFormState(contactId = contactId)
    )

    init {
        if (contactId > 0) {
            loadContact()
        }
    }

    fun loadContact() {
        uiState = uiState.copy(
            isLoading = true,
            hasErrorLoading = false
        )
        viewModelScope.launch {
            delay(2000)
            val contact = ContactDatasource.instance.findById(contactId)
            uiState = if (contact == null) {
                uiState.copy(
                    isLoading = false,
                    hasErrorLoading = true
                )
            } else {
                uiState.copy(
                    isLoading = false,
                    contact = contact,
                    firstName = uiState.firstName.copy(
                        value = contact.firstName
                    ),
                    lastName = uiState.lastName.copy(
                        value = contact.lastName
                    ),
                    phone = uiState.phone.copy(
                        value = contact.phoneNumber
                    ),
                    email = uiState.email.copy(
                        value = contact.email
                    ),
                    isFavorite = uiState.isFavorite.copy(
                        value = contact.isFavorite
                    ),
                    birthDate = uiState.birthDate.copy(
                        value = contact.birthDate
                    ),
                    type = uiState.type.copy(
                        value = contact.type
                    )
                )
            }
        }
    }
}












