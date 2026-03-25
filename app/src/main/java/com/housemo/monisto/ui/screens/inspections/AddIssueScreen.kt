package com.housemo.monisto.ui.screens.inspections

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import androidx.navigation.NavController
import com.housemo.monisto.ui.components.SectionHeader
import com.housemo.monisto.ui.screens.structures.ExposedDropdownField

@Composable
fun AddIssueScreen(
    navController: NavController,
    viewModel: InspectionViewModel = koinViewModel()
) {
    var type by remember { mutableStateOf("Crack") }
    var location by remember { mutableStateOf("") }
    var severity by remember { mutableStateOf("Medium") }
    var description by remember { mutableStateOf("") }
    var locationError by remember { mutableStateOf(false) }

    val issueTypes = listOf("Crack", "Leak", "Humidity", "Mold", "Corrosion", "Settlement", "Spalling", "Delamination", "Water Damage", "Structural Damage", "Other")
    val severities = listOf("Low", "Medium", "High", "Critical")
    val locationSuggestions = listOf("North Wall", "South Wall", "East Wall", "West Wall", "Ceiling", "Floor", "Foundation", "Roof", "Window", "Door")

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SectionHeader("Issue Information")
        ExposedDropdownField("Issue Type", type, issueTypes) { type = it }
        OutlinedTextField(
            value = location,
            onValueChange = { location = it; locationError = false },
            label = { Text("Location *") },
            leadingIcon = { Icon(Icons.Default.LocationOn, null) },
            isError = locationError,
            supportingText = if (locationError) {{ Text("Required") }} else null,
            modifier = Modifier.fillMaxWidth()
        )
        Text("Location Suggestions:", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        @OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            locationSuggestions.forEach { s ->
                FilterChip(selected = location == s, onClick = { location = s }, label = { Text(s, style = MaterialTheme.typography.labelSmall) })
            }
        }
        ExposedDropdownField("Severity", severity, severities) { severity = it }
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            leadingIcon = { Icon(Icons.Default.Notes, null) },
            minLines = 3,
            maxLines = 5,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = {
                if (location.isBlank()) { locationError = true; return@Button }
                viewModel.addIssue(type = type, location = location.trim(), severity = severity, description = description.trim())
                navController.navigateUp()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Save, null, Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text("Save Issue")
        }
    }
}
