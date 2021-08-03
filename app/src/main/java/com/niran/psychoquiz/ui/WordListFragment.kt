package com.niran.psychoquiz.ui

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.niran.psychoquiz.DISPLAY_ALL_WORDS_LIST
import com.niran.psychoquiz.PsychoQuizApplication
import com.niran.psychoquiz.R
import com.niran.psychoquiz.database.models.Word
import com.niran.psychoquiz.database.models.Word.Types
import com.niran.psychoquiz.databinding.FragmentWordListBinding
import com.niran.psychoquiz.utils.adapters.WordAdapter
import com.niran.psychoquiz.utils.filterWordListBySearchQuery
import com.niran.psychoquiz.utils.sortByType
import com.niran.psychoquiz.viewmodels.WordViewModel
import com.niran.psychoquiz.viewmodels.WordViewModelFactory


class WordListFragment : Fragment() {

    private var _binding: FragmentWordListBinding? = null
    private val binding get() = _binding!!

    private lateinit var wordAdapter: WordAdapter

    private var firstLetterChar: Char = DISPLAY_ALL_WORDS_LIST
    private val displayAllWords get() = firstLetterChar == DISPLAY_ALL_WORDS_LIST

    private val viewModel: WordViewModel by viewModels {
        WordViewModelFactory((activity?.application as PsychoQuizApplication).wordRepository)
    }

    private val navArgs: WordListFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentWordListBinding.inflate(inflater)

        firstLetterChar = navArgs.letter[0]

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        wordAdapter = WordAdapter(object : WordAdapter.WordClickHandler {
            override fun onCloseClicked(word: Word) {
                viewModel.customUpdateWord(word, Word.Types.UNKNOWN)
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
        })

        binding.apply {
            wordRv.apply {
                adapter = wordAdapter
                setHasFixedSize(true)
            }
        }

        if (displayAllWords)
            viewModel.getAllWords().observe(viewLifecycleOwner) { wordList ->
                uiSafeLoadList(wordList) { wordAdapter.submitList(it.sortByType()) }
            }
        else viewModel.getWordsByLetter(firstLetterChar)
            .observe(viewLifecycleOwner) { wordList ->
                uiSafeLoadList(wordList) { wordAdapter.submitList(it.sortByType()) }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)

        (menu.findItem(R.id.search).actionView as SearchView).apply {
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

    private fun searchWord(query: String?) {
        if (displayAllWords)
            viewModel.getAllWords().observe(viewLifecycleOwner) { wordList ->
                uiSafeLoadList(wordList) {
                    val searchedList = it.sortByType().filterWordListBySearchQuery(query)
                    wordAdapter.submitList(uiLoadSearchedList(searchedList))
                }
            }
        else viewModel.getWordsByLetter(firstLetterChar)
            .observe(viewLifecycleOwner) { wordList ->
                uiSafeLoadList(wordList) {
                    val searchedList = it.sortByType().filterWordListBySearchQuery(query)
                    wordAdapter.submitList(uiLoadSearchedList(searchedList))
                }
            }
    }

    private fun uiLoadSearchedList(searchedWordList: List<Word>): List<Word> =
        with(binding) {

            if (searchedWordList.isEmpty()) noResultTv.visibility = View.VISIBLE
            else noResultTv.visibility = View.GONE

            searchedWordList
        }


    private fun uiSafeLoadList(
        wordList: List<Word>?,
        actionSubmitList: (wordList: List<Word>) -> Unit
    ) =
        wordList?.let {
            binding.apply {
                if (wordList.isEmpty()) loadingWordsPb.visibility = View.VISIBLE
                else {
                    loadingWordsPb.visibility = View.GONE
                    actionSubmitList(it)
                }
            }
        }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



