package br.edu.utfpr.appcontatos.data

import kotlin.random.Random

class ContactDatasource private constructor() {
    companion object {
        val instance:ContactDatasource by lazy {
            ContactDatasource()
        }
    }

    private val contacts: MutableList<Contact> = mutableListOf()

    init {
        contacts.addAll(generateContacts())
    }

    fun findAll(): List<Contact> = contacts.toList()

    fun findById(id: Int): Contact? = contacts.firstOrNull { it.id == id }

    fun save(contact: Contact): Contact = if (contact.id > 0) {
        // atualizar
        val index: Int = contacts.indexOfFirst { it.id == contact.id }
        contact.also { contacts[index] = it }
    } else {
        // inserir
        val maxId: Int = contacts.maxBy { it.id }.id
        contact.copy(id = maxId + 1).also { contacts.add(it) }
    }

    fun delete(contact: Contact) {
        if (contact.id > 0) {
            contacts.removeIf { it.id == contact.id }
        }
    }
}

fun generateContacts(): List<Contact> {
    val firstNames = listOf("João", "José", "Everton", "Marcos", "André", "Anderson", "Antônio",
        "Laura", "Ana", "Maria", "Joaquina", "Suelen")
    val lastNames = listOf("Do Carmo", "Oliveira", "Dos Santos", "Da Silva", "Brasil", "Pichetti",
        "Cordeiro", "Silveira", "Andrades", "Cardoso")
    val contacts: MutableList<Contact> = mutableListOf()
    for (i in 0..19) {
        var generatedNewContact = false
        while (!generatedNewContact) {
            val firstNameIndex = Random.nextInt(firstNames.size)
            val lastNameIndex = Random.nextInt(lastNames.size)
            val newContact = Contact(
                id = i+1,
                firstName = firstNames[firstNameIndex],
                lastName = lastNames[lastNameIndex]
            )
            if (!contacts.any { it.fullName == newContact.fullName }) {
                contacts.add(newContact)
                generatedNewContact = true
            }
        }
    }
    return contacts
}