package com.yveltius.versememorization.data.collections

import com.yveltius.versememorization.data.util.JsonFileReader
import com.yveltius.versememorization.data.util.Log
import com.yveltius.versememorization.data.util.VerseMemorizationLog
import com.yveltius.versememorization.data.verses.EmptyVerseJsonFileReaderTestImpl
import com.yveltius.versememorization.data.verses.VerseJsonFileReaderTestImpl
import com.yveltius.versememorization.data.verses.VerseRepository
import com.yveltius.versememorization.data.verses.VerseRepositoryImpl
import com.yveltius.versememorization.entity.collections.VerseCollection
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertThrows
import org.junit.jupiter.api.assertDoesNotThrow
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.inject
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class VerseCollectionRepositoryImplTest {
    @BeforeTest
    fun before() {
        startKoin {
            modules(
                module {
                    single<Log> { VerseMemorizationLog() }
                    single<JsonFileReader>(named("notEmpty")) { VerseCollectionJsonFileReaderTestImpl() }
                    single<JsonFileReader>(named("Empty")) { EmptyCollectionJsonFileReaderTestImpl() }
                    single<JsonFileReader>(named("notEmptyVerses")) { VerseJsonFileReaderTestImpl() }
                    single<JsonFileReader>(named("EmptyVerses")) { EmptyVerseJsonFileReaderTestImpl() }
                    single<VerseCollectionRepository> {
                        VerseCollectionRepositoryImpl(
                            log = get(),
                            jsonFileReader = get(named("notEmpty")),
                            verseRepository = get()
                        )
                    }
                    single<VerseCollectionRepository>(named("Empty")) {
                        VerseCollectionRepositoryImpl(
                            log = get(),
                            jsonFileReader = get(named("Empty")),
                            verseRepository = get(named("EmptyVerses"))
                        )
                    }
                    single<VerseRepository> {
                        VerseRepositoryImpl(
                            log = get(),
                            jsonFileReader = get(named("notEmptyVerses"))
                        )
                    }
                    single<VerseRepository>(named("EmptyVerses")) {
                        VerseRepositoryImpl(
                            log = get(),
                            jsonFileReader = get(named("EmptyVerses"))
                        )
                    }
                }
            )
        }
    }

    @AfterTest
    fun after() {
        stopKoin()
    }

    @Test
    fun `no collections returned from empty repo`() {
        val emptyVerseCollectionRepository: VerseCollectionRepository by inject(
            VerseCollectionRepository::class.java,
            named("Empty")
        )

        val collections =
            runBlocking { emptyVerseCollectionRepository.getAllCollections().getOrNull() }
        assertTrue(message = "Collection should NOT be null and should be empty. Size: ${collections?.size}") {
            collections != null && collections.isEmpty()
        }
    }

    @Test
    fun `get collections returns correct collection list size`() {
        val collectionRepository: VerseCollectionRepository by inject(VerseCollectionRepository::class.java)

        val actual = runBlocking { collectionRepository.getAllCollections() }.getOrNull()?.size

        val expected = 4

        assertTrue(message = "Expected: $expected\nActual: $actual") {
            actual == expected
        }
    }

    @Test
    fun `correct number of collections returned for Hebrews 12`() {
        val collectionRepository: VerseCollectionRepository by inject(VerseCollectionRepository::class.java)
        val verseRepository: VerseRepository by inject(VerseRepository::class.java)

        val actual = runBlocking {
            val verse = verseRepository.getVerse(book = "Hebrews").getOrThrow()

            val collectionsContainingVerse = collectionRepository
                .getCollectionsForVerse(verse)
                .getOrThrow()
                .size

            collectionsContainingVerse
        }

        val expected = 2

        assertTrue("Expected: $expected\nActual: $actual") {
            actual == expected
        }
    }

    @Test
    fun `empty set returned when verse isn't in any collection`() {
        val collectionRepository: VerseCollectionRepository by inject(VerseCollectionRepository::class.java)
        val verseRepository: VerseRepository by inject(VerseRepository::class.java)

        val actual = runBlocking {
            val verse = verseRepository.getVerse(book = "deuteronomy").getOrThrow()

            val collectionsContainingVerse = collectionRepository
                .getCollectionsForVerse(verse)
                .getOrThrow()
                .size

            collectionsContainingVerse
        }

        val expected = 0

        assertTrue("Expected: $expected\nActual: $actual") {
            actual == expected
        }
    }

    @Test
    fun `throws exception when empty name is provided when creating a collection`() {
        val collectionRepository: VerseCollectionRepository by inject(VerseCollectionRepository::class.java)
        val verseRepository: VerseRepository by inject(VerseRepository::class.java)

        val verses = runBlocking {
            verseRepository.getVerses(book = "Romans").getOrThrow()
        }

        assertThrows(Throwable::class.java) {
            runBlocking {
                val newCollection = VerseCollection(
                    name = "",
                    verses = verses
                )

                collectionRepository.addCollection(newCollection).getOrThrow()
            }
        }
    }

    @Test
    fun `able to add a collection with no verses`() {
        val collectionRepository: VerseCollectionRepository by inject(VerseCollectionRepository::class.java)

        assertDoesNotThrow(message = "Collections without verses are acceptable and should not cause an issue.") {
            runBlocking {
                val newCollection = VerseCollection(
                    name = "New Collection",
                    verses = setOf()
                )

                collectionRepository.addCollection(newCollection).getOrThrow()
            }
        }
    }

    @Test
    fun `throws exception when adding collection with same name as another`() {
        val collectionRepository: VerseCollectionRepository by inject(VerseCollectionRepository::class.java)

        assertThrows(Throwable::class.java) {
            runBlocking {
                val allCollections = collectionRepository.getAllCollections().getOrThrow()

                collectionRepository.addCollection(allCollections.first()).getOrThrow()
            }
        }
    }

    @Test
    fun `throws exception when adding collection with same name as another ignore case`() {
        val collectionRepository: VerseCollectionRepository by inject(VerseCollectionRepository::class.java)

        assertThrows(Throwable::class.java) {
            runBlocking {
                val allCollections = collectionRepository.getAllCollections().getOrThrow()

                collectionRepository.addCollection(
                    newCollection = VerseCollection(
                        name = allCollections.first().name.uppercase(),
                        verses = allCollections.first().verses
                    )
                ).getOrThrow()
            }
        }
    }

    @Test
    fun `added collection shows up when getting all collections`() {
        val collectionRepository: VerseCollectionRepository by inject(VerseCollectionRepository::class.java)
        val verseRepository: VerseRepository by inject(VerseRepository::class.java)

        val verses = runBlocking {
            verseRepository.getVerses(book = "Romans").getOrThrow()
        }

        val newCollection = VerseCollection(
            name = "New Collection",
            verses = verses
        )

        assertTrue(message = "Expected added VerseCollection(${newCollection.name} to be in getAllCollections return value.") {
            runBlocking {
                collectionRepository.addCollection(newCollection).getOrThrow()

                collectionRepository.getAllCollections().getOrThrow().contains(newCollection)
            }
        }
    }

    @Test
    fun `deleted verse is removed from relevant collections`() {
        val collectionRepository: VerseCollectionRepository by inject(VerseCollectionRepository::class.java)
        val verseRepository: VerseRepository by inject(VerseRepository::class.java)

        val relevantVerse = runBlocking {
            verseRepository.getVerse(book = "Romans").getOrThrow()
        }

        val collectionWithRelevantVerse = runBlocking {
            collectionRepository
                .getAllCollections()
                .getOrThrow()
                .first { it.verses.any { verse -> verse.uuid == relevantVerse.uuid } }
        }

        assertTrue {
            collectionWithRelevantVerse.verses.contains(relevantVerse)
        }

        // we have confirmed it is in the collection so now we need to delete it from verse repo
        runBlocking { verseRepository.removeVerse(relevantVerse) }

        assertTrue {
            runBlocking {
                collectionRepository.getAllCollections().getOrThrow()
                    .none { it.verses.contains(relevantVerse) }
            }
        }
    }

    @Test
    fun `when trying to delete a collection, if it does not exist, count it as a success`() {
        val collectionRepository: VerseCollectionRepository by inject(VerseCollectionRepository::class.java)

        val newCollection = VerseCollection(
            name = "New Collection",
            verses = setOf()
        )

        assertTrue(message = "Should count as a success when deleting a non-existent collection. Encountered an error.") {
            runBlocking {
                collectionRepository.deleteCollection(
                    collection = newCollection
                ).isSuccess
            }
        }
    }

    @Test
    fun `deleted collection no longer shows up in all collections`() {
        val collectionRepository: VerseCollectionRepository by inject(VerseCollectionRepository::class.java)

        val isCollectionDeleted = runBlocking {
            val allCollectionsPreDelete = collectionRepository.getAllCollections().getOrThrow()
            val nameOfCollectionBeingDeleted = allCollectionsPreDelete.first().name
            collectionRepository.deleteCollection(collection = allCollectionsPreDelete.first())

            !collectionRepository.getAllCollections().getOrThrow().any { collection ->
                collection.name.equals(
                    nameOfCollectionBeingDeleted,
                    ignoreCase = true
                )
            }
        }

        assertTrue(message = "Found a collection in all collections that should have been deleted.") {
            isCollectionDeleted
        }
    }

    @Test
    fun `adding a verse to a collection it is already a part of can count as a success`() {
        val collectionRepository: VerseCollectionRepository by inject(VerseCollectionRepository::class.java)
        val verseRepository: VerseRepository by inject(VerseRepository::class.java)

        val verseToAdd = runBlocking {
            verseRepository.getVerses(book = "Romans").getOrThrow()
        }.first()

        val collectionContainingVerse = runBlocking {
            collectionRepository.getCollectionsForVerse(verseToAdd).getOrThrow().first()
        }

        assertTrue(message = "Adding a verse that is already in a collection should be OK.") {
            runBlocking {
                collectionRepository.addVerseToCollection(collectionName = collectionContainingVerse.name, verse = verseToAdd).getOrThrow()
            }
        }
    }
}