package com.housemo.monisto.ui.screens.buildings

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
import androidx.navigation.NavController
import com.housemo.monisto.ui.components.SectionHeader

@Composable
fun AddBuildingScreen(
    navController: NavController,
    viewModel: BuildingViewModel = koinViewModel()
) {
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var floorsCount by remember { mutableStateOf("1") }
    var yearBuilt by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SectionHeader("Building Information")

        OutlinedTextField(
            value = name,
            onValueChange = { name = it; nameError = false },
            label = { Text("Building Name *") },
            leadingIcon = { Icon(Icons.Default.Home, null) },
            isError = nameError,
            supportingText = if (nameError) {{ Text("Name is required") }} else null,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Address") },
            leadingIcon = { Icon(Icons.Default.LocationOn, null) },
            modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = floorsCount,
                onValueChange = { if (it.all { c -> c.isDigit() }) floorsCount = it },
                label = { Text("Number of Floors") },
                leadingIcon = { Icon(Icons.Default.Layers, null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = yearBuilt,
                onValueChange = { if (it.all { c -> c.isDigit() } && it.length <= 4) yearBuilt = it },
                label = { Text("Year Built") },
                leadingIcon = { Icon(Icons.Default.CalendarMonth, null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }
        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes") },
            leadingIcon = { Icon(Icons.Default.Notes, null) },
            minLines = 3,
            maxLines = 5,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))
        Button(
            onClick = {
                if (name.isBlank()) { nameError = true; return@Button }
                viewModel.addBuilding(
                    name = name.trim(),
                    address = address.trim(),
                    floorsCount = floorsCount.toIntOrNull() ?: 1,
                    yearBuilt = yearBuilt.toIntOrNull() ?: 0,
                    notes = notes.trim()
                )
                navController.navigateUp()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Save, null, Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text("Save Building")
        }
    }
}
