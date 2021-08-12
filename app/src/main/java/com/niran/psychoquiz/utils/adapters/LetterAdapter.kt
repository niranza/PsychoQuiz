package com.niran.psychoquiz.utils.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.niran.psychoquiz.database.models.settings.WordFirstLetterSetting
import com.niran.psychoquiz.databinding.LetterItemBinding

class LetterAdapter(private val letterClickHandler: LetterClickHandler) :
    RecyclerView.Adapter<LetterAdapter.LetterViewHolder>() {

    private val alphabets = WordFirstLetterSetting.Constant.keyList

    class LetterViewHolder private constructor(
        private val binding: LetterItemBinding,
        private val letterClickHandler: LetterClickHandler
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(letter: Char) {
            binding.btnLetter.apply {
                text = letter.toString()
                setOnClickListener { letterClickHandler.onLetterClicked(letter) }
            }
        }

        companion object {
            fun create(parent: ViewGroup, letterClickHandler: LetterClickHandler)
                    : LetterViewHolder {
                val binding = LetterItemBinding.inflate(LayoutInflater.from(parent.context))
                return LetterViewHolder(binding, letterClickHandler)
            }
        }
    }

    interface LetterClickHandler {
        fun onLetterClicked(letter: Char)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LetterViewHolder {
        return LetterViewHolder.create(parent, letterClickHandler)
    }

    override fun onBindViewHolder(holder: LetterViewHolder, position: Int) {
        holder.bind(alphabets[position])
    }

    override fun getItemCount(): Int {
        return alphabets.count()
    }
}