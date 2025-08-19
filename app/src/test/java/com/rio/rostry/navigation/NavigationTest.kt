package com.rio.rostry.navigation

import org.junit.Test
import org.junit.Assert.*

class NavigationTest {
    
    @Test
    fun testRouteBuilding() {
        // Test that route builders work correctly
        val fowlId = "test-fowl-id"
        val expectedEditFowlRoute = "edit_fowl/$fowlId"
        val expectedFowlDetailRoute = "fowl_detail/$fowlId"
        val expectedAddHealthRecordRoute = "add_health_record/$fowlId"
        
        assertEquals(expectedEditFowlRoute, AppRoutes.editFowl(fowlId))
        assertEquals(expectedFowlDetailRoute, AppRoutes.fowlDetail(fowlId))
        assertEquals(expectedAddHealthRecordRoute, AppRoutes.addHealthRecord(fowlId))
    }
    
    @Test
    fun testDestinationRoutes() {
        // Test that destination routes are correctly defined
        assertEquals("login", AppDestination.Login.route)
        assertEquals("home", AppDestination.Home.route)
        assertEquals("edit_fowl/{fowlId}", AppDestination.EditFowl("").route)
        assertEquals("fowl_detail/{fowlId}", AppDestination.FowlDetail("").route)
        assertEquals("add_health_record/{fowlId}", AppDestination.AddHealthRecord("").route)
    }
    
    @Test
    fun testDestinationArguments() {
        // Test that destination arguments are correctly defined
        val editFowlDestination = AppDestination.EditFowl("")
        assertEquals(1, editFowlDestination.arguments.size)
        assertEquals("fowlId", editFowlDestination.arguments[0].name)
        
        val fowlDetailDestination = AppDestination.FowlDetail("")
        assertEquals(1, fowlDetailDestination.arguments.size)
        assertEquals("fowlId", fowlDetailDestination.arguments[0].name)
        
        val addHealthRecordDestination = AppDestination.AddHealthRecord("")
        assertEquals(1, addHealthRecordDestination.arguments.size)
        assertEquals("fowlId", addHealthRecordDestination.arguments[0].name)
    }
    
    @Test
    fun testDestinationDeepLinks() {
        // Test that destination deep links are correctly defined
        val editFowlDestination = AppDestination.EditFowl("")
        assertEquals(1, editFowlDestination.deepLinks.size)
        assertEquals("rostry://edit_fowl/{fowlId}", editFowlDestination.deepLinks[0].uriPattern)
        
        val fowlDetailDestination = AppDestination.FowlDetail("")
        assertEquals(1, fowlDetailDestination.deepLinks.size)
        assertEquals("rostry://fowl/{fowlId}", fowlDetailDestination.deepLinks[0].uriPattern)
        
        val addHealthRecordDestination = AppDestination.AddHealthRecord("")
        assertEquals(1, addHealthRecordDestination.deepLinks.size)
        assertEquals("rostry://add_health_record/{fowlId}", addHealthRecordDestination.deepLinks[0].uriPattern)
    }
}