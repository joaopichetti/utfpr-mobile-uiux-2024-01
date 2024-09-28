package br.edu.utfpr.appcontatos.ui.contact.form

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.utfpr.appcontatos.R
import br.edu.utfpr.appcontatos.data.ContactDatasource
import br.edu.utfpr.appcontatos.data.ContactTypeEnum
import br.edu.utfpr.appcontatos.ui.Arguments
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.random.Random

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
                    ),
                    patrimonio = uiState.patrimonio.copy(
                        value = contact.patrimonio.toString()
                    )
                )
            }
        }
    }

    fun onFirstNameChanged(newFirstName: String) {
        if (uiState.firstName.value != newFirstName) {
            uiState = uiState.copy(
                firstName = uiState.firstName.copy(
                    value = newFirstName,
                    errorMessageCode = validateFirstName(newFirstName)
                )
            )
        }
    }

    private fun validateFirstName(firstName: String): Int =
        if (firstName.isBlank()) R.string.nome_obrigatorio
        else 0


    fun onLastNameChanged(newLastName: String) {
        if (uiState.lastName.value != newLastName) {
            uiState = uiState.copy(
                lastName = uiState.lastName.copy(
                    value = newLastName
                )
            )
        }
    }

    fun onPhoneChanged(value: String) {
        val newPhone = value.replace(Regex("\\D"), "")
        if (newPhone.length <= 11 && uiState.phone.value != newPhone) {
            uiState = uiState.copy(
                phone = uiState.phone.copy(
                    value = newPhone,
                    errorMessageCode = validatePhone(newPhone)
                )
            )
        }
    }

    private fun validatePhone(phone: String): Int =
        if (phone.isNotBlank() && (phone.length < 10 || phone.length > 11)) {
            R.string.telefone_invalido
        } else {
            0
        }

    fun onEmailChanged(newEmail: String) {
        if (uiState.email.value != newEmail) {
            uiState = uiState.copy(
                email = uiState.email.copy(
                    value = newEmail,
                    errorMessageCode = validateEmail(newEmail)
                )
            )
        }
    }

    private fun validateEmail(email: String): Int =
        if (email.isNotBlank() && !Regex("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$").matches(email)) {
            R.string.email_invalido
        } else {
            0
        }

    fun onIsFavoriteChanged(isFavorite: Boolean) {
        if (uiState.isFavorite.value != isFavorite) {
            uiState = uiState.copy(
                isFavorite = uiState.isFavorite.copy(value = isFavorite)
            )
        }
    }

    fun onBirthDateChanged(newBirthDate: LocalDate) {
        if (uiState.birthDate.value != newBirthDate) {
            uiState = uiState.copy(
                birthDate = uiState.birthDate.copy(
                    value = newBirthDate
                )
            )
        }
    }

    fun onTypeChanged(newType: ContactTypeEnum) {
        if (uiState.type.value != newType) {
            uiState = uiState.copy(
                type = uiState.type.copy(
                    value = newType
                )
            )
        }
    }

    fun onPatrimonioChanged(newPatrimonio: String) {
        if (uiState.patrimonio.value != newPatrimonio) {
            uiState = uiState.copy(
                patrimonio = uiState.patrimonio.copy(
                    value = newPatrimonio,
                    errorMessageCode = validatePatrimonio(newPatrimonio)
                )
            )
        }
    }

    private fun validatePatrimonio(patrimonio: String): Int {
        if (patrimonio.isBlank()) return 0

        return try {
            BigDecimal(patrimonio)
            0
        } catch (_: NumberFormatException) {
            R.string.patrimonio_invalido
        }
    }

    private fun isValidForm(): Boolean {
        uiState = uiState.copy(
            firstName = uiState.firstName.copy(
                errorMessageCode = validateFirstName(uiState.firstName.value)
            ),
            phone = uiState.phone.copy(
                errorMessageCode = validatePhone(uiState.phone.value)
            ),
            email = uiState.email.copy(
                errorMessageCode = validateEmail(uiState.email.value)
            ),
            patrimonio = uiState.patrimonio.copy(
                errorMessageCode = validatePatrimonio(uiState.patrimonio.value)
            )
        )
        return uiState.isValidForm
    }

    fun save() {
        if (isValidForm()) {
            uiState = uiState.copy(
                isSaving = true,
                hasErrorSaving = false
            )
            viewModelScope.launch {
                delay(2000)
                val hasError = Random.nextBoolean()
                uiState = if (!hasError) {
                    val contactToSave = uiState.contact.copy(
                        firstName = uiState.firstName.value,
                        lastName = uiState.lastName.value,
                        phoneNumber = uiState.phone.value,
                        email = uiState.email.value,
                        isFavorite = uiState.isFavorite.value,
                        type = uiState.type.value,
                        birthDate = uiState.birthDate.value,
                        patrimonio = if (uiState.patrimonio.value.isBlank()) {
                            BigDecimal.ZERO
                        } else {
                            BigDecimal(uiState.patrimonio.value)
                        }
                    )
                    ContactDatasource.instance.save(contactToSave)
                    uiState.copy(
                        isSaving = false,
                        contactSaved = true
                    )
                } else {
                    uiState.copy(
                        isSaving = false,
                        hasErrorSaving = true
                    )
                }
            }
        }
    }
}












