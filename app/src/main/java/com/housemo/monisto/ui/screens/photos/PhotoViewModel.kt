package com.housemo.monisto.ui.screens.photos

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.housemo.monisto.data.local.entity.Photo
import com.housemo.monisto.data.repo.PhotoRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PhotoViewModel(
    private val repository: PhotoRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val issueId: Long = savedStateHandle.get<Long>("issueId") ?: -1L

    val photos: StateFlow<List<Photo>> = if (issueId != -1L)
        repository.getPhotosByIssue(issueId).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    else MutableStateFlow(emptyList())

    fun addPhoto(uri: String, location: String, caption: String, iId: Long = issueId) {
        viewModelScope.launch {
            repository.insertPhoto(Photo(issueId = iId, uri = uri, location = location, caption = caption))
        }
    }

    fun deletePhoto(photo: Photo) {
        viewModelScope.launch { repository.deletePhoto(photo) }
    }
}
