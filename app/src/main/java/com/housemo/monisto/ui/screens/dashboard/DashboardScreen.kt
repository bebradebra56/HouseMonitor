package com.housemo.monisto.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import androidx.navigation.NavController
import com.housemo.monisto.ui.components.*
import com.housemo.monisto.ui.navigation.Screen
import com.housemo.monisto.ui.theme.*
import com.housemo.monisto.util.DateUtils

@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = koinViewModel()
) {
    val buildingCount by viewModel.buildingCount.collectAsState(initial = 0)
    val openIssueCount by viewModel.openIssueCount.collectAsState(initial = 0)
    val pendingRepairCount by viewModel.pendingRepairCount.collectAsState(initial = 0)
    val recentInspections by viewModel.recentInspections.collectAsState(initial = emptyList())
    val buildings by viewModel.buildings.collectAsState(initial = emptyList())
    val openIssues by viewModel.openIssues.collectAsState(initial = emptyList())

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(Blue40, Teal40)))
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = "Good Day!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Overview of all your properties",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }

        item {
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DashboardStatCard(
                    title = "Buildings",
                    value = buildingCount.toString(),
                    icon = Icons.Default.Business,
                    color = Blue40,
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate(Screen.Buildings.route) }
                )
                DashboardStatCard(
                    title = "Open Issues",
                    value = openIssueCount.toString(),
                    icon = Icons.Default.ReportProblem,
                    color = if (openIssueCount > 0) SeverityCritical else ConditionGood,
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate(Screen.Inspections.createRoute(-1L)) }
                )
            }
        }

        item {
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DashboardStatCard(
                    title = "Inspections",
                    value = recentInspections.size.toString(),
                    icon = Icons.Default.AssignmentTurnedIn,
                    color = Teal40,
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate(Screen.Inspections.createRoute(-1L)) }
                )
                DashboardStatCard(
                    title = "Pending Repairs",
                    value = pendingRepairCount.toString(),
                    icon = Icons.Default.Build,
                    color = if (pendingRepairCount > 0) Orange40 else ConditionGood,
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate(Screen.Repairs.createRoute(-1L)) }
                )
            }
        }

        item {
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedCard(
                    onClick = { navController.navigate(Screen.Schedule.route) },
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Default.CalendarMonth, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
                        Text("Schedule", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium)
                    }
                }
                OutlinedCard(
                    onClick = { navController.navigate(Screen.Reports.route) },
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Default.BarChart, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
                        Text("Reports", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Buildings", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                TextButton(onClick = { navController.navigate(Screen.Buildings.route) }) { Text("View All") }
            }
        }

        if (buildings.isEmpty()) {
            item {
                EmptyState(
                    icon = Icons.Default.Business,
                    title = "No buildings yet",
                    subtitle = "Add your first building to start monitoring"
                )
            }
        } else {
            items(buildings.take(3)) { building ->
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                    onClick = { navController.navigate(Screen.BuildingOverview.createRoute(building.id)) }
                ) {
                    Row(
                        Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(Blue40.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Home, null, tint = Blue40, modifier = Modifier.size(28.dp))
                        }
                        Column(Modifier.weight(1f)) {
                            Text(building.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            if (building.address.isNotEmpty()) {
                                Text(building.address, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text("${building.floorsCount} floors", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        ConditionScoreIndicator(building.conditionScore, Modifier.width(64.dp))
                    }
                }
            }
        }

        if (openIssues.isNotEmpty()) {
            item {
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Open Issues", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    TextButton(onClick = { navController.navigate(Screen.Inspections.createRoute(-1L)) }) { Text("View All") }
                }
            }
            items(openIssues.take(3)) { issue ->
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                    onClick = { navController.navigate(Screen.IssueDetails.createRoute(issue.id)) }
                ) {
                    Row(
                        Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        IconCircle(Icons.Default.ReportProblem, SeverityCritical.copy(alpha = if (issue.severity == "Critical") 1f else 0.5f))
                        Column(Modifier.weight(1f)) {
                            Text(issue.type, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
                            Text(issue.location, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        SeverityBadge(issue.severity)
                    }
                }
            }
        }

        if (recentInspections.isNotEmpty()) {
            item {
                Spacer(Modifier.height(16.dp))
                Text("Recent Inspections", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 16.dp))
            }
            items(recentInspections) { inspection ->
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Row(
                        Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        IconCircle(Icons.Default.AssignmentTurnedIn, Teal40)
                        Column(Modifier.weight(1f)) {
                            Text(inspection.inspector, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
                            Text(DateUtils.formatDate(inspection.date), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        ConditionBadge(inspection.overallCondition)
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(20.dp))
            SectionHeader("Quick Actions")
        }

        item {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Triple(Icons.Default.Add, "Add Building") { navController.navigate(Screen.AddBuilding.route) },
                    Triple(Icons.Default.Inventory, "Materials") { navController.navigate(Screen.Materials.route) },
                    Triple(Icons.Default.BarChart, "Reports") { navController.navigate(Screen.Reports.route) }
                ).forEach { (icon, label, action) ->
                    FilledTonalButton(
                        onClick = action,
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 12.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(icon, null, modifier = Modifier.size(20.dp))
                            Text(label, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}
