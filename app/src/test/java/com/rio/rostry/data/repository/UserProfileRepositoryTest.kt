package com.rio.rostry.data.repository

import com.rio.rostry.data.models.UserProfile
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference

/**
 * Unit tests for the UserProfileRepository.
 */
class UserProfileRepositoryTest {

    private lateinit var repository: UserProfileRepository
    private lateinit var mockFirestore: FirebaseFirestore
    private lateinit var mockCollection: CollectionReference

    @Before
    fun setup() {
        // Mock Firebase services
        mockFirestore = mock(FirebaseFirestore::class.java)
        mockCollection = mock(CollectionReference::class.java)
        
        // Set up the mock hierarchy
        `when`(mockFirestore.collection("users")).thenReturn(mockCollection)
        
        // Initialize repository with mocks
        repository = UserProfileRepository(mockFirestore)
    }

    @Test
    fun `saveProfile should return success when profile is saved successfully`() = runBlocking {
        // Arrange
        val profile = UserProfile(uid = "test-uid", name = "Test User")
        val mockDocument: DocumentReference = mock()
        val mockTask: Task<Void> = mock()
        
        `when`(mockCollection.document("test-uid")).thenReturn(mockDocument)
        `when`(mockDocument.set(profile)).thenReturn(mockTask)
        `when`(mockTask.await()).thenReturn(null) // Success

        // Act
        val result = repository.saveProfile(profile)

        // Assert
        assertTrue(result.isSuccess)
    }

    @Test
    fun `saveProfile should return failure when exception occurs`() = runBlocking {
        // Arrange
        val profile = UserProfile(uid = "test-uid", name = "Test User")
        val mockDocument: DocumentReference = mock()
        val mockTask: Task<Void> = mock()
        
        `when`(mockCollection.document("test-uid")).thenReturn(mockDocument)
        `when`(mockDocument.set(profile)).thenReturn(mockTask)
        `when`(mockTask.await()).thenThrow(RuntimeException("Database error"))

        // Act
        val result = repository.saveProfile(profile)

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Database error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getProfile should return success with profile when profile exists`() = runBlocking {
        // Arrange
        val profile = UserProfile(uid = "test-uid", name = "Test User")
        val mockDocument: DocumentReference = mock()
        val mockTask: Task<DocumentSnapshot> = mock()
        val mockDocumentSnapshot: DocumentSnapshot = mock()
        
        `when`(mockCollection.document("test-uid")).thenReturn(mockDocument)
        `when`(mockDocument.get()).thenReturn(mockTask)
        `when`(mockTask.await()).thenReturn(mockDocumentSnapshot)
        `when`(mockDocumentSnapshot.exists()).thenReturn(true)
        `when`(mockDocumentSnapshot.toObject(UserProfile::class.java)).thenReturn(profile)

        // Act
        val result = repository.getProfile("test-uid")

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(profile, result.getOrNull())
    }

    @Test
    fun `getProfile should return success with null when profile does not exist`() = runBlocking {
        // Arrange
        val uid = "test-uid"
        val mockDocument: DocumentReference = mock()
        val mockTask: Task<DocumentSnapshot> = mock()
        val mockDocumentSnapshot: DocumentSnapshot = mock()
        
        `when`(mockCollection.document(uid)).thenReturn(mockDocument)
        `when`(mockDocument.get()).thenReturn(mockTask)
        `when`(mockTask.await()).thenReturn(mockDocumentSnapshot)
        `when`(mockDocumentSnapshot.exists()).thenReturn(false)

        // Act
        val result = repository.getProfile(uid)

        // Assert
        assertTrue(result.isSuccess)
        assertNull(result.getOrNull())
    }

    @Test
    fun `getProfile should return failure when exception occurs`() = runBlocking {
        // Arrange
        val uid = "test-uid"
        val mockDocument: DocumentReference = mock()
        val mockTask: Task<DocumentSnapshot> = mock()
        
        `when`(mockCollection.document(uid)).thenReturn(mockDocument)
        `when`(mockDocument.get()).thenReturn(mockTask)
        `when`(mockTask.await()).thenThrow(RuntimeException("Database error"))

        // Act
        val result = repository.getProfile(uid)

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Database error", result.exceptionOrNull()?.message)
    }
}