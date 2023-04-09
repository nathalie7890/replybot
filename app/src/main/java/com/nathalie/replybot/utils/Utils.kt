package com.nathalie.replybot.utils

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

object Utils {
    fun <T> RecyclerView.Adapter<*>.update(
        oldList: List<T>,
        newList: List<T>,
        compare: (T, T) -> Boolean
    ) {
        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return oldList.size
            }

            override fun getNewListSize(): Int {
                return newList.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return compare(oldList[oldItemPosition], newList[newItemPosition])
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition] == newList[newItemPosition]
            }
        })

        diff.dispatchUpdatesTo(this)
    }

    fun validate(vararg fields: String): Boolean {
        fields.forEach { field ->
            if (field.isEmpty()) {
                return false
            }
        }
        return true
    }
}