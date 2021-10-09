package com.niran.psychoquiz.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.niran.psychoquiz.R
import com.niran.psychoquiz.database.models.Word
import com.niran.psychoquiz.databinding.WordItemBinding
import com.niran.psychoquiz.utils.UiUtil

class WordAdapter(private val wordClickHandler: WordClickHandler) :
    ListAdapter<Word, WordAdapter.WordViewHolder>(WordCallback) {

    class WordViewHolder private constructor(
        private val binding: WordItemBinding,
        private val wordClickHandler: WordClickHandler
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(word: Word) {
            binding.apply {

                tvWordText.text = word.wordText
                tvWordTranslation.text = word.wordTranslation

                tvWordTranslation.isVisible = wordClickHandler.showTranslation

                btnStar.setOnClickListener {
                    if (isPositionValid()) wordClickHandler.onStarClicked(word)
                }

                btnCheck.setOnClickListener {
                    if (isPositionValid()) wordClickHandler.onCheckClicked(word)
                }

                btnClose.setOnClickListener {
                    if (isPositionValid()) wordClickHandler.onCloseClicked(word)
                }

                itemView.setOnClickListener {
                    if (isPositionValid()) wordClickHandler.onItemViewClicked(word)
                }

                when (word.wordType) {
                    Word.Types.FAVORITE.ordinal ->
                        UiUtil.setViewsBackgroundColor(R.attr.wordFavoriteBgColor, *backgrounds())

                    Word.Types.UNKNOWN.ordinal ->
                        UiUtil.setViewsBackgroundColor(R.attr.wordUnknownBgColor, *backgrounds())

                    Word.Types.KNOWN.ordinal ->
                        UiUtil.setViewsBackgroundColor(R.attr.wordKnownBgColor, *backgrounds())

                    else ->
                        UiUtil.setViewsBackgroundColor(R.attr.wordNeutralBgColor, *backgrounds())
                }

            }
        }

        private fun isPositionValid() = adapterPosition != RecyclerView.NO_POSITION

        private fun backgrounds() = with(binding) { arrayOf(btnCheck, btnStar, btnClose, itemView) }

        companion object {
            fun create(parent: ViewGroup, wordClickHandler: WordClickHandler): WordViewHolder {
                val binding = WordItemBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                return WordViewHolder(binding, wordClickHandler)
            }
        }
    }

    interface WordClickHandler {
        fun onItemViewClicked(word: Word)
        fun onCloseClicked(word: Word)
        fun onStarClicked(word: Word)
        fun onCheckClicked(word: Word)
        val showTranslation: Boolean
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        return WordViewHolder.create(parent, wordClickHandler)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    object WordCallback : DiffUtil.ItemCallback<Word>() {

        override fun areItemsTheSame(oldItem: Word, newItem: Word): Boolean {
            return oldItem.wordId == newItem.wordId
        }

        override fun areContentsTheSame(oldItem: Word, newItem: Word): Boolean {
            return newItem.hashCode() == oldItem.hashCode()
        }
    }
}