package com.vikmanz.shpppro.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.vikmanz.shpppro.data.ContactsService
import com.vikmanz.shpppro.data.contactModel.Contact
import com.vikmanz.shpppro.databinding.AddContactActivityMyContactsBinding
import com.vikmanz.shpppro.utilits.log
import com.vikmanz.shpppro.utilits.setContactPhoto
import com.vikmanz.shpppro.utilits.setContactPhotoFromUri


class AddContactDialogFragment(contactsService: ContactsService) : DialogFragment() {

    constructor() : this(ContactsService()) {
        // doesn't do anything special
    }

    interface ConfirmationListener {
        fun addContactConfirmButtonClicked(contact: Contact)
        //fun cancelButtonClicked()
    }

    private val _contactsService = contactsService
    private var imgUri: Uri? = null


    private lateinit var listener: ConfirmationListener

    private lateinit var _binding: AddContactActivityMyContactsBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            // Instantiate the ConfirmationListener so we can send events to the host
            listener = activity as ConfirmationListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException(activity.toString() + " must implement ConfirmationListener")
        }
    }

    @SuppressLint("UseGetLayoutInflater")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        if (savedInstanceState != null) restoreImageUri(savedInstanceState)

        _binding = AddContactActivityMyContactsBinding.inflate(LayoutInflater.from(context))
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            with(_binding) {
                updateAvatarImage()
                _binding.imageViewAvatarAddContact.setOnClickListener { requestDefaultImage() }
                buttonAddPhotoFromGaleryAddContact.setOnClickListener { requestImage() }
                buttonSaveAddNewContactActivityMyContacts.setOnClickListener {
                    dialog?.dismiss()
                    listener.addContactConfirmButtonClicked(
                        _contactsService.getOneContact(
                            id = Math.random().toLong(),
                            photoUrl = _contactsService.getCurrentContactPhotoUrl(),
                            photoUri = imgUri,
                            photoIndex = _contactsService.getCurrentPhotoCounter(),
                            name = textInputUserNameAddContact.editText?.text.toString(),
                            career = textInputCareerAddContact.editText?.text.toString(),
                            email = textInputCareerAddContact.editText?.text.toString(),
                            phone = textInputCareerAddContact.editText?.text.toString(),
                            address = textInputCareerAddContact.editText?.text.toString(),
                            birthday = textInputCareerAddContact.editText?.text.toString()
                        )
                    )
                    _contactsService.incrementPhotoCounter()
                }
                buttonBackAddContact.setOnClickListener { dialog?.cancel() }
            }

            // Pass null as the parent view because its going in the dialog layout
            builder.setView(_binding.root).create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun restoreImageUri(savedInstanceState: Bundle) {
        if (SDK_INT >= 33) {
            imgUri = savedInstanceState.getParcelable(PHOTO_STATE_KEY, Uri::class.java)
        }
        else {
            @Suppress("DEPRECATION")
            imgUri = savedInstanceState.getParcelable(PHOTO_STATE_KEY) as? Uri
        }
        log("uri loaded! uri - $imgUri")
    }

    private fun requestDefaultImage() {
        _contactsService.incrementPhotoCounter()
        if (imgUri != null) imgUri = null
        updateAvatarImage()
    }

    private fun requestImage() {
        requestImageLauncher.launch("image/*")
    }

    private val requestImageLauncher =                         // req img
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                imgUri = uri
                updateAvatarImage()
            }
        }

    private fun updateAvatarImage() {
        if (imgUri == null) {
            _binding.imageViewAvatarAddContact.setContactPhoto(_contactsService.getCurrentContactPhotoUrl())
            log("img update -default")
        } else {
            _binding.imageViewAvatarAddContact.setContactPhotoFromUri(imgUri)
            log("img update - $imgUri")
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(PHOTO_STATE_KEY, imgUri)
    }

    companion object {
        // Save/Load State Keys. Don't need to change.
        private const val PHOTO_STATE_KEY = "PHOTO_ADD_CONTACT_DIALOG"
    }

}