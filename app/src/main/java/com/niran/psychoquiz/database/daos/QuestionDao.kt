package com.niran.psychoquiz.database.daos

import androidx.room.*
import com.niran.psychoquiz.database.models.Question

@Dao
interface QuestionDao {

    @Query("SELECT * FROM question_table ORDER BY question_id ASC")
    suspend fun getAllQuestions(): List<Question>

    @Query("DELETE FROM question_table")
    suspend fun deleteAllQuestions()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertQuestion(question: Question)

    @Update
    suspend fun updateQuestion(question: Question)
}