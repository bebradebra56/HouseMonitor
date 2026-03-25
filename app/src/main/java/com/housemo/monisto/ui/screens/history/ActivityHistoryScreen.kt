package com.housemo.monisto.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.housemo.monisto.ui.theme.Blue40
import com.housemo.monisto.ui.theme.Teal40
import com.housemo.monisto.ui.theme.Orange40
import com.housemo.monisto.ui.theme.Green40
import com.housemo.monisto.util.DateUtils

@Composable
fun ActivityHistoryScreen(
    navController: NavController,
    viewModel: ActivityHistoryViewModel = koinViewModel()
) {
    val logs by viewModel.logs.collectAsState()

    if (logs.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            EmptyState(Icons.Default.History, "No activity yet", "Your actions will be recorded here")
        }
        return
    }

    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text("${logs.size} activities", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        items(logs, key = { it.id }) { log ->
            val iconColor = when {
                log.action.contains("Building") -> Blue40
                log.action.contains("Inspection") -> Teal40
                log.action.contains("Repair") -> Orange40
                log.action.contains("Resolved") -> Green40
                else -> MaterialTheme.colorScheme.primary
            }
            val icon = when {
                log.action.contains("Building") -> Icons.Default.Home
                log.action.contains("Inspection") -> Icons.Default.AssignmentTurnedIn
                log.action.contains("Repair") -> Icons.Default.Build
                log.action.contains("Issue") -> Icons.Default.ReportProblem
                log.action.contains("Material") -> Icons.Default.Inventory
                log.action.contains("Resolved") -> Icons.Default.CheckCircle
                else -> Icons.Default.History
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    Modifier.size(40.dp).clip(CircleShape).background(iconColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = iconColor, modifier = Modifier.size(20.dp))
                }
                Column(Modifier.weight(1f)) {
                    Text(log.action, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Text(log.details, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(DateUtils.formatDateTime(log.timestamp), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                }
            }
            HorizontalDivider(modifier = Modifier.padding(start = 52.dp))
        }
    }
}
