package com.vikmanz.shpppro.presentation.screens.main.main_fragment.my_contacts_list.adapter.decorator

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Create custom paddings of elements in recycler view.
 */
class MarginItemDecoration(private val spaceSize: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        with(outRect) {
         //  if (parent.getChildAdapterPosition(view) == 0) top = spaceSize    // this call bug when deleting first element in recycler view.
            left = spaceSize
            right = spaceSize
            bottom = spaceSize
        }
    }
}