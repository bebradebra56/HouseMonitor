package com.housemo.monisto.data.repo

import com.housemo.monisto.data.local.dao.PhotoDao
import com.housemo.monisto.data.local.entity.Photo
import kotlinx.coroutines.flow.Flow

class PhotoRepository(
    private val photoDao: PhotoDao
) {
    fun getPhotosByIssue(issueId: Long): Flow<List<Photo>> = photoDao.getPhotosByIssue(issueId)
    fun getPhotoById(id: Long): Flow<Photo?> = photoDao.getPhotoById(id)
    fun getPhotoCount(): Flow<Int> = photoDao.getPhotoCount()
    suspend fun insertPhoto(photo: Photo): Long = photoDao.insertPhoto(photo)
    suspend fun deletePhoto(photo: Photo) = photoDao.deletePhoto(photo)
}
