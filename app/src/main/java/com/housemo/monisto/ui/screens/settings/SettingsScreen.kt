package com.housemo.monisto.ui.screens.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import androidx.navigation.NavController
import com.housemo.monisto.ui.components.SectionHeader
import com.housemo.monisto.ui.navigation.Screen

@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val unitSystem by viewModel.unitSystem.collectAsState()
    val defaultCurrency by viewModel.defaultCurrency.collectAsState()

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState())
    ) {
        SectionHeader("Measurements")
        SettingsDropdownRow(
            icon = Icons.Default.Straighten,
            title = "Unit System",
            subtitle = "Choose between Metric and Imperial",
            value = unitSystem,
            options = listOf("Metric", "Imperial"),
            onSelect = { viewModel.setUnitSystem(it) }
        )
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        SectionHeader("Currency")
        SettingsDropdownRow(
            icon = Icons.Default.AttachMoney,
            title = "Default Currency",
            subtitle = "Used for repair cost estimates",
            value = defaultCurrency,
            options = listOf("USD", "EUR", "GBP", "CNY", "JPY", "CAD", "AUD"),
            onSelect = { viewModel.setDefaultCurrency(it) }
        )
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        SectionHeader("Data")
        SettingsNavRow(
            icon = Icons.Default.History,
            title = "Activity History",
            subtitle = "View all recent actions",
            onClick = { navController.navigate(Screen.ActivityHistory.route) }
        )
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        SectionHeader("About")
        SettingsNavRow(
            icon = Icons.Default.Policy,
            title = "Privacy Policy",
            subtitle = "Tap to read",
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://housemoniitor.com/privacy-policy.html"))
                context.startActivity(intent)
            }
        )

        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        ElevatedCard(Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("House Monitor", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Building condition & repair management", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Version 1.0.0", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Spacer(Modifier.height(32.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDropdownRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    value: String,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ListItem(
        headlineContent = { Text(title, fontWeight = FontWeight.Medium) },
        supportingContent = { Text(subtitle, style = MaterialTheme.typography.bodySmall) },
        leadingContent = { Icon(icon, null, tint = MaterialTheme.colorScheme.primary) },
        trailingContent = {
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                FilterChip(
                    selected = true,
                    onClick = { expanded = true },
                    label = { Text(value) },
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
    )
}

@Composable
fun SettingsNavRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        ListItem(
            headlineContent = { Text(title, fontWeight = FontWeight.Medium) },
            supportingContent = { Text(subtitle, style = MaterialTheme.typography.bodySmall) },
            leadingContent = { Icon(icon, null, tint = MaterialTheme.colorScheme.primary) },
            trailingContent = { Icon(Icons.Default.ChevronRight, null) }
        )
    }
}
