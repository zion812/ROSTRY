package com.rio.rostry.repository

import com.rio.rostry.data.models.Fowl
import com.rio.rostry.data.models.HealthRecord
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot

class FowlRepositoryTest {

    private lateinit var repository: FowlRepository
    private lateinit var mockFirestore: FirebaseFirestore
    private lateinit var mockFowlCollection: CollectionReference

    @Before
    fun setup() {
        // Mock Firebase services
        mockFirestore = mock(FirebaseFirestore::class.java)
        mockFowlCollection = mock(CollectionReference::class.java)
        
        // Set up the mock hierarchy
        `when`(mockFirestore.collection("fowls")).thenReturn(mockFowlCollection)
        
        // Initialize repository with mocks
        repository = FowlRepository(mockFirestore)
    }

    @Test
    fun `addFowl should return success with fowl ID`() = runBlocking {
        // Arrange
        val fowl = Fowl(
            ownerUserId = "test-user-id",
            name = "Test Chicken",
            breed = "Rhode Island Red",
            birthDate = System.currentTimeMillis(),
            status = "growing"
        )
        
        val mockDocument: DocumentReference = mock()
        val mockTask: Task<Void> = mock()
        val documentId = "test-document-id"
        
        `when`(mockFowlCollection.document()).thenReturn(mockDocument)
        `when`(mockDocument.id).thenReturn(documentId)
        `when`(mockDocument.set(any<Fowl>())).thenReturn(mockTask)
        `when`(mockTask.await()).thenReturn(null)

        // Act
        val result = repository.addFowl(fowl)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(documentId, result.getOrNull())
    }

    @Test
    fun `getFowlsForUser should return fowls for user`() = runBlocking {
        // Arrange
        val ownerId = "test-user-id"
        val fowls = listOf(
            Fowl(
                fowlId = "1",
                ownerUserId = ownerId,
                name = "Henrietta",
                breed = "Rhode Island Red"
            )
        )
        
        val mockQuery: Query = mock()
        val mockTask: Task<QuerySnapshot> = mock()
        val mockQuerySnapshot: QuerySnapshot = mock()
        val mockDocumentSnapshot: DocumentSnapshot = mock()
        
        `when`(mockFowlCollection.whereEqualTo("ownerUserId", ownerId)).thenReturn(mockQuery)
        `when`(mockQuery.get()).thenReturn(mockTask)
        `when`(mockTask.await()).thenReturn(mockQuerySnapshot)
        `when`(mockQuerySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))
        `when`(mockDocumentSnapshot.toObject(Fowl::class.java)).thenReturn(fowls[0])

        // Act
        val result = repository.getFowlsForUser(ownerId)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(fowls, result.getOrNull())
    }

    @Test
    fun `addHealthRecord should add record to fowl`() = runBlocking {
        // Arrange
        val fowlId = "test-fowl-id"
        val ownerId = "test-user-id"
        val healthRecord = HealthRecord(
            type = "vaccination",
            date = System.currentTimeMillis(),
            notes = "Vaccinated for avian flu"
        )
        
        val fowl = Fowl(
            fowlId = fowlId,
            ownerUserId = ownerId,
            name = "Test Chicken",
            breed = "Rhode Island Red",
            healthRecords = listOf(healthRecord)
        )
        
        val mockDocument: DocumentReference = mock()
        val mockTask: Task<Void> = mock()
        val mockGetTask: Task<DocumentSnapshot> = mock()
        val mockDocumentSnapshot: DocumentSnapshot = mock()
        
        `when`(mockFowlCollection.document(fowlId)).thenReturn(mockDocument)
        `when`(mockDocument.get()).thenReturn(mockGetTask)
        `when`(mockGetTask.await()).thenReturn(mockDocumentSnapshot)
        `when`(mockDocumentSnapshot.exists()).thenReturn(true)
        `when`(mockDocumentSnapshot.toObject(Fowl::class.java)).thenReturn(fowl)
        `when`(mockDocument.set(any<Fowl>())).thenReturn(mockTask)
        `when`(mockTask.await()).thenReturn(null)

        // Act
        val result = repository.addHealthRecord(fowlId, healthRecord)

        // Assert
        assertTrue(result.isSuccess)
    }
}