package br.edu.utfpr.appcontatos.ui.contact.form

import br.edu.utfpr.appcontatos.data.Contact
import br.edu.utfpr.appcontatos.data.ContactTypeEnum
import java.time.LocalDate

data class FormField<T>(
    val value: T,
    val errorMessageCode: Int = 0
) {
    val hasError get(): Boolean = errorMessageCode > 0
    val isValid get(): Boolean = !hasError
}

data class ContactFormState(
    val contactId: Int = 0,
    val isLoading: Boolean = false,
    val contact: Contact = Contact(),
    val hasErrorLoading: Boolean = false,
    val isSaving: Boolean = false,
    val hasErrorSaving: Boolean = false,
    val contactSaved: Boolean = false,
    val firstName: FormField<String> = FormField(value = ""),
    val lastName: FormField<String> = FormField(value = ""),
    val phone: FormField<String> = FormField(value = ""),
    val email: FormField<String> = FormField(value = ""),
    val isFavorite: FormField<Boolean> = FormField(value = false),
    val birthDate: FormField<LocalDate> = FormField(value = LocalDate.now()),
    val type: FormField<ContactTypeEnum> = FormField(value = ContactTypeEnum.PERSONAL),
    val patrimonio: FormField<String> = FormField(value = "")
) {
    val isNewContact get(): Boolean = contactId <= 0
    val isValidForm get(): Boolean = firstName.isValid &&
            lastName.isValid &&
            phone.isValid &&
            email.isValid &&
            isFavorite.isValid &&
            birthDate.isValid &&
            type.isValid &&
            patrimonio.isValid
}







