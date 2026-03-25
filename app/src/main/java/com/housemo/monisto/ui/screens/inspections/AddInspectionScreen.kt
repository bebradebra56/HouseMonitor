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
import com.housemo.monisto.ui.components.DatePickerField
import com.housemo.monisto.ui.components.SectionHeader
import com.housemo.monisto.ui.screens.structures.ExposedDropdownField

@Composable
fun AddInspectionScreen(
    navController: NavController,
    viewModel: InspectionViewModel = koinViewModel()
) {
    var inspector by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var notes by remember { mutableStateOf("") }
    var overallCondition by remember { mutableStateOf("Good") }
    var inspectorError by remember { mutableStateOf(false) }

    val conditions = listOf("Excellent", "Good", "Fair", "Poor", "Critical")

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SectionHeader("Inspection Details")

        OutlinedTextField(
            value = inspector,
            onValueChange = { inspector = it; inspectorError = false },
            label = { Text("Inspector Name *") },
            leadingIcon = { Icon(Icons.Default.Person, null) },
            isError = inspectorError,
            supportingText = if (inspectorError) {{ Text("Required") }} else null,
            modifier = Modifier.fillMaxWidth()
        )
        DatePickerField(
            label = "Inspection Date",
            selectedDateMillis = selectedDate,
            onDateSelected = { selectedDate = it },
            modifier = Modifier.fillMaxWidth()
        )
        ExposedDropdownField("Overall Condition", overallCondition, conditions) { overallCondition = it }
        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes") },
            leadingIcon = { Icon(Icons.Default.Notes, null) },
            minLines = 3,
            maxLines = 6,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = {
                if (inspector.isBlank()) { inspectorError = true; return@Button }
                viewModel.addInspection(date = selectedDate, inspector = inspector.trim(), notes = notes.trim(), overallCondition = overallCondition)
                navController.navigateUp()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Save, null, Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text("Save Inspection")
        }
    }
}
