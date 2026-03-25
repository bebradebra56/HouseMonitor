package com.housemo.monisto.ui.screens.schedule

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import androidx.navigation.NavController
import com.housemo.monisto.data.prefs.PreferencesManager
import com.housemo.monisto.ui.components.*
import com.housemo.monisto.ui.navigation.Screen
import com.housemo.monisto.ui.screens.repairs.RepairViewModel
import com.housemo.monisto.ui.theme.*
import com.housemo.monisto.util.DateUtils

@Composable
fun ScheduleScreen(
    navController: NavController,
    viewModel: RepairViewModel = koinViewModel()
) {
    val prefs: PreferencesManager = koinInject()
    val currency by prefs.defaultCurrency.collectAsState(initial = "USD")
    val scheduled by viewModel.scheduledRepairs.collectAsState()

    if (scheduled.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            EmptyState(Icons.Default.CalendarMonth, "No scheduled repairs", "Repairs with scheduled dates will appear here")
        }
        return
    }

    val overdue = scheduled.filter { DateUtils.isOverdue(it.scheduledDate) }
    val upcoming = scheduled.filter { !DateUtils.isOverdue(it.scheduledDate) }

    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (overdue.isNotEmpty()) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Warning, null, tint = SeverityCritical, modifier = Modifier.size(20.dp))
                    Text("Overdue (${overdue.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = SeverityCritical)
                }
            }
            items(overdue, key = { it.id }) { repair ->
                ElevatedCard(
                    onClick = { navController.navigate(Screen.Tasks.createRoute(repair.id)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(containerColor = SeverityCritical.copy(alpha = 0.05f))
                ) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(SeverityCritical.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Build, null, tint = SeverityCritical, modifier = Modifier.size(24.dp))
                        }
                        Column(Modifier.weight(1f)) {
                            Text(repair.type, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            Text(repair.scheduledDate?.let { DateUtils.formatDate(it) } ?: "", style = MaterialTheme.typography.bodySmall, color = SeverityCritical)
                        }
                        PriorityBadge(repair.priority)
                    }
                }
            }
        }

        if (upcoming.isNotEmpty()) {
            item { SectionHeader("Upcoming (${upcoming.size})") }
            items(upcoming, key = { it.id }) { repair ->
                ElevatedCard(
                    onClick = { navController.navigate(Screen.Tasks.createRoute(repair.id)) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(StatusPending.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.CalendarMonth, null, tint = StatusPending, modifier = Modifier.size(24.dp))
                        }
                        Column(Modifier.weight(1f)) {
                            Text(repair.type, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            if (repair.contractor.isNotEmpty()) Text(repair.contractor, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            val isDueSoon = DateUtils.isDueSoon(repair.scheduledDate)
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.Schedule, null, modifier = Modifier.size(12.dp), tint = if (isDueSoon) SeverityMedium else MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(repair.scheduledDate?.let { DateUtils.formatDate(it) } ?: "", style = MaterialTheme.typography.bodySmall, color = if (isDueSoon) SeverityMedium else MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            PriorityBadge(repair.priority)
                            if (repair.cost > 0) {
                                Spacer(Modifier.height(4.dp))
                                Text("$currency ${String.format("%.0f", repair.cost)}", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }
    }
}
