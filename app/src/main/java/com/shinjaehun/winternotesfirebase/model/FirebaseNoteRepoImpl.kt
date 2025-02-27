package com.shinjaehun.winternotesfirebase.model

import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Tasks.await
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.shinjaehun.winternotesfirebase.common.COLLECTION_NAME
import com.shinjaehun.winternotesfirebase.common.Result
import com.shinjaehun.winternotesfirebase.common.UiState
import com.shinjaehun.winternotesfirebase.common.awaitTaskCompletable
import com.shinjaehun.winternotesfirebase.common.awaitTaskResult
import com.shinjaehun.winternotesfirebase.common.toFirebaseNote
import com.shinjaehun.winternotesfirebase.common.toNote
import com.shinjaehun.winternotesfirebase.common.toUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

private const val TAG = "FirebaseNoteRepoImpl"

class FirebaseNoteRepoImpl(
    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance(),
    val remote: FirebaseFirestore = FirebaseFirestore.getInstance(),
    val storageReference: StorageReference = FirebaseStorage.getInstance().reference,
): INoteRepository {
    override suspend fun getNotes(): Result<Exception, List<Note>> {
        return try {
            val user = getActiveUser()
            Log.i(TAG, "user: $user")
            val task = awaitTaskResult(
                remote.collection(COLLECTION_NAME)
                    .whereEqualTo("creator", user!!.uid)
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
            Log.i(TAG, "documentSnapshot: $documentSnapshot")
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

    override suspend fun insertOrUpdateNote(note: Note, imageUri: Uri?): Result<Exception, Unit> {
        val user = getActiveUser()

        return try {
            // 기본적인 처리 방법이지...
            // uploadImageFile()을 따로 처리하려면 이 방법 권장
//            val updateNote = note.copy(creator = user)
//            Log.i(TAG, "note to insert: $updateNote")
//            awaitTaskCompletable(
//                remote.collection(COLLECTION_NAME)
//                    .document(updateNote.dateTime + updateNote.creator!!.uid)
//                    .set(updateNote.toFirebaseNote)
//            )
//            Result.build { Unit }

//            if (imageUri == null) {
//                val updateNote = note.copy(creator = user)
//                Log.i(TAG, "note to insert: $updateNote")
//                awaitTaskCompletable(
//                    remote.collection(COLLECTION_NAME)
//                        .document(updateNote.dateTime + updateNote.creator!!.uid)
//                        .set(updateNote.toFirebaseNote)
//                )
//                Result.build { Unit }
//            } else {
//                // 여기서 이미지 처리하는 게 맞다고 생각했는데
//                // 이미지 용량이 커서 그런가?
//                // 이미지 업로드 전에 noteList 갱신이 먼저 이루어짐.... 확실히 안되는 거 맞음!!
//                // 지금 생각해보면 task를 중간에 끊어서 await()를 넣으면 가능할지도 모르겠다...
//                awaitTaskCompletable(
//                    storageReference.child("winternotesfirebase_images/${imageUri.lastPathSegment}")
//                        .putFile(imageUri)
//                        .addOnSuccessListener { task ->
//                            task.storage.downloadUrl.addOnSuccessListener {
//                                Log.i(TAG, "download url: ${it.toString()}")
//                                val updateNote = note.copy(creator = user, imagePath = it.toString())
//                                Log.i(TAG, "note to insert: $updateNote")
//                                remote.collection(COLLECTION_NAME)
//                                    .document(updateNote.dateTime + updateNote.creator!!.uid)
//                                    .set(updateNote.toFirebaseNote)
//                            }
//                        }
//                )
//
//                Result.build { Unit }
//            }

            if (imageUri == null) {
                val updateNote = note.copy(creator = user)
                Log.i(TAG, "note to insert: $updateNote")
                awaitTaskCompletable(
                    remote.collection(COLLECTION_NAME)
                        .document(updateNote.dateTime + updateNote.creator!!.uid)
                        .set(updateNote.toFirebaseNote)
                )
                Result.build { Unit }
            } else {
//                var uri: Uri? = null
//                coroutineScope {
//                    async {
//                        uri = storageReference.child("winternotesfirebase_images/${imageUri.lastPathSegment}")
//                            .putFile(imageUri)
//                            .await()
//                            .storage
//                            .downloadUrl
//                            .await()
//                    }
//                }.join() //나름 이것 때문에 된 걸로 알고 있었는데...
//                Log.i(TAG, "uri of this: $uri")
//                val updateNote = note.copy(creator = user, imagePath = uri.toString())
//                Log.i(TAG, "note to insert: $updateNote")
//                remote.collection(COLLECTION_NAME)
//                    .document(updateNote.dateTime + updateNote.creator!!.uid)
//                    .set(updateNote.toFirebaseNote)

                // 근데 이게 될 줄은 몰랐는데... await() 이게 끝까지 기다려주는 역할을 하고 있음!
                val uri = storageReference.child("winternotesfirebase_images/${imageUri.lastPathSegment}")
                    .putFile(imageUri)
                    .await()
                    .storage
                    .downloadUrl
                    .await()

                Log.i(TAG, "uri of this: $uri")
                val updateNote = note.copy(creator = user, imagePath = uri.toString())
                Log.i(TAG, "note to insert: $updateNote")
                remote.collection(COLLECTION_NAME)
                    .document(updateNote.dateTime + updateNote.creator!!.uid)
                    .set(updateNote.toFirebaseNote)

                Result.build { Unit }
            }
        } catch (e: Exception) {
            Result.build { throw e }
        }
    }

//    override suspend fun uploadImageFile(
//        // 이런 방법으로 이미지 처리하는 것도 괜춘?
//        fileUri: Uri,
//        onResult: (UiState<Uri>) -> Unit
//    ) {
//        try {
//            val uri: Uri = withContext(Dispatchers.IO) {
//                storageReference.child("winternotesfirebase_images/${fileUri.lastPathSegment}")
//                    .putFile(fileUri)
//                    .await()
//                    .storage
//                    .downloadUrl
//                    .await()
//            }
//            onResult.invoke(UiState.Success(uri))
//        } catch (e: FirebaseException) {
//            onResult.invoke(UiState.Failure(e.message))
//        } catch (e: Exception) {
//            onResult.invoke(UiState.Failure(e.message))
//        }
//    }

    private fun getActiveUser(): User? {
        return firebaseAuth.currentUser?.toUser
    }
}