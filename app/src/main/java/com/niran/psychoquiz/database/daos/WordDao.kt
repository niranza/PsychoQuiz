package com.niran.psychoquiz.database.daos

import androidx.room.*
import com.niran.psychoquiz.database.models.Word
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {

    @Query("SELECT * FROM word_table")
    suspend fun suspendedGetAllWords(): List<Word>

    @Query("SELECT word_translation FROM word_table")
    suspend fun suspendedGetAllTranslations(): List<String>

    @Query("SELECT * FROM word_table ORDER BY word_text ASC")
    fun getAllWords(): Flow<List<Word>>

    @Query("SELECT * FROM word_table WHERE word_char = :firstLetter ORDER BY word_text ASC")
    fun getWordsByLetter(firstLetter: Char): Flow<List<Word>>

    @Query("DELETE FROM word_table")
    suspend fun deleteAllWords()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: Word)

    @Update
    suspend fun updateWord(word: Word)

    @Delete
    suspend fun deleteWord(word: Word)

}