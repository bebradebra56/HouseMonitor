package com.housemo.monisto.ui.screens.materials

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
import androidx.navigation.NavController
import com.housemo.monisto.data.local.entity.Material
import com.housemo.monisto.ui.components.*
import com.housemo.monisto.ui.screens.repairs.RepairViewModel
import com.housemo.monisto.ui.screens.structures.ExposedDropdownField
import com.housemo.monisto.ui.theme.Orange40

@Composable
fun MaterialsScreen(
    navController: NavController,
    viewModel: RepairViewModel = koinViewModel()
) {
    val materials by viewModel.allMaterials.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var deleteMaterial by remember { mutableStateOf<Material?>(null) }

    Box(Modifier.fillMaxSize()) {
        if (materials.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                EmptyState(Icons.Default.Inventory, "No materials yet", "Track your construction and repair materials")
            }
        } else {
            LazyColumn(
                Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val grouped = materials.groupBy { it.type }
                grouped.forEach { (type, mats) ->
                    item { SectionHeader(type) }
                    items(mats, key = { it.id }) { material ->
                        MaterialCard(material = material, onDelete = { deleteMaterial = material })
                    }
                }
            }
        }
        ExtendedFloatingActionButton(
            onClick = { showAddDialog = true },
            icon = { Icon(Icons.Default.Add, null) },
            text = { Text("Add Material") },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        )
    }

    if (showAddDialog) {
        AddMaterialDialog(
            onAdd = { name, type, desc, qty, unit, supplier ->
                viewModel.addMaterial(name, type, desc, qty, unit, supplier)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }

    deleteMaterial?.let { m ->
        DeleteConfirmDialog("Delete Material", "Delete \"${m.name}\"?",
            onConfirm = { viewModel.deleteMaterial(m); deleteMaterial = null },
            onDismiss = { deleteMaterial = null })
    }
}

@Composable
fun MaterialCard(material: Material, onDelete: () -> Unit) {
    ElevatedCard(Modifier.fillMaxWidth()) {
        Row(
            Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(Orange40.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Inventory, null, tint = Orange40, modifier = Modifier.size(24.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(material.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text(material.type, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (material.supplier.isNotEmpty()) Text("From: ${material.supplier}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (material.quantity > 0f) {
                Column(horizontalAlignment = Alignment.End) {
                    Text("${material.quantity}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text(material.unit, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
fun AddMaterialDialog(onAdd: (String, String, String, Float, String, String) -> Unit, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Concrete") }
    var description by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("kg") }
    var supplier by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf(false) }

    val types = listOf("Concrete", "Brick", "Wood", "Steel", "Glass", "Plaster", "Tile", "Stone", "Paint", "Insulation", "Other")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Material") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it; nameError = false }, label = { Text("Name *") }, isError = nameError, modifier = Modifier.fillMaxWidth())
                ExposedDropdownField("Type", type, types) { type = it }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = quantity, onValueChange = { quantity = it }, label = { Text("Qty") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.weight(1f))
                    OutlinedTextField(value = unit, onValueChange = { unit = it }, label = { Text("Unit") }, modifier = Modifier.weight(1f))
                }
                OutlinedTextField(value = supplier, onValueChange = { supplier = it }, label = { Text("Supplier") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = {
                if (name.isBlank()) { nameError = true; return@Button }
                onAdd(name.trim(), type, description.trim(), quantity.toFloatOrNull() ?: 0f, unit.ifEmpty { "pcs" }, supplier.trim())
            }) { Text("Add") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
