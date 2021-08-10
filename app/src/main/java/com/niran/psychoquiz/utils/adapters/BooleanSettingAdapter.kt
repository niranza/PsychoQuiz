package com.niran.psychoquiz.utils.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.niran.psychoquiz.database.models.settings.superclasses.BooleanSetting
import com.niran.psychoquiz.databinding.BooleanSettingItemBinding

class BooleanSettingAdapter(
    private val booleanSettingClickHandler: BooleanSettingClickHandler
) : ListAdapter<BooleanSetting, BooleanSettingAdapter.BooleanSettingViewHolder>(
    BooleanSettingCallBack
) {

    class BooleanSettingViewHolder private constructor(
        private val binding: BooleanSettingItemBinding,
        private val booleanSettingClickHandler: BooleanSettingClickHandler
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(booleanSetting: BooleanSetting) {
            binding.apply {
                keyTv.text = booleanSetting.settingName
                valueCb.apply {
                    isChecked = booleanSetting.settingValue
                    setOnClickListener {
                        booleanSettingClickHandler.onCheckBoxClick(booleanSetting)
                    }
                }
            }
        }

        companion object {
            fun create(
                parent: ViewGroup,
                booleanSettingClickHandler: BooleanSettingClickHandler
            ): BooleanSettingViewHolder {
                val binding = BooleanSettingItemBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                return BooleanSettingViewHolder(binding, booleanSettingClickHandler)
            }
        }
    }

    interface BooleanSettingClickHandler {
        fun onCheckBoxClick(booleanSetting: BooleanSetting)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BooleanSettingViewHolder {
        return BooleanSettingViewHolder.create(parent, booleanSettingClickHandler)
    }

    override fun onBindViewHolder(holder: BooleanSettingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    object BooleanSettingCallBack : DiffUtil.ItemCallback<BooleanSetting>() {
        override fun areItemsTheSame(oldItem: BooleanSetting, newItem: BooleanSetting): Boolean {
            return newItem.settingId == oldItem.settingId
        }

        override fun areContentsTheSame(oldItem: BooleanSetting, newItem: BooleanSetting): Boolean {
            return newItem.settingValue == oldItem.settingValue
        }
    }
}