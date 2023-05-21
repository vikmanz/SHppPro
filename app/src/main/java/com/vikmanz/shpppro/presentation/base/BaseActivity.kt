package com.vikmanz.shpppro.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.viewbinding.ViewBinding

/**
 * Base activity with binding to extend it in activities.
 */
abstract class BaseActivity<VBinding : ViewBinding>(
    private val inflaterMethod: (LayoutInflater) -> VBinding
) :
    AppCompatActivity() {

    private var _binding: VBinding? = null
    val binding get() = requireNotNull(_binding)

    abstract val navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = inflaterMethod.invoke(layoutInflater).also{setContentView(it.root)}
        initNavController()
        setListeners()
        setObservers()
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    protected open fun setListeners() { }

    protected open fun setObservers() { }

    abstract fun initNavController()
}