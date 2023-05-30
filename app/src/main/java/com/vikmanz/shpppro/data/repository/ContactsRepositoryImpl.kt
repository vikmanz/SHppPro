package com.vikmanz.shpppro.data.repository

import android.net.Uri
import com.github.javafaker.Faker
import com.vikmanz.shpppro.constants.Constants.START_NUMBER_OF_CONTACTS
import com.vikmanz.shpppro.data.contact_model.Contact
import com.vikmanz.shpppro.data.repository.interfaces.Repository
import com.vikmanz.shpppro.data.utils.ContactsPhoneInfoTaker
import com.vikmanz.shpppro.utilits.extensions.log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.net.URL
import java.util.UUID
import javax.inject.Inject

/**
 * Implementation of repository.
 * Main service to create contacts objects from information on from random.
 */
class ContactsRepositoryImpl @Inject constructor(
) : Repository<Contact> {

    //This object is a wrapper. if we pass it a new object it will call emit
    private val _contactList = MutableStateFlow(listOf<Contact>())

    //this object sends out the immutable list
    override val contactList = _contactList.asStateFlow()

    private val faker = Faker.instance() // fake data generator.

    private var imgCounter = 0  // counter to switch random images.

    private val multiselectList = ArrayList<Contact>()

    init {
        setFakeContacts()
    }

    /**
     * Create and return one contact with information from input.
     */
    override fun createContact(
        contactPhotoLink: Any,
        photoIndex: Int,
        name: String,
        career: String,
        email: String,
        phone: String,
        address: String,
        birthday: String
    ): Contact {
        val newContact = Contact(
            contactId = getRandomId(),
            contactPhotoLink = contactPhotoLink,
            contactPhotoIndex = photoIndex,
            contactName = name,
            contactCareer = career,
            contactEmail = email,
            contactPhone = phone,
            contactAddress = address,
            contactBirthday = birthday
        )
        imgCounter++
        return newContact
    }

    /**
     * Create and return one contact with random fake information.
     */
    override fun generateRandomContact(): Contact =
        createContact(
            contactPhotoLink = IMAGES[imgCounter % IMAGES.size],
            photoIndex = imgCounter,
            name = faker.name().fullName(),
            career = faker.company().name(),
            email = faker.internet().emailAddress(),
            phone = faker.phoneNumber().phoneNumber(),
            address = faker.address().fullAddress(),
            birthday = faker.date().birthday().toString(),
        )

    /**
     * Create and return list of fake contacts.
     */
    override fun setFakeContacts() {
        _contactList.value = (0 until START_NUMBER_OF_CONTACTS).map {
            generateRandomContact()
        }.toMutableList()
    }

    /**
     * Create and return contact list with information from phonebook ArrayList<[name: String, phone: String]>.
     */
    override fun setPhoneContacts() {
        val listOfContactsInformation = ContactsPhoneInfoTaker().getPhonebookContactsInfo()
        _contactList.value = (0 until listOfContactsInformation.size).map {
            createContact(
                contactPhotoLink = Uri.parse(listOfContactsInformation[it][1]).let { uri ->
                    if (uri == Uri.EMPTY) IMAGES[imgCounter % IMAGES.size] else uri
                },
                photoIndex = imgCounter,
                name = listOfContactsInformation[it][0],
                career = listOfContactsInformation[it][4],
                email = listOfContactsInformation[it][3],
                phone = listOfContactsInformation[it][2],
                address = faker.address().fullAddress(),
                birthday = faker.date().birthday().toString()
            )
        }.toMutableList()
    }

    /**
     * Return random id for new contacts.
     */
    private fun getRandomId(): Long {
        return UUID.randomUUID().mostSignificantBits
    }

    /**
     * Return current random photo url.
     */
    override fun getCurrentContactPhotoUrl(): URL {
        return IMAGES[imgCounter % IMAGES.size]
    }

    /**
     * Return current random photo counter.
     */
    override fun getCurrentPhotoCounter(): Int {
        return imgCounter
    }

    /**
     * Change random photo counter to switch random image to next.
     */
    override fun incrementPhotoCounter() {
        imgCounter++
    }

    override fun addContact(contact: Contact) {
        addContact(contact, _contactList.value.size)
    }

    /**
     * Add new contact to list of contacts to concrete index.
     */
    override fun addContact(contact: Contact, index: Int) {
        _contactList.value = _contactList.value.toMutableList().apply { add(index, contact) }
    }

    /**
     * Delete contact from list of contacts.
     */
    override fun deleteContact(contact: Contact) {
        _contactList.value = _contactList.value.toMutableList().apply { remove(contact) }
    }

    /**
     * Get contact position in list of contacts.
     */
    override fun getContactPosition(contact: Contact): Int {
        return _contactList.value.indexOf(contact)
    }

    /**
     * Get contact from list via index.
     */
    override fun getContact(index: Int): Contact? =
        if (index >= 0 && index < _contactList.value.size) _contactList.value[index]
        else null

    /**
     * Get contact from list via id.
     */
    override fun findContact(id: Long): Contact? {
        _contactList.value.forEach { if (it.contactId == id) return it }
        return null
    }

    override fun deleteMultipleContacts() {
        multiselectList.forEach { contact ->
            _contactList.value = _contactList.value.toMutableList().apply {
                remove(contact)
            }
        }

    }

    override fun clearMultiselect() {
        _contactList.value = _contactList.value.toMutableList().onEach { it.isChecked = false }
        multiselectList.clear()
    }

    override fun checkContactInMultiselect(contact: Contact): Boolean {
        log("list count start: ${multiselectList.size}")
        contact.isChecked = !contact.isChecked
        if (multiselectList.contains(contact)) {
            multiselectList.remove(contact)
            log("list count: ${multiselectList.size}")
        } else {
            multiselectList.add(contact)
            log("list count: ${multiselectList.size}")
        }
        log("list count end: ${multiselectList.size}")
        return multiselectList.size == 0
    }


    override fun isContainsContact(contact: Contact): Boolean {
        return _contactList.value.contains(contact)
    }


    /**
     * Random images for fake data or adding new contacts.
     */
    companion object {
        val IMAGES = mutableListOf(
            URL("https://images.unsplash.com/photo-1600267185393-e158a98703de?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=600&ixid=MnwxfDB8MXxyYW5kb218fHx8fHx8fHwxNjI0MDE0NjQ0&ixlib=rb-1.2.1&q=80&utm_campaign=api-credit&utm_medium=referral&utm_source=unsplash_source&w=800"),
            URL("https://images.unsplash.com/photo-1579710039144-85d6bdffddc9?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=600&ixid=MnwxfDB8MXxyYW5kb218fHx8fHx8fHwxNjI0MDE0Njk1&ixlib=rb-1.2.1&q=80&utm_campaign=api-credit&utm_medium=referral&utm_source=unsplash_source&w=800"),
            URL("https://images.unsplash.com/photo-1488426862026-3ee34a7d66df?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=600&ixid=MnwxfDB8MXxyYW5kb218fHx8fHx8fHwxNjI0MDE0ODE0&ixlib=rb-1.2.1&q=80&utm_campaign=api-credit&utm_medium=referral&utm_source=unsplash_source&w=800"),
            URL("https://images.unsplash.com/photo-1620252655460-080dbec533ca?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=600&ixid=MnwxfDB8MXxyYW5kb218fHx8fHx8fHwxNjI0MDE0NzQ1&ixlib=rb-1.2.1&q=80&utm_campaign=api-credit&utm_medium=referral&utm_source=unsplash_source&w=800"),
            URL("https://images.unsplash.com/photo-1613679074971-91fc27180061?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=600&ixid=MnwxfDB8MXxyYW5kb218fHx8fHx8fHwxNjI0MDE0NzUz&ixlib=rb-1.2.1&q=80&utm_campaign=api-credit&utm_medium=referral&utm_source=unsplash_source&w=800"),
            URL("https://images.unsplash.com/photo-1485795959911-ea5ebf41b6ae?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=600&ixid=MnwxfDB8MXxyYW5kb218fHx8fHx8fHwxNjI0MDE0NzU4&ixlib=rb-1.2.1&q=80&utm_campaign=api-credit&utm_medium=referral&utm_source=unsplash_source&w=800"),
            URL("https://images.unsplash.com/photo-1545996124-0501ebae84d0?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=600&ixid=MnwxfDB8MXxyYW5kb218fHx8fHx8fHwxNjI0MDE0NzY1&ixlib=rb-1.2.1&q=80&utm_campaign=api-credit&utm_medium=referral&utm_source=unsplash_source&w=800"),
            URL("https://images.unsplash.com/photo-1567186937675-a5131c8a89ea?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=600&ixid=MnwxfDB8MXxyYW5kb218fHx8fHx8fHwxNjI0MDE0ODYx&ixlib=rb-1.2.1&q=80&utm_campaign=api-credit&utm_medium=referral&utm_source=unsplash_source&w=800")
        )
    }

}