package com.vikmanz.shpppro.presentation.screens.main.add_contact

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.vikmanz.shpppro.constants.Constants.MARGINS_OF_ELEMENTS
import com.vikmanz.shpppro.databinding.FragmentAddContactMyContactsBinding
import com.vikmanz.shpppro.presentation.base.BaseFragment
import com.vikmanz.shpppro.presentation.screens.main.add_contact.adapter.ContactsAdapter
import com.vikmanz.shpppro.presentation.screens.main.main_fragment.my_contacts_list.adapter.decorator.MarginItemDecoration
import com.vikmanz.shpppro.presentation.utils.extensions.setKeyboardVisibility
import com.vikmanz.shpppro.presentation.utils.extensions.setVisibleOrGone
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddContactFragment :
    BaseFragment<FragmentAddContactMyContactsBinding, AddContactViewModel>(
        FragmentAddContactMyContactsBinding::inflate
    ) {

    override val viewModel: AddContactViewModel by viewModels()


    /**
     * Create adapter for contacts recycler view.
     */
    private val adapterForRecycler = ContactsAdapter()


    /**
     * Set listeners for buttons.
     */
    override fun setListeners() {
        with(binding) {
            buttonMyContactsBackButton.setOnClickListener { viewModel.onButtonBackPressed() }
            buttonAddContactSearch.setOnClickListener { setSearchMode(true) }
            buttonAddContactCancelSearch.setOnClickListener { setSearchMode(false) }
            setSearchBarListeners()
        }
    }

    private fun setSearchBarListeners() {
        with(binding) {
            searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(newText: String?): Boolean {
                    filterList(newText)
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    filterList(newText)
                    return false
                }

                fun filterList(newText: String?) {
                    setNoContactsDisclaimerVisible(adapterForRecycler.filter(newText ?: ""))
                }
            })
        }
    }

    private fun observeContactsList() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.contactList.collect { contactList ->
                    adapterForRecycler.submitListFromViewModel(contactList)
                    adapterForRecycler.filter(binding.searchBar.query.toString())
                    // setNoContactsDisclaimerVisible(adapterForRecycler.currentList.isEmpty())
                }
            }
        }
    }

    private fun setSearchMode(isSearchMode: Boolean) {
        viewModel.setSearchMode(isSearchMode)
        with(binding.searchBar) {
            setKeyboardVisibility(isSearchMode, this)
            this.setQuery("", true)    // clear search
        }
    }


    /**
     * Set observer for ViewModel. When ViewModel was changed, adapter of recycler view was take notify.
     */
    override fun setObservers() {
        observeContactsList()
        observeUI()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }


    private fun observeUI() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    with(binding) {

                        val isSearchMode = state.isSearchMode
                        textViewAddContactTitle.setVisibleOrGone(!isSearchMode)
                        buttonAddContactSearch.setVisibleOrGone(!isSearchMode)
                        buttonMyContactsBackButton.setVisibleOrGone(!isSearchMode)
                        searchBar.setVisibleOrGone(isSearchMode)
                        buttonAddContactCancelSearch.setVisibleOrGone(isSearchMode)
                        recyclerViewContactList.setVisibleOrGone(!state.isLoadingUsers)
                        progressBarContactList.setVisibleOrGone(state.isLoadingUsers)
                        //setNoContactsDisclaimerVisible(adapterForRecycler.currentList.isEmpty())

                        showAddContactDialog(
                            contactEmail = state.contactEmail,
                            isShowAlertDialog = state.isShowAlertDialog,
                            onAcceptAlertDialog = state.onAcceptAlertDialog,
                            onDismissAlertDialog = state.onDismissAlertDialog
                        )

                    }
                }
            }
        }
    }

    private fun showAddContactDialog(
        contactEmail: String,
        isShowAlertDialog: Boolean,
        onAcceptAlertDialog: () -> Unit,
        onDismissAlertDialog: () -> Unit
    ) {
        if (isShowAlertDialog) {
            val builder = AlertDialog.Builder(requireContext())
            val title = "Увага!"
            val message = "Ви хочете додати контакт${ if (contactEmail.isEmpty()) "?" else "\n$contactEmail ?"}"
            builder.setTitle(title)
            builder.setMessage(message)
            builder.setPositiveButton("Додати") { dialog, _ ->
                onAcceptAlertDialog()
                dialog.dismiss()
            }
            builder.setNegativeButton("Відхилити") { dialog, _ ->
                onDismissAlertDialog()
                dialog.dismiss()
            }
            builder.show()
        }
    }

    private fun setNoContactsDisclaimerVisible(isVisible: Boolean) {
        with(binding) {
            textViewNoResultsTitle.setVisibleOrGone(isVisible)
            textViewNoResultsSubtitle.setVisibleOrGone(isVisible)
        }
    }


    /**
     * Init recycler view and swipe to delete.
     */
    private fun initRecyclerView() {
        with(binding.recyclerViewContactList) {
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(MarginItemDecoration(MARGINS_OF_ELEMENTS))
            adapter = adapterForRecycler
        }
    }
}