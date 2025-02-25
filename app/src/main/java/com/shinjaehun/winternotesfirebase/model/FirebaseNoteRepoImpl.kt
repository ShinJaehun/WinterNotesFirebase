package com.shinjaehun.winternotesfirebase.model

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.shinjaehun.winternotesfirebase.common.COLLECTION_NAME
import com.shinjaehun.winternotesfirebase.common.Result
import com.shinjaehun.winternotesfirebase.common.awaitTaskCompletable
import com.shinjaehun.winternotesfirebase.common.awaitTaskResult
import com.shinjaehun.winternotesfirebase.common.toFirebaseNote
import com.shinjaehun.winternotesfirebase.common.toNote
import com.shinjaehun.winternotesfirebase.common.toUser

private const val TAG = "NoteRepoImpl"

class FirebaseNoteRepoImpl(
    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance(),
    val remote: FirebaseFirestore = FirebaseFirestore.getInstance(),
    val storageReference: StorageReference = FirebaseStorage.getInstance().reference,
): INoteRepository {
    override suspend fun getNotes(): Result<Exception, List<Note>> {
        return try {
            val user = getActiveUser()
            val task = awaitTaskResult(
                remote.collection(COLLECTION_NAME)
                    .whereEqualTo("creator", user?.uid)
                    .get()
            )
            resultToNoteList(task)
        } catch (e: Exception) {
            Result.build { throw e }
        }
    }

    private fun resultToNoteList(result: QuerySnapshot?): Result<Exception, List<Note>> {
        val noteList = mutableListOf<Note>()
        result?.forEach { documentSnapshot ->
            noteList.add(documentSnapshot.toObject(FirebaseNote::class.java).toNote)
        }
        return Result.build { noteList }
    }

    override suspend fun getNoteById(noteId: String): Result<Exception, Note> {
        return try {
            Log.i(TAG, "what is noteId: $noteId")
            val user = getActiveUser()
            Log.i(TAG, "what is user uid: ${user?.uid}")
            val task = awaitTaskResult(
                remote.collection(COLLECTION_NAME)
                    .document(noteId + user?.uid)
                    .get()
            )

            Result.build {
                task.toObject(FirebaseNote::class.java)?.toNote ?: throw Exception()
            }
        } catch (e: Exception) {
            Result.build { throw e }
        }
    }

    override suspend fun deleteNote(note: Note): Result<Exception, Unit> = Result.build {
        awaitTaskCompletable(
            remote.collection(COLLECTION_NAME)
                .document(note.dateTime + note.creator!!.uid)
                .delete()
        )
    }

    override suspend fun insertOrUpdateNote(note: Note): Result<Exception, Unit> {
        return try {
            awaitTaskCompletable(
                remote.collection(COLLECTION_NAME)
                    .document(note.dateTime + note.creator!!.uid)
                    .set(note.toFirebaseNote)
            )
            Result.build { Unit }
        } catch (e: Exception) {
            Result.build { throw e }
        }
    }

    private fun getActiveUser(): User? {
        return firebaseAuth.currentUser?.toUser
    }
}