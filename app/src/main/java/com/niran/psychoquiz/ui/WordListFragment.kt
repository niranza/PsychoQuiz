package com.niran.psychoquiz.ui

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.niran.psychoquiz.PsychoQuizApplication
import com.niran.psychoquiz.R
import com.niran.psychoquiz.database.models.Word
import com.niran.psychoquiz.database.models.Word.Types
import com.niran.psychoquiz.databinding.FragmentWordListBinding
import com.niran.psychoquiz.utils.adapters.WordAdapter
import com.niran.psychoquiz.utils.filterByWordChar
import com.niran.psychoquiz.utils.filterWordListBySearchQuery
import com.niran.psychoquiz.utils.sortByType
import com.niran.psychoquiz.viewmodels.WordViewModel
import com.niran.psychoquiz.viewmodels.WordViewModelFactory
import kotlinx.coroutines.delay


class WordListFragment : Fragment() {

    private var _binding: FragmentWordListBinding? = null
    private val binding get() = _binding!!

    private lateinit var wordAdapter: WordAdapter

    private var firstLetterChar: Char = DISPLAY_ALL_WORDS_LIST
    private val displayAllWords get() = firstLetterChar == DISPLAY_ALL_WORDS_LIST

    private var showTranslation = true

    private val viewModel: WordViewModel by viewModels {
        WordViewModelFactory((activity?.application as PsychoQuizApplication).wordRepository)
    }

    private val navArgs: WordListFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentWordListBinding.inflate(inflater)

        firstLetterChar = navArgs.letter[0]

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        wordAdapter = WordAdapter(object : WordAdapter.WordClickHandler {
            override fun onCloseClicked(word: Word) {
                viewModel.customUpdateWord(word, Types.UNKNOWN)
            }

            override fun onStarClicked(word: Word) {
                viewModel.customUpdateWord(word, Types.FAVORITE)
            }

            override fun onCheckClicked(word: Word) {
                viewModel.customUpdateWord(word, Types.KNOWN)
            }

            override fun onItemViewClicked(word: Word) {
                if (word.wordType == Types.NEUTRAL.ordinal) return
                viewModel.customUpdateWord(word, Types.NEUTRAL)
            }

            override val showTranslation: Boolean get() = this@WordListFragment.showTranslation
        })

        binding.apply {
            rvWord.apply {
                adapter = wordAdapter
                setHasFixedSize(true)
            }
        }

        if (displayAllWords)
            viewModel.getAllWordsAsLiveData().observe(viewLifecycleOwner) { wordList ->
                uiSafeLoadList(wordList) { wordAdapter.submitList(it.sortByType()) }
            }
        else viewModel.getWordsByLetterAsLiveData().observe(viewLifecycleOwner) { wordList ->
            uiSafeLoadList(wordList) {
                wordAdapter.submitList(it.filterByWordChar(firstLetterChar).sortByType())
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_word_list_menu, menu)

        (menu.findItem(R.id.item_search).actionView as SearchView).apply {
            queryHint = getString(R.string.search_word_hint)
            isSubmitButtonEnabled = true

            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    searchWord(query)
                    this@apply.clearFocus()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    searchWord(newText)
                    return true
                }
            })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.item_show_translation -> {
            lifecycleScope.launchWhenCreated {
                item.apply {
                    if (showTranslation) {
                        showTranslation = false
                        wordAdapter.notifyDataSetChanged()
                        delay(DELAY_TIME_IN_MILLIS)
                        title = getString(R.string.show_translation)
                    } else {
                        showTranslation = true
                        wordAdapter.notifyDataSetChanged()
                        delay(DELAY_TIME_IN_MILLIS)
                        title = getString(R.string.hide_translation)
                    }
                }
            }
            true
        }
        android.R.id.home -> {
            view?.findNavController()?.navigateUp()
            true
        }
        else -> true
    }

    private fun searchWord(query: String?) {
        if (displayAllWords)
            viewModel.getAllWordsAsLiveData().observe(viewLifecycleOwner) { wordList ->
                uiSafeLoadList(wordList) {
                    val searchedList = it.filterWordListBySearchQuery(query).sortByType()
                    wordAdapter.submitList(uiLoadSearchedList(searchedList))
                }
            }
        else viewModel.getWordsByLetterAsLiveData().observe(viewLifecycleOwner) { wordList ->
            uiSafeLoadList(wordList) {
                val searchedList = it.filterWordListBySearchQuery(query)
                    .filterByWordChar(firstLetterChar).sortByType()
                wordAdapter.submitList(uiLoadSearchedList(searchedList))
            }
        }
    }

    private fun uiLoadSearchedList(searchedWordList: List<Word>): List<Word> = with(binding) {

        if (searchedWordList.isEmpty()) tvNoResult.visibility = View.VISIBLE
        else tvNoResult.visibility = View.GONE

        searchedWordList
    }


    private fun uiSafeLoadList(
        wordList: List<Word>?,
        actionSubmitList: (wordList: List<Word>) -> Unit
    ) =
        wordList?.let {
            binding.apply {
                if (it.isEmpty()) pbLoadingWords.visibility = View.VISIBLE
                else {
                    pbLoadingWords.visibility = View.GONE
                    actionSubmitList(it)
                }
            }
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val DISPLAY_ALL_WORDS_LIST = '0'
        private const val DELAY_TIME_IN_MILLIS = 100L
    }
}



