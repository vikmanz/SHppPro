package com.vikmanz.shpppro.presentation.main.my_contacts_list.add_contact

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vikmanz.shpppro.data.contact_model.Contact
import com.vikmanz.shpppro.data.repository.interfaces.Repository
import javax.inject.Inject

/**
 * ViewModel for My Contacts Activity.
 */
class AddContactDialogFragmentViewModel : ViewModel() {

    @Inject
    lateinit var contactsRepository: Repository<Contact>

    // avatar
    val currentPhoto = MutableLiveData<Any>(getFakePhotoUrl())
    private fun getFakePhotoUrl() = contactsRepository.getCurrentContactPhotoUrl()

    fun changeFakePhotoToNext() {
        contactsRepository.incrementPhotoCounter()
        currentPhoto.value = getFakePhotoUrl()
    }

    fun setPhotoUri(uri: Uri) {
        currentPhoto.value = uri
    }

    fun createNewContact(
        name: String,
        career: String,
        email: String,
        phone: String,
        address: String,
        birthday: String
    ) {
        val newContact = contactsRepository.createContact(
            contactPhotoLink = getCurrentPhoto(),
            photoIndex = contactsRepository.getCurrentPhotoCounter(),
            name = name,
            career = career,
            email = email,
            phone = phone,
            address = address,
            birthday = birthday
        )
        contactsRepository.addContact(newContact)
    }

    private fun getCurrentPhoto(): Any = with(currentPhoto.value) {
        if (this is Uri) this else getFakePhotoUrl()
    }


}