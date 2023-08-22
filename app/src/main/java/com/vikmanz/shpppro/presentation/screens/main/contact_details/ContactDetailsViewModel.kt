package com.vikmanz.shpppro.presentation.screens.main.contact_details

import androidx.lifecycle.SavedStateHandle
import com.vikmanz.shpppro.domain.repository.ContactsRepositoryLocal
import com.vikmanz.shpppro.domain.repository.ShPPContactsRepository
import com.vikmanz.shpppro.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ContactDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    contactsRepository: ShPPContactsRepository,
) : BaseViewModel() {

    private val navArgs = ContactDetailsFragmentArgs.fromSavedStateHandle(savedStateHandle)

    private val contactItem = contactsRepository.findContact(navArgs.contactID)!!

    val contactName = contactItem.contact.name
    val contactCareer = contactItem.contact.career
    val contactAddress = contactItem.contact.address
    val contactPhoto = contactItem.contact.image

    fun onButtonBackPressed() {
        navigateBack()
    }

}