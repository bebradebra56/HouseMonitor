package com.housemo.monisto.ui.screens.buildings

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
import com.housemo.monisto.data.local.entity.Floor
import com.housemo.monisto.ui.components.*
import com.housemo.monisto.ui.navigation.Screen
import com.housemo.monisto.ui.theme.Blue40

@Composable
fun FloorsScreen(
    navController: NavController,
    viewModel: BuildingViewModel = koinViewModel()
) {
    val floors by viewModel.floors.collectAsState()
    val building by viewModel.currentBuilding.collectAsState()
    var deleteFloor by remember { mutableStateOf<Floor?>(null) }

    Box(Modifier.fillMaxSize()) {
        if (floors.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                EmptyState(Icons.Default.Layers, "No floors yet", "Add floors to organize your rooms")
            }
        } else {
            LazyColumn(
                Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item { Text("${floors.size} floor${if (floors.size != 1) "s" else ""}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                items(floors, key = { it.id }) { floor ->
                    ElevatedCard(
                        onClick = { navController.navigate(Screen.Rooms.createRoute(floor.id)) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(Blue40.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("${floor.level}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Blue40)
                            }
                            Column(Modifier.weight(1f)) {
                                Text(floor.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                Text("Level ${floor.level}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            IconButton(onClick = { deleteFloor = floor }, modifier = Modifier.size(36.dp)) {
                                Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
        building?.id?.let { bId ->
            ExtendedFloatingActionButton(
                onClick = { navController.navigate(Screen.AddFloor.createRoute(bId)) },
                icon = { Icon(Icons.Default.Add, null) },
                text = { Text("Add Floor") },
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
            )
        }
    }

    deleteFloor?.let { f ->
        DeleteConfirmDialog("Delete Floor", "Delete \"${f.name}\"?", onConfirm = { viewModel.deleteFloor(f); deleteFloor = null }, onDismiss = { deleteFloor = null })
    }
}

@Composable
fun AddFloorScreen(navController: NavController, viewModel: BuildingViewModel = koinViewModel()) {
    var name by remember { mutableStateOf("") }
    var level by remember { mutableStateOf("0") }
    var nameError by remember { mutableStateOf(false) }
    val floors = listOf("Basement", "Ground Floor", "Floor 1", "Floor 2", "Floor 3", "Floor 4", "Roof")

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionHeader("Floor Information")
        OutlinedTextField(
            value = name,
            onValueChange = { name = it; nameError = false },
            label = { Text("Floor Name *") },
            leadingIcon = { Icon(Icons.Default.Layers, null) },
            isError = nameError,
            supportingText = if (nameError) {{ Text("Required") }} else null,
            modifier = Modifier.fillMaxWidth()
        )
        Text("Quick Select:", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        @OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            floors.forEach { suggestion ->
                FilterChip(
                    selected = name == suggestion,
                    onClick = { name = suggestion },
                    label = { Text(suggestion, style = MaterialTheme.typography.labelSmall) }
                )
            }
        }
        OutlinedTextField(
            value = level,
            onValueChange = { if (it.all { c -> c.isDigit() || c == '-' }) level = it },
            label = { Text("Level Number") },
            supportingText = { Text("Use negative for basement (e.g. -1)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = {
                if (name.isBlank()) { nameError = true; return@Button }
                viewModel.addFloor(name.trim(), level.toIntOrNull() ?: 0)
                navController.navigateUp()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Save, null, Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text("Save Floor")
        }
    }
}
