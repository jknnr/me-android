package io.forus.me.android.presentation.view.screens.records.newrecord.adapters

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import io.forus.me.android.domain.models.records.RecordType
import io.forus.me.android.domain.models.records.Validator
import io.forus.me.android.presentation.view.screens.records.newrecord.viewholders.RecordTypeVH
import io.forus.me.android.presentation.view.screens.records.newrecord.viewholders.RecordValidatorVH

class RecordValidatorAdapter(private val clickListener: ((Validator) -> Unit)?): RecyclerView.Adapter<RecordValidatorVH>() {

    private var lastSelectedPosition: Int = -1

    var items: List<Validator> = emptyList()
        set(value) {
            val old = field
            field = value
            DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize() = old.size
                override fun getNewListSize() = field.size
                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) = old[oldItemPosition] == field[newItemPosition]
                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) = old[oldItemPosition] == field[newItemPosition]
            }).dispatchUpdatesTo(this)
            notifyDataSetChanged()
        }

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RecordValidatorVH(parent) { validator: Validator, position: Int ->
        lastSelectedPosition = position
        //notifyDataSetChanged()
        clickListener?.invoke(validator)
    }

    override fun onBindViewHolder(holder: RecordValidatorVH, position: Int) {
        holder.render(items[position], lastSelectedPosition)
    }
    override fun getItemCount() = items.size
    override fun getItemId(position: Int) = position.toLong()
}