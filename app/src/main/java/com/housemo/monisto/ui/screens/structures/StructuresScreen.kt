package com.housemo.monisto.ui.screens.structures

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
import com.housemo.monisto.data.local.entity.Structure
import com.housemo.monisto.ui.components.*
import com.housemo.monisto.ui.navigation.Screen
import com.housemo.monisto.ui.screens.buildings.BuildingViewModel
import com.housemo.monisto.ui.theme.Orange40

@Composable
fun StructuresScreen(
    navController: NavController,
    viewModel: BuildingViewModel = koinViewModel()
) {
    val structures by viewModel.structures.collectAsState()
    val room by viewModel.currentRoom.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var deleteStructure by remember { mutableStateOf<Structure?>(null) }

    Box(Modifier.fillMaxSize()) {
        if (structures.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                EmptyState(Icons.Default.Roofing, "No structures yet", "Add structural elements for this room")
            }
        } else {
            LazyColumn(
                Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(structures, key = { it.id }) { structure ->
                    ElevatedCard(
                        onClick = { navController.navigate(Screen.StructureDetails.createRoute(structure.id)) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(Orange40.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(structureIcon(structure.type), null, tint = Orange40, modifier = Modifier.size(26.dp))
                            }
                            Column(Modifier.weight(1f)) {
                                Text(structure.type, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                Text(structure.material, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            ConditionBadge(structure.condition)
                            IconButton(onClick = { deleteStructure = structure }, modifier = Modifier.size(36.dp)) {
                                Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
        ExtendedFloatingActionButton(
            onClick = { showAddDialog = true },
            icon = { Icon(Icons.Default.Add, null) },
            text = { Text("Add Structure") },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        )
    }

    if (showAddDialog) {
        AddStructureDialog(
            onAdd = { type, material, condition, notes ->
                viewModel.addStructure(type, material, condition, notes)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }

    deleteStructure?.let { s ->
        DeleteConfirmDialog("Delete Structure", "Delete ${s.type}?", onConfirm = { viewModel.deleteStructure(s); deleteStructure = null }, onDismiss = { deleteStructure = null })
    }
}

fun structureIcon(type: String) = when (type) {
    "Wall" -> Icons.Default.ViewColumn
    "Ceiling" -> Icons.Default.Roofing
    "Floor" -> Icons.Default.GridOn
    "Roof" -> Icons.Default.Home
    "Foundation" -> Icons.Default.AccountBalance
    "Window" -> Icons.Default.CropPortrait
    "Door" -> Icons.Default.MeetingRoom
    else -> Icons.Default.Construction
}

@Composable
fun AddStructureDialog(onAdd: (String, String, String, String) -> Unit, onDismiss: () -> Unit) {
    var type by remember { mutableStateOf("Wall") }
    var material by remember { mutableStateOf("Concrete") }
    var condition by remember { mutableStateOf("Good") }
    var notes by remember { mutableStateOf("") }

    val types = listOf("Wall", "Ceiling", "Floor", "Roof", "Foundation", "Window", "Door", "Column")
    val materials = listOf("Concrete", "Brick", "Wood", "Steel", "Glass", "Plaster", "Tile", "Stone")
    val conditions = listOf("Excellent", "Good", "Fair", "Poor", "Critical")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Structure") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ExposedDropdownField("Type", type, types) { type = it }
                ExposedDropdownField("Material", material, materials) { material = it }
                ExposedDropdownField("Condition", condition, conditions) { condition = it }
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
            }
        },
        confirmButton = {
            Button(onClick = { onAdd(type, material, condition, notes) }) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropdownField(label: String, value: String, options: List<String>, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true).fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = { onSelect(option); expanded = false }
                )
            }
        }
    }
}
