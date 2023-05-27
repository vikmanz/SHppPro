package com.vikmanz.shpppro.ui.main.main_fragment.my_contacts_list

import androidx.lifecycle.MutableLiveData
import com.vikmanz.shpppro.base.BaseViewModel
import com.vikmanz.shpppro.data.contact_model.ContactListItem
import com.vikmanz.shpppro.data.repository.interfaces.Repository
import com.vikmanz.shpppro.ui.main.main_fragment.MainViewPagerFragmentDirections
import com.vikmanz.shpppro.utilits.extensions.swapBoolean
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

// FakeData (true) or PhoneData (false) view first on myContacts
private const val FAKE_LIST_FIRST = true

/**
 * ViewModel for My Contacts Activity.
 */
@HiltViewModel
class MyContactsListViewModel @Inject constructor(
    contactsRepository: Repository<ContactListItem>
) : BaseViewModel() {

    private val _repository = contactsRepository

    private var lastDeletedContact: ContactListItem? = null
    private var lastDeletedPosition: Int = 0

    init {
        _repository.setFakeContacts()
    }

    /**
     * Create fake contact list and Flow to take it from outside.
     */
    val contactList = _repository.contactList

    /**
     * Variables to control swap between fake contacts and phone contacts lists.
     */
    val fakeListActivated = MutableLiveData(FAKE_LIST_FIRST)

    /**
     * Delete contact from list of contacts.
     */
    fun deleteContact(contact: ContactListItem): Boolean {
        if (contact == lastDeletedContact) return false
        lastDeletedContact = contact
        lastDeletedPosition = getContactPosition(contact)
        _repository.deleteContact(contact)
        return true
    }

    /**
     * Delete contact from list of contacts.
     */
    fun restoreLastDeletedContact() {
        lastDeletedContact?.let {
            if (!_repository.isContainsContact(it)) {
                addContactToPosition(it, lastDeletedPosition)
                lastDeletedContact = null
            }
        }
    }

    /**
     * Get contact from list via index.
     */
    fun getContact(index: Int): ContactListItem? {
        return _repository.getContact(index)
    }

    /**
     * Change contact list to fake contacts list.
     */
    fun changeContactList() {
        if (fakeListActivated.value == true) {
            _repository.setPhoneContacts()
        } else {
            _repository.setFakeContacts()
        }
        fakeListActivated.swapBoolean()
    }

    fun onContactPressed(contactID: Long) {
        navigate(MainViewPagerFragmentDirections.startContactDetails(contactID))
    }

    /**
     * Get contact position in list of contacts.
     */
    private fun getContactPosition(contact: ContactListItem): Int {
        return _repository.getContactPosition(contact)
    }

    /**
     * Add new contact to list of contacts to concrete index.
     */
    private fun addContactToPosition(contact: ContactListItem, index: Int) {
        _repository.addContact(contact, index)
    }
}