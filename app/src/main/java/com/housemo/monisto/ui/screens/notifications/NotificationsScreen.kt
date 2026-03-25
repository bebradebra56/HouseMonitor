package com.housemo.monisto.ui.screens.notifications

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
import androidx.navigation.NavController
import com.housemo.monisto.ui.components.EmptyState
import com.housemo.monisto.ui.theme.*
import com.housemo.monisto.util.DateUtils

@Composable
fun NotificationsScreen(
    navController: NavController,
    viewModel: NotificationsViewModel = koinViewModel()
) {
    val notifications by viewModel.notifications.collectAsState()

    if (notifications.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            EmptyState(Icons.Default.NotificationsActive, "All clear!", "No urgent notifications at this time")
        }
        return
    }

    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            val urgentCount = notifications.count { it.isUrgent }
            if (urgentCount > 0) {
                ElevatedCard(
                    Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(containerColor = SeverityCritical.copy(alpha = 0.08f))
                ) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Icon(Icons.Default.Warning, null, tint = SeverityCritical, modifier = Modifier.size(24.dp))
                        Text("$urgentCount urgent item${if (urgentCount > 1) "s" else ""} require attention",
                            style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = SeverityCritical)
                    }
                }
                Spacer(Modifier.height(4.dp))
            }
        }
        items(notifications, key = { "${it.type}_${it.id}" }) { notif ->
            val color = if (notif.isUrgent) SeverityCritical else StatusPending
            val icon = when (notif.type) {
                "Repair" -> Icons.Default.Build
                "Issue" -> Icons.Default.ReportProblem
                else -> Icons.Default.Notifications
            }
            ElevatedCard(
                Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = if (notif.isUrgent) SeverityCritical.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(color.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
                    }
                    Column(Modifier.weight(1f)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(notif.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = color)
                            if (notif.isUrgent) {
                                Surface(shape = RoundedCornerShape(50), color = SeverityCritical.copy(alpha = 0.15f)) {
                                    Text("Urgent", Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = SeverityCritical, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(notif.message, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(4.dp))
                        Text(DateUtils.formatDate(notif.timestamp), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                    }
                }
            }
        }
    }
}
