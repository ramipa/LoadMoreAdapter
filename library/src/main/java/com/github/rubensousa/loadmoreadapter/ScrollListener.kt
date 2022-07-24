package com.github.rubensousa.loadmoreadapter

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class ScrollListener(
    private var inverted: Boolean,
    private var visibleThreshold: Int,
    private var loadMoreAdapter: LoadMoreAdapter,
    private var eventListener: OnEventListener?,
    private var layoutManager: RecyclerView.LayoutManager?
) : RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (layoutManager == null) {
            return
        }
        var lastVisiblePosition = 0
        val totalItems = loadMoreAdapter.itemCount
        if (totalItems == 0 || dx == 0 && dy == 0) {
            return
        }

        if (layoutManager is StaggeredGridLayoutManager) {
            val itemPositions =
                if (inverted) (layoutManager as StaggeredGridLayoutManager).findFirstVisibleItemPositions(
                    null
                ) else (layoutManager as StaggeredGridLayoutManager).findLastCompletelyVisibleItemPositions(
                    null
                )
            lastVisiblePosition = 0
            for (i in itemPositions.indices) {
                if (i == 0) {
                    lastVisiblePosition = itemPositions[i]
                } else if (itemPositions[i] > lastVisiblePosition) {
                    lastVisiblePosition = itemPositions[i]
                }
            }
        } else if (layoutManager is LinearLayoutManager) {
            lastVisiblePosition =
                if (inverted) (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition() else (layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
        }
        if (inverted && lastVisiblePosition == visibleThreshold) {
            eventListener?.onScrollForMore()
        } else if (totalItems <= lastVisiblePosition + visibleThreshold) {
            eventListener?.onScrollForMore()
        }
    }

    interface OnEventListener {
        fun onScrollForMore()
    }
}