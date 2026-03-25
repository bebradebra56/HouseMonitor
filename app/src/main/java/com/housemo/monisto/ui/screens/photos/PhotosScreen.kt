package com.housemo.monisto.ui.screens.photos

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.housemo.monisto.data.local.entity.Photo
import com.housemo.monisto.ui.components.DeleteConfirmDialog
import com.housemo.monisto.ui.components.EmptyState
import com.housemo.monisto.ui.navigation.Screen

@Composable
fun PhotosScreen(
    navController: NavController,
    viewModel: PhotoViewModel = koinViewModel()
) {
    val photos by viewModel.photos.collectAsState()
    var deletePhoto by remember { mutableStateOf<Photo?>(null) }
    var selectedPhoto by remember { mutableStateOf<Photo?>(null) }

    val issueId = remember {
        navController.currentBackStackEntry?.arguments?.getLong("issueId") ?: -1L
    }

    Box(Modifier.fillMaxSize()) {
        if (photos.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                EmptyState(Icons.Default.PhotoLibrary, "No photos yet", "Capture photos of issues to document them")
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(photos, key = { it.id }) { photo ->
                    PhotoCard(
                        photo = photo,
                        onClick = { selectedPhoto = photo },
                        onDelete = { deletePhoto = photo }
                    )
                }
            }
        }
        ExtendedFloatingActionButton(
            onClick = { navController.navigate(Screen.AddPhoto.createRoute(issueId)) },
            icon = { Icon(Icons.Default.AddAPhoto, null) },
            text = { Text("Add Photo") },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        )
    }

    selectedPhoto?.let { photo ->
        AlertDialog(
            onDismissRequest = { selectedPhoto = null },
            title = {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(photo.caption.ifEmpty { photo.location }, style = MaterialTheme.typography.titleMedium)
                    IconButton(onClick = { selectedPhoto = null }) { Icon(Icons.Default.Close, null) }
                }
            },
            text = {
                AsyncImage(
                    model = Uri.parse(photo.uri),
                    contentDescription = photo.caption,
                    modifier = Modifier.fillMaxWidth().height(300.dp).clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            },
            confirmButton = {}
        )
    }

    deletePhoto?.let { p ->
        DeleteConfirmDialog("Delete Photo", "Delete this photo?",
            onConfirm = { viewModel.deletePhoto(p); deletePhoto = null },
            onDismiss = { deletePhoto = null })
    }
}

@Composable
fun PhotoCard(photo: Photo, onClick: () -> Unit, onDelete: () -> Unit) {
    var showDelete by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier.fillMaxWidth().aspectRatio(1f).clip(RoundedCornerShape(12.dp)).clickable { onClick() }
    ) {
        AsyncImage(
            model = Uri.parse(photo.uri),
            contentDescription = photo.caption,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            Modifier.fillMaxSize().background(
                androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.3f)
            )
        )
        if (photo.location.isNotEmpty() || photo.caption.isNotEmpty()) {
            Text(
                text = photo.caption.ifEmpty { photo.location },
                modifier = Modifier.align(Alignment.BottomStart).padding(8.dp),
                style = MaterialTheme.typography.labelSmall,
                color = androidx.compose.ui.graphics.Color.White,
                fontWeight = FontWeight.Medium
            )
        }
        IconButton(
            onClick = onDelete,
            modifier = Modifier.align(Alignment.TopEnd).size(36.dp)
        ) {
            Icon(Icons.Default.Delete, null, tint = androidx.compose.ui.graphics.Color.White, modifier = Modifier.size(18.dp))
        }
    }
}
