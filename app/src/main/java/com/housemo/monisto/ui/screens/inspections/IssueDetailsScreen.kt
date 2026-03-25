package com.housemo.monisto.ui.screens.inspections

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import androidx.navigation.NavController
import com.housemo.monisto.ui.components.*
import com.housemo.monisto.ui.navigation.Screen
import com.housemo.monisto.ui.screens.photos.PhotoViewModel
import com.housemo.monisto.util.DateUtils

@Composable
fun IssueDetailsScreen(
    navController: NavController,
    viewModel: InspectionViewModel = koinViewModel(),
    photoViewModel: PhotoViewModel = koinViewModel()
) {
    val issue by viewModel.currentIssue.collectAsState()
    val measurements by viewModel.measurements.collectAsState()
    val photos by photoViewModel.photos.collectAsState()

    val i = issue ?: run {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(i.type, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            Text(i.location, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        SeverityBadge(i.severity)
                    }
                    HorizontalDivider()
                    if (i.description.isNotEmpty()) {
                        Text(i.description, style = MaterialTheme.typography.bodyMedium)
                    }
                    InfoRow("Status", if (i.isResolved) "Resolved" else "Open")
                    InfoRow("Reported", DateUtils.formatDate(i.createdAt))
                    if (i.resolvedAt != null) {
                        InfoRow("Resolved At", DateUtils.formatDate(i.resolvedAt))
                    }
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { navController.navigate(Screen.Photos.createRoute(i.id)) },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.PhotoLibrary, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Photos (${photos.size})")
                }
                OutlinedButton(
                    onClick = { navController.navigate(Screen.Measurements.createRoute(i.id)) },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Straighten, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Measurements")
                }
            }
        }

        item {
            OutlinedButton(
                onClick = { navController.navigate(Screen.AddRepair.createRoute(i.id)) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Build, null, Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Schedule Repair")
            }
        }

        if (!i.isResolved) {
            item {
                Button(
                    onClick = { viewModel.resolveIssue(i) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = com.housemo.monisto.ui.theme.ConditionGood)
                ) {
                    Icon(Icons.Default.CheckCircle, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Mark as Resolved")
                }
            }
        }

        if (measurements.isNotEmpty()) {
            item { SectionHeader("Measurements") }
            items(measurements) { m ->
                ElevatedCard(Modifier.fillMaxWidth()) {
                    Row(
                        Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(m.type, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
                            if (m.notes.isNotEmpty()) Text(m.notes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text("${m.value} ${m.unit}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}
