package com.dev.petmarket_android.common.adapter

import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import com.dev.petmarket_android.common.model.PetResponse
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Test adapter using PetResponse as example data type
 */
class TestPetAdapter : DiffUtilAdapter<PetResponse, TestPetAdapter.ViewHolder>(
    areItemsTheSame = { old, new -> old.id == new.id },
    areContentsTheSame = { old, new -> old == new }
) {
    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(FrameLayout(parent.context))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // No-op for testing
    }

    class ViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView)
}

@RunWith(RobolectricTestRunner::class)
class DiffUtilAdapterTest {

    private lateinit var adapter: TestPetAdapter

    @Before
    fun setUp() {
        adapter = TestPetAdapter()
    }

    @Test
    fun submitList_initialSubmit_addsItems() {
        val items = listOf(
            PetResponse(id = 1, name = "Fluffy", price = 100.0, status = "AVAILABLE"),
            PetResponse(id = 2, name = "Max", price = 150.0, status = "AVAILABLE")
        )

        adapter.submitList(items)

        assertEquals("Adapter should have 2 items", 2, adapter.itemCount)
    }

    @Test
    fun submitList_emptyList_removesAllItems() {
        val initialItems = listOf(
            PetResponse(id = 1, name = "Fluffy", price = 100.0, status = "AVAILABLE")
        )
        adapter.submitList(initialItems)
        assertEquals(1, adapter.itemCount)

        adapter.submitList(emptyList())

        assertEquals("Adapter should have 0 items", 0, adapter.itemCount)
    }

    @Test
    fun submitList_updateList_replacesItems() {
        val initialItems = listOf(
            PetResponse(id = 1, name = "Fluffy", price = 100.0, status = "AVAILABLE")
        )
        adapter.submitList(initialItems)

        val updatedItems = listOf(
            PetResponse(id = 1, name = "Fluffy", price = 120.0, status = "AVAILABLE"),
            PetResponse(id = 2, name = "Max", price = 150.0, status = "AVAILABLE")
        )
        adapter.submitList(updatedItems)

        assertEquals("Adapter should have 2 items after update", 2, adapter.itemCount)
    }

    @Test
    fun submitList_sameItemsDifferentOrder_clearsAndReloads() {
        val list1 = listOf(
            PetResponse(id = 1, name = "Fluffy", price = 100.0, status = "AVAILABLE"),
            PetResponse(id = 2, name = "Max", price = 150.0, status = "AVAILABLE")
        )
        adapter.submitList(list1)

        val list2 = listOf(
            PetResponse(id = 2, name = "Max", price = 150.0, status = "AVAILABLE"),
            PetResponse(id = 1, name = "Fluffy", price = 100.0, status = "AVAILABLE")
        )
        adapter.submitList(list2)

        assertEquals("Adapter should have 2 items", 2, adapter.itemCount)
    }

    @Test
    fun itemCount_noItems_returnsZero() {
        assertEquals(0, adapter.itemCount)
    }

    @Test
    fun itemCount_afterSubmitList_returnsCorrectCount() {
        val items = listOf(
            PetResponse(id = 1, name = "Pet1", price = 100.0, status = "AVAILABLE"),
            PetResponse(id = 2, name = "Pet2", price = 100.0, status = "AVAILABLE"),
            PetResponse(id = 3, name = "Pet3", price = 100.0, status = "AVAILABLE")
        )
        adapter.submitList(items)

        assertEquals(3, adapter.itemCount)
    }

    @Test
    fun submitList_largeDataset_handlesEfficiently() {
        // Create a large dataset to test DiffUtil efficiency
        val largeList = (1..1000).map { id ->
            PetResponse(id = id.toLong(), name = "Pet$id", price = 100.0 * id, status = "AVAILABLE")
        }

        val startTime = System.currentTimeMillis()
        adapter.submitList(largeList)
        val endTime = System.currentTimeMillis()

        assertEquals(1000, adapter.itemCount)
        // DiffUtil should be reasonably fast even with 1000 items
        assertTrue("DiffUtil should complete in reasonable time", endTime - startTime < 5000)
    }

    @Test
    fun submitList_multipleUpdates_maintainsCorrectState() {
        var itemList = listOf(
            PetResponse(id = 1, name = "Pet1", price = 100.0, status = "AVAILABLE")
        )
        adapter.submitList(itemList)
        assertEquals(1, adapter.itemCount)

        // Add item
        itemList = listOf(
            PetResponse(id = 1, name = "Pet1", price = 100.0, status = "AVAILABLE"),
            PetResponse(id = 2, name = "Pet2", price = 150.0, status = "AVAILABLE")
        )
        adapter.submitList(itemList)
        assertEquals(2, adapter.itemCount)

        // Remove first item
        itemList = listOf(
            PetResponse(id = 2, name = "Pet2", price = 150.0, status = "AVAILABLE")
        )
        adapter.submitList(itemList)
        assertEquals(1, adapter.itemCount)

        // Clear all
        adapter.submitList(emptyList())
        assertEquals(0, adapter.itemCount)
    }
}
