package com.github.rubensousa.loadmoreadapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

abstract class LoadMoreAdapter : RecyclerView.Adapter<LoadMoreAdapter.ViewHolder>,
    ScrollListener.OnEventListener {

    private var loadMoreListener: OnLoadMoreListener? = null
    private var scrollListener: ScrollListener? = null

    @LayoutRes
    private var progressLayout: Int = 0
    private var loading: Boolean = false
    private var loadingEnabled: Boolean = true
    private var visibleThreshold: Int = 5
    private var inverted: Boolean = false

    @JvmOverloads
    constructor(
        loadMoreListener: OnLoadMoreListener,
        scrollListener: ScrollListener? = null,
    ) : super() {
        this.loadMoreListener = loadMoreListener
        this.scrollListener = scrollListener
        this.progressLayout = R.layout.loadmore_progress
    }

    open fun restoreState(restoreState: Bundle?) {
        if (restoreState != null) {
            loadingEnabled =
                restoreState.getBoolean(STATE_LOADING_ENABLED)
            loading =
                restoreState.getBoolean(STATE_LOADING)
        }
    }

    open fun saveState(outState: Bundle) {
        outState.putBoolean(
            STATE_LOADING,
            loading
        )
        outState.putBoolean(
            STATE_LOADING_ENABLED,
            loadingEnabled
        )
    }

    fun setup(recyclerView: RecyclerView) {
        recyclerView.addOnScrollListener(
            ScrollListener(inverted, visibleThreshold, this, this, recyclerView.layoutManager)
        )
    }

    fun getVisibleThreshold(): Int {
        return visibleThreshold
    }

    fun isInversed(): Boolean {
        return inverted
    }

    fun setOnLoadMoreListener(listener: OnLoadMoreListener) {
        loadMoreListener = listener
    }

    fun isLoading(): Boolean {
        return loading
    }

    fun setLoadingState(loading: Boolean) {

        val items = getItems().toMutableList()

        // If we were loading and want to stop doing it,
        // we must remove the null items
        if (this.loading && !loading) {
            if (inverted) {
                if (items[0] == null) {
                    items.drop(0)
                    notifyItemRemoved(0)
                }
            } else {
                if (items[items.size - 1] == null) {
                    items.drop(items.size - 1)
                    notifyItemRemoved(items.size)
                }
            }
            this.loading = false
        } else if (!this.loading && loading) { // If we're not already loading but want to
            if (inverted) {
                items.add(0, null)
                notifyItemInserted(0)
            } else {
                items.add(null)
                notifyItemInserted(items.size - 1)
            }
            this.loading = true
            loadMoreListener?.onLoadMore(items.size - 1)
        } else {
            this.loading = loading
        }
    }

    fun isLoadingEnabled(): Boolean {
        return loadingEnabled
    }

    fun setLoadingEnable(enable: Boolean) {
        loadingEnabled = enable
    }

    override fun onScrollForMore() {
        if (!loading && loadingEnabled) {
            setLoadingState(true)
        }
    }

    abstract fun getItems(): List<*>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == VIEW_PROGRESS) {
            ViewHolder(
                LayoutInflater.from(parent.context).inflate(progressLayout, parent, false)
            )
        } else onCreateNormalViewHolder(parent, viewType)
    }

    abstract fun onCreateNormalViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder

    override fun getItemViewType(position: Int): Int {
        val items = getItems()
        return if (items.isNotEmpty()) VIEW_NORMAL else VIEW_PROGRESS
    }

    interface OnLoadMoreListener {
        fun onLoadMore(offset: Int)
    }

    open class ViewHolder(itemView: View) : RecyclerView.ViewHolder(
        itemView
    )

    companion object {
        private const val STATE_LOADING = "loading"
        private const val STATE_LOADING_ENABLED = "loading_enabled"
        const val VIEW_NORMAL = 0
        const val VIEW_PROGRESS = 1
    }

}