package com.dev.petmarket_android.common.adapter

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

/**
 * Generic base adapter that uses DiffUtil for efficient list updates.
 * Subclasses should implement onCreateViewHolder and onBindViewHolder.
 *
 * @param T The type of items in the list
 * @param VH The ViewHolder type
 */
abstract class DiffUtilAdapter<T, VH : RecyclerView.ViewHolder>(
    private val areItemsTheSame: (old: T, new: T) -> Boolean,
    private val areContentsTheSame: (old: T, new: T) -> Boolean
) : RecyclerView.Adapter<VH>() {

    protected val items = mutableListOf<T>()

    /**
     * Submits a new list to the adapter and calculates differences efficiently.
     * This replaces the old inefficient notifyDataSetChanged() pattern.
     */
    fun submitList(newList: List<T>) {
        val diffResult = DiffUtil.calculateDiff(
            DiffUtilCallback(items, newList, areItemsTheSame, areContentsTheSame)
        )
        items.clear()
        items.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemCount(): Int = items.size

    /**
     * Internal DiffUtil callback for this adapter.
     */
    private class DiffUtilCallback<T>(
        private val oldList: List<T>,
        private val newList: List<T>,
        private val areItemsTheSame: (old: T, new: T) -> Boolean,
        private val areContentsTheSame: (old: T, new: T) -> Boolean
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return areItemsTheSame(oldList[oldItemPosition], newList[newItemPosition])
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return areContentsTheSame(oldList[oldItemPosition], newList[newItemPosition])
        }
    }
}
