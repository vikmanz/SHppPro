package com.vikmanz.shpppro.data

import android.net.Uri
import android.util.Log
import com.github.javafaker.Faker
import com.vikmanz.shpppro.constants.Constants.START_NUMBER_OF_CONTACTS
import com.vikmanz.shpppro.data.contactModel.Contact
import com.vikmanz.shpppro.utilits.log
import java.util.*
import kotlin.collections.ArrayList

class ContactsService {

    private var contacts = listOf<Contact>() // All users

    private val faker = Faker.instance() // face data to user

    init {
        contacts = (0 until START_NUMBER_OF_CONTACTS).map {
            generateRandomContact()
        }.toMutableList()
    }

    fun getContacts(): List<Contact> {
        return contacts
    }

    fun getOneContact(
        id: Long,
        photoUrl: String,
        photoUri: Uri?,
        photoIndex: Int,
        name: String,
        career: String,
        email: String,
        phone: String,
        address: String,
        birthday: String
    ): Contact {
        val newContact = Contact(
            contactId = id,
            contactPhotoUrl = photoUrl,
            contactPhotoUri = photoUri,
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

    private fun generateRandomContact(): Contact = getOneContact(
            id = getRandomId(),
            photoUrl = IMAGES[imgCounter % IMAGES.size],
            photoUri = null,
            photoIndex = imgCounter,
            name = faker.name().fullName(),
            career = faker.company().name(),
            email = faker.internet().emailAddress(),
            phone = faker.phoneNumber().phoneNumber(),
            address = faker.address().fullAddress(),
            birthday = faker.date().birthday().toString()
        )

    fun createContactListFromPhonebookInfo(listOfContactsInformation: ArrayList<List<String>>): List<Contact> {
        val newContacts = (0 until listOfContactsInformation.size).map {
            getOneContact(
                id = getRandomId(),
                photoUrl = IMAGES[imgCounter % IMAGES.size],
                photoUri = null,
                photoIndex = imgCounter,
                name = listOfContactsInformation[it][0],
                career = listOfContactsInformation[it][1],
                email = faker.internet().emailAddress(),
                phone = faker.phoneNumber().phoneNumber(),
                address = faker.address().fullAddress(),
                birthday = faker.date().birthday().toString()
            )
        }.toMutableList()
        log("service return new list with size ${newContacts.size}")
        return newContacts
    }


    fun getCurrentContactPhotoUrl(): String {
        return IMAGES[imgCounter % IMAGES.size]
    }

    fun getCurrentPhotoCounter(): Int {
        return imgCounter
    }

    private fun getRandomId(): Long {
        return UUID.randomUUID().mostSignificantBits
    }

    fun incrementPhotoCounter() {
        imgCounter++
    }

    fun createFakeContacts(): List<Contact> {
        val newContacts = (0 until START_NUMBER_OF_CONTACTS).map {
            generateRandomContact()
        }.toMutableList()
        log("service return new fake list with size ${newContacts.size}")
        return newContacts
    }

    companion object {
        val IMAGES = mutableListOf(
            "https://images.unsplash.com/photo-1600267185393-e158a98703de?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=600&ixid=MnwxfDB8MXxyYW5kb218fHx8fHx8fHwxNjI0MDE0NjQ0&ixlib=rb-1.2.1&q=80&utm_campaign=api-credit&utm_medium=referral&utm_source=unsplash_source&w=800",
            "https://images.unsplash.com/photo-1579710039144-85d6bdffddc9?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=600&ixid=MnwxfDB8MXxyYW5kb218fHx8fHx8fHwxNjI0MDE0Njk1&ixlib=rb-1.2.1&q=80&utm_campaign=api-credit&utm_medium=referral&utm_source=unsplash_source&w=800",
            "https://images.unsplash.com/photo-1488426862026-3ee34a7d66df?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=600&ixid=MnwxfDB8MXxyYW5kb218fHx8fHx8fHwxNjI0MDE0ODE0&ixlib=rb-1.2.1&q=80&utm_campaign=api-credit&utm_medium=referral&utm_source=unsplash_source&w=800",
            "https://images.unsplash.com/photo-1620252655460-080dbec533ca?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=600&ixid=MnwxfDB8MXxyYW5kb218fHx8fHx8fHwxNjI0MDE0NzQ1&ixlib=rb-1.2.1&q=80&utm_campaign=api-credit&utm_medium=referral&utm_source=unsplash_source&w=800",
            "https://images.unsplash.com/photo-1613679074971-91fc27180061?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=600&ixid=MnwxfDB8MXxyYW5kb218fHx8fHx8fHwxNjI0MDE0NzUz&ixlib=rb-1.2.1&q=80&utm_campaign=api-credit&utm_medium=referral&utm_source=unsplash_source&w=800",
            "https://images.unsplash.com/photo-1485795959911-ea5ebf41b6ae?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=600&ixid=MnwxfDB8MXxyYW5kb218fHx8fHx8fHwxNjI0MDE0NzU4&ixlib=rb-1.2.1&q=80&utm_campaign=api-credit&utm_medium=referral&utm_source=unsplash_source&w=800",
            "https://images.unsplash.com/photo-1545996124-0501ebae84d0?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=600&ixid=MnwxfDB8MXxyYW5kb218fHx8fHx8fHwxNjI0MDE0NzY1&ixlib=rb-1.2.1&q=80&utm_campaign=api-credit&utm_medium=referral&utm_source=unsplash_source&w=800",
            "https://images.unsplash.com/flagged/photo-1568225061049-70fb3006b5be?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=600&ixid=MnwxfDB8MXxyYW5kb218fHx8fHx8fHwxNjI0MDE0Nzcy&ixlib=rb-1.2.1&q=80&utm_campaign=api-credit&utm_medium=referral&utm_source=unsplash_source&w=800",
            "https://images.unsplash.com/photo-1567186937675-a5131c8a89ea?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=600&ixid=MnwxfDB8MXxyYW5kb218fHx8fHx8fHwxNjI0MDE0ODYx&ixlib=rb-1.2.1&q=80&utm_campaign=api-credit&utm_medium=referral&utm_source=unsplash_source&w=800",
            "https://images.unsplash.com/photo-1546456073-92b9f0a8d413?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=600&ixid=MnwxfDB8MXxyYW5kb218fHx8fHx8fHwxNjI0MDE0ODY1&ixlib=rb-1.2.1&q=80&utm_campaign=api-credit&utm_medium=referral&utm_source=unsplash_source&w=800"
        )

        private var imgCounter = 0
    }

}