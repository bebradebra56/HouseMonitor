package com.housemo.monisto.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Dashboard : Screen("dashboard")

    object Buildings : Screen("buildings")
    object AddBuilding : Screen("add_building")
    object BuildingOverview : Screen("building_overview/{buildingId}") {
        fun createRoute(buildingId: Long) = "building_overview/$buildingId"
    }

    object Floors : Screen("floors/{buildingId}") {
        fun createRoute(buildingId: Long) = "floors/$buildingId"
    }
    object AddFloor : Screen("add_floor/{buildingId}") {
        fun createRoute(buildingId: Long) = "add_floor/$buildingId"
    }

    object Rooms : Screen("rooms/{floorId}") {
        fun createRoute(floorId: Long) = "rooms/$floorId"
    }
    object AddRoom : Screen("add_room/{floorId}") {
        fun createRoute(floorId: Long) = "add_room/$floorId"
    }

    object Structures : Screen("structures/{roomId}") {
        fun createRoute(roomId: Long) = "structures/$roomId"
    }
    object StructureDetails : Screen("structure_details/{structureId}") {
        fun createRoute(structureId: Long) = "structure_details/$structureId"
    }

    object Inspections : Screen("inspections/{buildingId}") {
        fun createRoute(buildingId: Long = -1L) = "inspections/$buildingId"
    }
    object AddInspection : Screen("add_inspection/{buildingId}") {
        fun createRoute(buildingId: Long) = "add_inspection/$buildingId"
    }

    object Issues : Screen("issues/{inspectionId}") {
        fun createRoute(inspectionId: Long) = "issues/$inspectionId"
    }
    object AddIssue : Screen("add_issue/{inspectionId}") {
        fun createRoute(inspectionId: Long) = "add_issue/$inspectionId"
    }
    object IssueDetails : Screen("issue_details/{issueId}") {
        fun createRoute(issueId: Long) = "issue_details/$issueId"
    }

    object Photos : Screen("photos/{issueId}") {
        fun createRoute(issueId: Long) = "photos/$issueId"
    }
    object AddPhoto : Screen("add_photo/{issueId}") {
        fun createRoute(issueId: Long) = "add_photo/$issueId"
    }

    object Measurements : Screen("measurements/{issueId}") {
        fun createRoute(issueId: Long) = "measurements/$issueId"
    }

    object Materials : Screen("materials")

    object Repairs : Screen("repairs/{issueId}") {
        fun createRoute(issueId: Long = -1L) = "repairs/$issueId"
    }
    object AddRepair : Screen("add_repair/{issueId}") {
        fun createRoute(issueId: Long) = "add_repair/$issueId"
    }

    object Schedule : Screen("schedule")

    object Tasks : Screen("tasks/{repairId}") {
        fun createRoute(repairId: Long) = "tasks/$repairId"
    }

    object Reports : Screen("reports")
    object ActivityHistory : Screen("activity_history")
    object Notifications : Screen("notifications")
    object Settings : Screen("settings")
}
