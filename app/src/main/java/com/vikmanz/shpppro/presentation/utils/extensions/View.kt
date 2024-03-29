package com.vikmanz.shpppro.presentation.utils.extensions

import android.view.View

fun View.setVisibleOrGone(isVisible: Boolean) {
    this.visibility = if (isVisible) View.VISIBLE else View.GONE
}

fun View.setVisible() {
    this.visibility = View.VISIBLE
}

fun setMultipleVisible(vararg input: View) {
    for (view in input) {
        view.setVisible()
    }
}

fun View.setInvisible() {
    this.visibility = View.INVISIBLE
}

fun setMultipleInvisible(vararg input: View) {
    for (view in input) {
        view.setInvisible()
    }
}
fun View.setGone() {
    this.visibility = View.GONE
}

fun setMultipleGone(vararg input: View) {
    for (view in input) {
        view.setGone()
    }
}




