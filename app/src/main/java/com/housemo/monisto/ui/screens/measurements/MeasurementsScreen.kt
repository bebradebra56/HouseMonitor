package com.housemo.monisto.ui.screens.measurements

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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import androidx.navigation.NavController
import com.housemo.monisto.data.local.entity.Measurement
import com.housemo.monisto.ui.components.*
import com.housemo.monisto.ui.screens.inspections.InspectionViewModel
import com.housemo.monisto.ui.screens.structures.ExposedDropdownField
import com.housemo.monisto.util.DateUtils

@Composable
fun MeasurementsScreen(
    navController: NavController,
    viewModel: InspectionViewModel = koinViewModel()
) {
    val measurements by viewModel.measurements.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var deleteMeasurement by remember { mutableStateOf<Measurement?>(null) }

    Box(Modifier.fillMaxSize()) {
        if (measurements.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                EmptyState(Icons.Default.Straighten, "No measurements yet", "Record crack widths, lengths, or other dimensions")
            }
        } else {
            LazyColumn(
                Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text("${measurements.size} measurement${if (measurements.size != 1) "s" else ""}",
                        style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                items(measurements, key = { it.id }) { m ->
                    ElevatedCard(Modifier.fillMaxWidth()) {
                        Row(
                            Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                IconCircle(Icons.Default.Straighten, MaterialTheme.colorScheme.secondary)
                                Column {
                                    Text(m.type, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                    Text(DateUtils.formatDate(m.measuredAt), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    if (m.notes.isNotEmpty()) Text(m.notes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("${m.value}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Text(m.unit, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            IconButton(onClick = { deleteMeasurement = m }, modifier = Modifier.size(36.dp)) {
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
            text = { Text("Add Measurement") },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        )
    }

    if (showAddDialog) {
        AddMeasurementDialog(
            onAdd = { type, value, unit, notes ->
                viewModel.addMeasurement(type, value, unit, notes)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }

    deleteMeasurement?.let { m ->
        DeleteConfirmDialog("Delete Measurement", "Delete this measurement?",
            onConfirm = { viewModel.deleteMeasurement(m); deleteMeasurement = null },
            onDismiss = { deleteMeasurement = null })
    }
}

@Composable
fun AddMeasurementDialog(onAdd: (String, Float, String, String) -> Unit, onDismiss: () -> Unit) {
    var type by remember { mutableStateOf("Width") }
    var value by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("mm") }
    var notes by remember { mutableStateOf("") }
    var valueError by remember { mutableStateOf(false) }

    val types = listOf("Width", "Length", "Depth", "Height", "Diameter", "Area", "Displacement")
    val units = listOf("mm", "cm", "m", "in", "ft", "mm²", "cm²", "m²")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Measurement") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ExposedDropdownField("Type", type, types) { type = it }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = value,
                        onValueChange = { value = it; valueError = false },
                        label = { Text("Value *") },
                        isError = valueError,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                    ExposedDropdownMenuUnit(unit, units) { unit = it }
                }
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = {
                val v = value.toFloatOrNull()
                if (v == null) { valueError = true; return@Button }
                onAdd(type, v, unit, notes)
            }) { Text("Add") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropdownMenuUnit(value: String, options: List<String>, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }, modifier = Modifier.width(90.dp)) {
        OutlinedTextField(
            value = value, onValueChange = {}, label = { Text("Unit") }, readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { opt ->
                DropdownMenuItem(text = { Text(opt) }, onClick = { onSelect(opt); expanded = false })
            }
        }
    }
}
