package com.housemo.monisto.ui.screens.structures

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import androidx.navigation.NavController
import com.housemo.monisto.ui.components.*
import com.housemo.monisto.ui.screens.buildings.BuildingViewModel

@Composable
fun StructureDetailsScreen(
    navController: NavController,
    viewModel: BuildingViewModel = koinViewModel()
) {
    val structure by viewModel.currentStructure.collectAsState()
    var editing by remember { mutableStateOf(false) }

    val s = structure ?: run {
        Box(Modifier.fillMaxSize()) { CircularProgressIndicator() }
        return
    }

    var type by remember(s) { mutableStateOf(s.type) }
    var material by remember(s) { mutableStateOf(s.material) }
    var condition by remember(s) { mutableStateOf(s.condition) }
    var notes by remember(s) { mutableStateOf(s.notes) }

    val types = listOf("Wall", "Ceiling", "Floor", "Roof", "Foundation", "Window", "Door", "Column")
    val materials = listOf("Concrete", "Brick", "Wood", "Steel", "Glass", "Plaster", "Tile", "Stone")
    val conditions = listOf("Excellent", "Good", "Fair", "Poor", "Critical")

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        ElevatedCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    IconCircle(structureIcon(s.type), MaterialTheme.colorScheme.primary, 56)
                    Column {
                        Text(s.type, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        ConditionBadge(s.condition)
                    }
                }
                HorizontalDivider()
                InfoRow("Material", s.material)
                InfoRow("Condition", s.condition)
                if (s.notes.isNotEmpty()) InfoRow("Notes", s.notes)
            }
        }

        if (editing) {
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Edit Structure", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    ExposedDropdownField("Type", type, types) { type = it }
                    ExposedDropdownField("Material", material, materials) { material = it }
                    ExposedDropdownField("Condition", condition, conditions) { condition = it }
                    OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { editing = false }, modifier = Modifier.weight(1f)) { Text("Cancel") }
                        Button(
                            onClick = {
                                viewModel.updateStructure(s.copy(type = type, material = material, condition = condition, notes = notes))
                                editing = false
                            },
                            modifier = Modifier.weight(1f)
                        ) { Text("Save") }
                    }
                }
            }
        } else {
            FilledTonalButton(
                onClick = { editing = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Edit, null, Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Edit Structure")
            }
        }
    }
}
