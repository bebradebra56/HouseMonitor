package com.housemo.monisto.ui.screens.photos

import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import org.koin.androidx.compose.koinViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.housemo.monisto.ui.components.SectionHeader
import java.io.File

@Composable
fun AddPhotoScreen(
    navController: NavController,
    viewModel: PhotoViewModel = koinViewModel()
) {
    val context = LocalContext.current
    var capturedUri by remember { mutableStateOf<Uri?>(null) }
    var location by remember { mutableStateOf("") }
    var caption by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    val issueId = remember {
        navController.currentBackStackEntry?.arguments?.getLong("issueId") ?: -1L
    }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            capturedUri = photoUri
        }
    }

    fun launchCamera() {
        val picturesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File(picturesDir, "IMG_${System.currentTimeMillis()}.jpg")
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", imageFile)
        photoUri = uri
        takePictureLauncher.launch(uri)
    }

    Column(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SectionHeader("Capture Photo")

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (capturedUri != null) {
                AsyncImage(
                    model = capturedUri,
                    contentDescription = "Captured photo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.PhotoCamera,
                        null,
                        modifier = Modifier.size(56.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "No photo taken",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Button(
            onClick = { launchCamera() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Icon(Icons.Default.PhotoCamera, null, Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text(if (capturedUri == null) "Take Photo" else "Retake Photo")
        }

        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Location / Area") },
            leadingIcon = { Icon(Icons.Default.LocationOn, null) },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = caption,
            onValueChange = { caption = it },
            label = { Text("Caption / Description") },
            leadingIcon = { Icon(Icons.Default.Notes, null) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.weight(1f))

        Button(
            onClick = {
                capturedUri?.let { uri ->
                    viewModel.addPhoto(uri = uri.toString(), location = location.trim(), caption = caption.trim(), iId = issueId)
                    navController.navigateUp()
                }
            },
            enabled = capturedUri != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Save, null, Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text("Save Photo")
        }
    }
}
