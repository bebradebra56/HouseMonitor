package com.housemo.monisto.ui.navigation

import androidx.compose.animation.*
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.*
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.housemo.monisto.ui.screens.buildings.*
import com.housemo.monisto.ui.screens.dashboard.DashboardScreen
import com.housemo.monisto.ui.screens.history.ActivityHistoryScreen
import com.housemo.monisto.ui.screens.inspections.*
import com.housemo.monisto.ui.screens.materials.MaterialsScreen
import com.housemo.monisto.ui.screens.measurements.MeasurementsScreen
import com.housemo.monisto.ui.screens.notifications.NotificationsScreen
import com.housemo.monisto.ui.screens.photos.*
import com.housemo.monisto.ui.screens.repairs.*
import com.housemo.monisto.ui.screens.reports.ReportsScreen
import com.housemo.monisto.ui.screens.schedule.ScheduleScreen
import com.housemo.monisto.ui.screens.settings.SettingsScreen
import com.housemo.monisto.ui.screens.splash.SplashScreen
import com.housemo.monisto.ui.screens.structures.*
import com.housemo.monisto.ui.screens.tasks.TasksScreen

data class BottomNavItemDef(val navigateTo: String, val baseRoute: String, val icon: ImageVector)

val bottomNavDefs = listOf(
    BottomNavItemDef(Screen.Dashboard.route, Screen.Dashboard.route, Icons.Default.Dashboard, ),
    BottomNavItemDef(Screen.Buildings.route, Screen.Buildings.route, Icons.Default.Business, ),
    BottomNavItemDef(Screen.Inspections.createRoute(-1L), "inspections", Icons.Default.AssignmentTurnedIn),
    BottomNavItemDef(Screen.Repairs.createRoute(-1L), "repairs", Icons.Default.Build),
    BottomNavItemDef(Screen.Reports.route, Screen.Reports.route, Icons.Default.BarChart)
)

val bottomNavTemplateRoutes = setOf(
    Screen.Dashboard.route,
    Screen.Buildings.route,
    Screen.Inspections.route,
    Screen.Repairs.route,
    Screen.Reports.route
)

val noTopBarRoutes = setOf(Screen.Splash.route)

fun isBottomNavSelected(navDef: BottomNavItemDef, currentRoute: String?): Boolean {
    if (currentRoute == null) return false
    return when (navDef.baseRoute) {
        Screen.Dashboard.route -> currentRoute == Screen.Dashboard.route
        Screen.Buildings.route -> currentRoute.startsWith("building") || currentRoute.startsWith("floor") || currentRoute.startsWith("room") || currentRoute.startsWith("structure") || currentRoute == Screen.Buildings.route
        "inspections" -> currentRoute.startsWith("inspections") || currentRoute.startsWith("issues") || currentRoute.startsWith("add_issue") || currentRoute.startsWith("issue_details")
        "repairs" -> currentRoute.startsWith("repairs") || currentRoute.startsWith("add_repair") || currentRoute.startsWith("tasks")
        Screen.Reports.route -> currentRoute == Screen.Reports.route
        else -> currentRoute == navDef.baseRoute
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in bottomNavTemplateRoutes
    val showTopBar = currentRoute !in noTopBarRoutes

    val title = when {
        currentRoute == Screen.Dashboard.route -> "House Monitor"
        currentRoute == Screen.Buildings.route -> "Buildings"
        currentRoute == Screen.AddBuilding.route -> "Add Building"
        currentRoute?.startsWith("building_overview") == true -> "Building Overview"
        currentRoute?.startsWith("floors") == true -> "Floors"
        currentRoute?.startsWith("add_floor") == true -> "Add Floor"
        currentRoute?.startsWith("rooms") == true -> "Rooms"
        currentRoute?.startsWith("add_room") == true -> "Add Room"
        currentRoute?.startsWith("structures") == true -> "Structures"
        currentRoute?.startsWith("structure_details") == true -> "Structure Details"
        currentRoute?.startsWith("inspections") == true -> "Inspections"
        currentRoute?.startsWith("add_inspection") == true -> "Add Inspection"
        currentRoute?.startsWith("issues") == true -> "Issues"
        currentRoute?.startsWith("add_issue") == true -> "Add Issue"
        currentRoute?.startsWith("issue_details") == true -> "Issue Details"
        currentRoute?.startsWith("photos") == true -> "Photos"
        currentRoute?.startsWith("add_photo") == true -> "Add Photo"
        currentRoute?.startsWith("measurements") == true -> "Measurements"
        currentRoute == Screen.Materials.route -> "Materials"
        currentRoute?.startsWith("repairs") == true -> "Repairs"
        currentRoute?.startsWith("add_repair") == true -> "Add Repair"
        currentRoute == Screen.Schedule.route -> "Schedule"
        currentRoute?.startsWith("tasks") == true -> "Tasks"
        currentRoute == Screen.Reports.route -> "Reports"
        currentRoute == Screen.ActivityHistory.route -> "Activity History"
        currentRoute == Screen.Notifications.route -> "Notifications"
        currentRoute == Screen.Settings.route -> "Settings"
        else -> "House Monitor"
    }

    val showBack = currentRoute !in bottomNavTemplateRoutes && currentRoute != Screen.Splash.route

    Scaffold(
        topBar = {
            if (showTopBar) {
                TopAppBar(
                    title = { Text(title) },
                    navigationIcon = {
                        if (showBack) {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                            }
                        }
                    },
                    actions = {
                        if (currentRoute == Screen.Dashboard.route) {
                            IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                                Icon(Icons.Default.Settings, contentDescription = "Settings")
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavDefs.forEach { item ->
                        val selected = isBottomNavSelected(item, currentRoute)
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = "") },
                            selected = selected,
                            onClick = {
                                navController.navigate(item.navigateTo) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn() + slideInHorizontally { it / 6 } },
            exitTransition = { fadeOut() + slideOutHorizontally { -it / 6 } },
            popEnterTransition = { fadeIn() + slideInHorizontally { -it / 6 } },
            popExitTransition = { fadeOut() + slideOutHorizontally { it / 6 } }
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                })
            }
            composable(Screen.Dashboard.route) {
                DashboardScreen(navController = navController)
            }
            composable(Screen.Buildings.route) {
                BuildingsScreen(navController = navController)
            }
            composable(Screen.AddBuilding.route) {
                AddBuildingScreen(navController = navController)
            }
            composable(
                route = Screen.BuildingOverview.route,
                arguments = listOf(navArgument("buildingId") { type = NavType.LongType })
            ) {
                BuildingOverviewScreen(navController = navController)
            }
            composable(
                route = Screen.Floors.route,
                arguments = listOf(navArgument("buildingId") { type = NavType.LongType })
            ) {
                FloorsScreen(navController = navController)
            }
            composable(
                route = Screen.AddFloor.route,
                arguments = listOf(navArgument("buildingId") { type = NavType.LongType })
            ) {
                AddFloorScreen(navController = navController)
            }
            composable(
                route = Screen.Rooms.route,
                arguments = listOf(navArgument("floorId") { type = NavType.LongType })
            ) {
                RoomsScreen(navController = navController)
            }
            composable(
                route = Screen.AddRoom.route,
                arguments = listOf(navArgument("floorId") { type = NavType.LongType })
            ) {
                AddRoomScreen(navController = navController)
            }
            composable(
                route = Screen.Structures.route,
                arguments = listOf(navArgument("roomId") { type = NavType.LongType })
            ) {
                StructuresScreen(navController = navController)
            }
            composable(
                route = Screen.StructureDetails.route,
                arguments = listOf(navArgument("structureId") { type = NavType.LongType })
            ) {
                StructureDetailsScreen(navController = navController)
            }
            composable(
                route = Screen.Inspections.route,
                arguments = listOf(navArgument("buildingId") { type = NavType.LongType })
            ) {
                InspectionsScreen(navController = navController)
            }
            composable(
                route = Screen.AddInspection.route,
                arguments = listOf(navArgument("buildingId") { type = NavType.LongType })
            ) {
                AddInspectionScreen(navController = navController)
            }
            composable(
                route = Screen.Issues.route,
                arguments = listOf(navArgument("inspectionId") { type = NavType.LongType })
            ) {
                IssuesScreen(navController = navController)
            }
            composable(
                route = Screen.AddIssue.route,
                arguments = listOf(navArgument("inspectionId") { type = NavType.LongType })
            ) {
                AddIssueScreen(navController = navController)
            }
            composable(
                route = Screen.IssueDetails.route,
                arguments = listOf(navArgument("issueId") { type = NavType.LongType })
            ) {
                IssueDetailsScreen(navController = navController)
            }
            composable(
                route = Screen.Photos.route,
                arguments = listOf(navArgument("issueId") { type = NavType.LongType })
            ) {
                PhotosScreen(navController = navController)
            }
            composable(
                route = Screen.AddPhoto.route,
                arguments = listOf(navArgument("issueId") { type = NavType.LongType })
            ) {
                AddPhotoScreen(navController = navController)
            }
            composable(
                route = Screen.Measurements.route,
                arguments = listOf(navArgument("issueId") { type = NavType.LongType })
            ) {
                MeasurementsScreen(navController = navController)
            }
            composable(Screen.Materials.route) {
                MaterialsScreen(navController = navController)
            }
            composable(
                route = Screen.Repairs.route,
                arguments = listOf(navArgument("issueId") { type = NavType.LongType })
            ) {
                RepairsScreen(navController = navController)
            }
            composable(
                route = Screen.AddRepair.route,
                arguments = listOf(navArgument("issueId") { type = NavType.LongType })
            ) {
                AddRepairScreen(navController = navController)
            }
            composable(Screen.Schedule.route) {
                ScheduleScreen(navController = navController)
            }
            composable(
                route = Screen.Tasks.route,
                arguments = listOf(navArgument("repairId") { type = NavType.LongType })
            ) {
                TasksScreen(navController = navController)
            }
            composable(Screen.Reports.route) {
                ReportsScreen(navController = navController)
            }
            composable(Screen.ActivityHistory.route) {
                ActivityHistoryScreen(navController = navController)
            }
            composable(Screen.Notifications.route) {
                NotificationsScreen(navController = navController)
            }
            composable(Screen.Settings.route) {
                SettingsScreen(navController = navController)
            }
        }
    }
}
