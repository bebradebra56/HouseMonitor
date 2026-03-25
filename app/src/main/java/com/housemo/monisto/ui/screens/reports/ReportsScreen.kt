package com.housemo.monisto.ui.screens.reports

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
import org.koin.compose.koinInject
import androidx.navigation.NavController
import com.housemo.monisto.data.prefs.PreferencesManager
import com.housemo.monisto.ui.components.*
import com.housemo.monisto.ui.theme.*

@Composable
fun ReportsScreen(
    navController: NavController,
    viewModel: ReportViewModel = koinViewModel()
) {
    val prefs: PreferencesManager = koinInject()
    val currency by prefs.defaultCurrency.collectAsState(initial = "USD")
    val stats by viewModel.stats.collectAsState()
    val buildings by viewModel.buildings.collectAsState()
    val openIssues by viewModel.openIssues.collectAsState()
    val allRepairs by viewModel.allRepairs.collectAsState()

    LazyColumn(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            Box(
                Modifier.fillMaxWidth().background(Brush.verticalGradient(listOf(Blue40, Teal40)))
                    .padding(20.dp)
            ) {
                Column {
                    Text("Building Reports", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Analytics & condition overview", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f))
                }
            }
        }

        item {
            Spacer(Modifier.height(16.dp))
            SectionHeader("Summary")
            Row(
                Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DashboardStatCard("Buildings", stats.totalBuildings.toString(), Icons.Default.Business, Blue40, Modifier.weight(1f))
                DashboardStatCard("Inspections", stats.totalInspections.toString(), Icons.Default.AssignmentTurnedIn, Teal40, Modifier.weight(1f))
            }
        }

        item {
            Spacer(Modifier.height(12.dp))
            Row(
                Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DashboardStatCard("Open Issues", stats.openIssues.toString(), Icons.Default.ReportProblem, if (stats.openIssues > 0) SeverityCritical else ConditionGood, Modifier.weight(1f))
                DashboardStatCard("Pending Repairs", stats.pendingRepairs.toString(), Icons.Default.Build, if (stats.pendingRepairs > 0) Orange40 else ConditionGood, Modifier.weight(1f))
            }
        }

        if (stats.criticalIssues > 0) {
            item {
                Spacer(Modifier.height(12.dp))
                ElevatedCard(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = SeverityCritical.copy(alpha = 0.08f))
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Default.Warning, null, tint = SeverityCritical, modifier = Modifier.size(32.dp))
                        Column {
                            Text("${stats.criticalIssues} Critical Issue${if (stats.criticalIssues != 1) "s" else ""}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = SeverityCritical)
                            Text("Requires immediate attention", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        if (buildings.isNotEmpty()) {
            item { Spacer(Modifier.height(16.dp)); SectionHeader("Buildings Condition") }
            items(buildings) { building ->
                ElevatedCard(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text(building.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                if (building.address.isNotEmpty()) Text(building.address, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        ConditionScoreIndicator(building.conditionScore)
                    }
                }
            }
        }

        if (openIssues.isNotEmpty()) {
            item { Spacer(Modifier.height(16.dp)); SectionHeader("Issues by Severity") }
            item {
                ElevatedCard(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        listOf("Critical", "High", "Medium", "Low").forEach { sev ->
                            val count = openIssues.count { it.severity == sev }
                            val color = when (sev) {
                                "Critical" -> SeverityCritical
                                "High" -> SeverityHigh
                                "Medium" -> SeverityMedium
                                else -> SeverityLow
                            }
                            if (count > 0) {
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Box(Modifier.size(12.dp).clip(RoundedCornerShape(50)).background(color))
                                        Text(sev, style = MaterialTheme.typography.bodyMedium, color = color, fontWeight = FontWeight.Medium)
                                    }
                                    Text("$count", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = color)
                                }
                                LinearProgressIndicator(
                                    progress = { count.toFloat() / openIssues.size },
                                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(50)),
                                    color = color,
                                    trackColor = color.copy(alpha = 0.15f)
                                )
                            }
                        }
                    }
                }
            }
        }

        if (allRepairs.isNotEmpty()) {
            item { Spacer(Modifier.height(16.dp)); SectionHeader("Repairs by Status") }
            item {
                ElevatedCard(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        listOf("Pending" to StatusPending, "InProgress" to StatusInProgress, "Done" to StatusDone).forEach { (status, color) ->
                            val count = allRepairs.count { it.status == status }
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Box(Modifier.size(12.dp).clip(RoundedCornerShape(50)).background(color))
                                    Text(status, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                }
                                Text("$count", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = color)
                            }
                        }
                        val totalCost = allRepairs.filter { it.status != "Done" }.sumOf { it.cost }
                        if (totalCost > 0) {
                            HorizontalDivider()
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Total Pending Cost", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                Text("$currency ${String.format("%.0f", totalCost)}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Orange40)
                            }
                        }
                    }
                }
            }
        }
    }
}
