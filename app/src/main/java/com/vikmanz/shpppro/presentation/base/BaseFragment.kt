package com.vikmanz.shpppro.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.vikmanz.shpppro.utils.extensions.observeNonNull
import com.vikmanz.shpppro.presentation.navigator.NavigationCommand

abstract class BaseFragment<VBinding : ViewBinding, VM : BaseViewModel>(
    private val inflaterMethod: (LayoutInflater, ViewGroup?, Boolean) -> VBinding
) : Fragment() {

    protected abstract val viewModel: VM

    private var _binding: VBinding? = null
    protected val binding get() = requireNotNull(_binding)

    protected open fun setListeners() {}
    protected open fun setObservers() {}
    protected open fun onReady() {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflaterMethod.invoke(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        setObservers()
        observeNavigation()
        onReady()
    }

    private fun observeNavigation() {
        viewModel.navigation.observeNonNull(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { navigationCommand ->
                handleNavigation(navigationCommand)
            }
        }
    }

    private fun handleNavigation(navCommand: NavigationCommand) {
        when (navCommand) {
            is NavigationCommand.ToDirection -> {   // nav to other screen (fragment)
                findNavController().navigate(navCommand.directions)
            }
            is NavigationCommand.ToActivity -> {    // nav to other activity
                findNavController().navigate(navCommand.directions)
                requireActivity().finish()
            }
            is NavigationCommand.Back -> {          // navigate up
                findNavController().navigateUp()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}