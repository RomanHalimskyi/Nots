package com.example.nots.Database

import android.provider.ContactsContract.CommonDataKinds.Note
import androidx.lifecycle.LiveData

class NotesRepository(private val noteDao: NoteDao) {

    val allNotes : LiveData<List<com.example.nots.Models.Note>> = noteDao.getAllNotes()

    suspend fun insert(note: com.example.nots.Models.Note){
        noteDao.insert(note)
    }

    suspend fun delete(note: com.example.nots.Models.Note){
        noteDao.delete(note)
    }

    suspend fun update(note: com.example.nots.Models.Note){
        noteDao.update(note.id, note.title, note.note)
    }
}