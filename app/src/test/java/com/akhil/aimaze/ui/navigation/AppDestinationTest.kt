package com.akhil.aimaze.ui.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class AppDestinationTest {
    @Test
    fun routesAreUnique() {
        val routes = AppDestination.entries.map(AppDestination::route)

        assertEquals(routes.size, routes.distinct().size)
    }

    @Test
    fun homeSectionsContainEveryNonHomeDestination() {
        assertFalse(AppDestination.Home in AppDestination.homeSections)
        assertEquals(
            AppDestination.entries.size - 1,
            AppDestination.homeSections.size,
        )
    }
}
