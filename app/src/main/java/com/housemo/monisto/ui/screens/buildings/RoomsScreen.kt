package com.housemo.monisto.ui.screens.buildings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import androidx.navigation.NavController
import com.housemo.monisto.data.local.entity.Room
import com.housemo.monisto.data.prefs.PreferencesManager
import com.housemo.monisto.ui.components.*
import com.housemo.monisto.ui.navigation.Screen
import com.housemo.monisto.ui.theme.Teal40

@Composable
fun RoomsScreen(
    navController: NavController,
    viewModel: BuildingViewModel = koinViewModel()
) {
    val prefs: PreferencesManager = koinInject()
    val unitSystem by prefs.unitSystem.collectAsState(initial = "Metric")
    val rooms by viewModel.rooms.collectAsState()
    val floor by viewModel.currentFloor.collectAsState()
    var deleteRoom by remember { mutableStateOf<Room?>(null) }

    Box(Modifier.fillMaxSize()) {
        if (rooms.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                EmptyState(Icons.Default.MeetingRoom, "No rooms yet", "Add rooms to this floor")
            }
        } else {
            LazyColumn(
                Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(rooms, key = { it.id }) { room ->
                    ElevatedCard(
                        onClick = { navController.navigate(Screen.Structures.createRoute(room.id)) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(Teal40.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.MeetingRoom, null, tint = Teal40, modifier = Modifier.size(26.dp))
                            }
                            Column(Modifier.weight(1f)) {
                                Text(room.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                if (room.area > 0f) {
                                    val areaText = if (unitSystem == "Imperial") "${(room.area * 10.764f).toInt()} ft²" else "${room.area} m²"
                                    Text(areaText, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                if (room.purpose.isNotEmpty()) {
                                    Text(room.purpose, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            IconButton(onClick = { deleteRoom = room }, modifier = Modifier.size(36.dp)) {
                                Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
        floor?.id?.let { fId ->
            ExtendedFloatingActionButton(
                onClick = { navController.navigate(Screen.AddRoom.createRoute(fId)) },
                icon = { Icon(Icons.Default.Add, null) },
                text = { Text("Add Room") },
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
            )
        }
    }

    deleteRoom?.let { r ->
        DeleteConfirmDialog("Delete Room", "Delete \"${r.name}\"?", onConfirm = { viewModel.deleteRoom(r); deleteRoom = null }, onDismiss = { deleteRoom = null })
    }
}

@Composable
fun AddRoomScreen(navController: NavController, viewModel: BuildingViewModel = koinViewModel()) {
    var name by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    var purpose by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf(false) }
    val suggestions = listOf("Living Room", "Kitchen", "Bedroom", "Bathroom", "Garage", "Storage", "Office", "Hallway")

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionHeader("Room Information")
        OutlinedTextField(
            value = name,
            onValueChange = { name = it; nameError = false },
            label = { Text("Room Name *") },
            leadingIcon = { Icon(Icons.Default.MeetingRoom, null) },
            isError = nameError,
            supportingText = if (nameError) {{ Text("Required") }} else null,
            modifier = Modifier.fillMaxWidth()
        )
        Text("Quick Select:", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            suggestions.take(4).forEach { s ->
                FilterChip(selected = name == s, onClick = { name = s }, label = { Text(s, style = MaterialTheme.typography.labelSmall) })
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            suggestions.drop(4).forEach { s ->
                FilterChip(selected = name == s, onClick = { name = s }, label = { Text(s, style = MaterialTheme.typography.labelSmall) })
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = area,
                onValueChange = { if (it.isEmpty() || it.toFloatOrNull() != null) area = it },
                label = { Text("Area (m²)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = purpose,
                onValueChange = { purpose = it },
                label = { Text("Purpose") },
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = {
                if (name.isBlank()) { nameError = true; return@Button }
                viewModel.addRoom(name.trim(), area.toFloatOrNull() ?: 0f, purpose.trim())
                navController.navigateUp()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Save, null, Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text("Save Room")
        }
    }
}
