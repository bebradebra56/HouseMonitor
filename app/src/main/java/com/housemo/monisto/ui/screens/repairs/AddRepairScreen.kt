package com.housemo.monisto.ui.screens.repairs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import androidx.navigation.NavController
import com.housemo.monisto.data.prefs.PreferencesManager
import com.housemo.monisto.ui.components.DatePickerField
import com.housemo.monisto.ui.components.SectionHeader
import com.housemo.monisto.ui.screens.structures.ExposedDropdownField

@Composable
fun AddRepairScreen(
    navController: NavController,
    viewModel: RepairViewModel = koinViewModel()
) {
    val prefs: PreferencesManager = koinInject()
    val currency by prefs.defaultCurrency.collectAsState(initial = "USD")
    var type by remember { mutableStateOf("Structural Repair") }
    var description by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("Medium") }
    var scheduledDateMillis by remember { mutableStateOf<Long?>(null) }
    var contractor by remember { mutableStateOf("") }

    val issueId = remember {
        navController.currentBackStackEntry?.arguments?.getLong("issueId") ?: -1L
    }

    val repairTypes = listOf("Structural Repair", "Waterproofing", "Crack Sealing", "Painting", "Plumbing", "Electrical", "HVAC", "Roofing", "Foundation Work", "Insulation", "Other")
    val priorities = listOf("Low", "Medium", "High", "Critical")

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SectionHeader("Repair Information")

        ExposedDropdownField("Repair Type", type, repairTypes) { type = it }
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            leadingIcon = { Icon(Icons.Default.Notes, null) },
            minLines = 2, maxLines = 4,
            modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = cost,
                onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) cost = it },
                label = { Text("Est. Cost ($currency)") },
                leadingIcon = { Icon(Icons.Default.AttachMoney, null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f)
            )
            Column(Modifier.weight(1f)) {
                ExposedDropdownField("Priority", priority, priorities) { priority = it }
            }
        }
        DatePickerField(
            label = "Scheduled Date (optional)",
            selectedDateMillis = scheduledDateMillis,
            onDateSelected = { scheduledDateMillis = it },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = contractor,
            onValueChange = { contractor = it },
            label = { Text("Contractor") },
            leadingIcon = { Icon(Icons.Default.Person, null) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = {
                viewModel.addRepair(
                    type = type,
                    description = description.trim(),
                    cost = cost.toDoubleOrNull() ?: 0.0,
                    priority = priority,
                    scheduledDate = scheduledDateMillis,
                    contractor = contractor.trim(),
                    iId = issueId
                )
                navController.navigateUp()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Save, null, Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text("Schedule Repair")
        }
    }
}
