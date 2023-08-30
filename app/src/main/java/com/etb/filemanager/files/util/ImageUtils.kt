package com.etb.filemanager.files.util

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.etb.filemanager.files.app.contentResolver
import com.etb.filemanager.files.provider.archive.common.mime.MimeType
import com.etb.filemanager.files.provider.archive.common.mime.isASpecificTypeOfMime
import com.etb.filemanager.files.provider.archive.common.mime.isMedia
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

fun getImagesInDirectory(directoryPath: String): List<String> {
    val images = mutableListOf<String>()

    val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    val projection = arrayOf(
        MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA
    )
    val selection = "${MediaStore.Images.Media.DATA} like ?"
    val selectionArgs = arrayOf("$directoryPath%")
    val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

    val cursor = contentResolver.query(
        uri, projection, selection, selectionArgs, sortOrder
    )

    cursor?.use {
        val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        while (it.moveToNext()) {
            val id = it.getLong(idColumn)
            val imageUri = ContentUris.withAppendedId(uri, id).toString()
            images.add(imageUri)
        }
    }

    return images
}

fun getMediaURIsFromDirectory(path: Path): List<Uri> {
    val mediaURIs = mutableListOf<Uri>()
    val directory = path.toFile()

    var contents = directory.listFiles()?.toList() ?: emptyList()
    contents = contents.sortedBy { it.name }

    contents.forEach { file ->
        val uri = Paths.get(file.absolutePath).fileProviderUri

        val mime = FileUtil().getMimeType(uri, null)!!
        if (MimeType(mime).isMedia()) {
            mediaURIs.add(uri)
        }
    }

    return mediaURIs
}

fun Context.getMediaIdFromPath(mediaPath: String): Long? {
    val context = this
    val projection = arrayOf(MediaStore.Images.Media._ID)
    val selection = "${MediaStore.MediaColumns.DATA} = ?"
    val selectionArgs = arrayOf(mediaPath)
    val mime = FileUtil().getMimeType(null, mediaPath)
    val queryUri = if (mediaPath.endsWith(".mp4")) {
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    } else {
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    }

    context.contentResolver.query(
        queryUri, projection, selection, selectionArgs, null
    )?.use { cursor ->
        if (cursor.moveToFirst()) {
            val idColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            return cursor.getLong(idColumnIndex)
        }
    }

    return null
}

suspend fun getMediaIdFromPath(mediaPath: String, context: Context): Long? =
    withContext(Dispatchers.IO) {
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val selection = "${MediaStore.MediaColumns.DATA} = ?"
        val selectionArgs = arrayOf(mediaPath)
        val mime = FileUtil().getMimeType(null, mediaPath) ?: return@withContext null

        val queryUri = if (MimeType(mime).isASpecificTypeOfMime(MimeType.VIDEO_MP4)) {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        context.contentResolver.query(
            queryUri, projection, selection, selectionArgs, null
        )?.use { cursor ->

            while (cursor.moveToFirst()) {
                val idColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                return@withContext cursor.getLong(idColumnIndex)
            }

        }

        return@withContext null
    }

fun getMediaIdFromFile(file: File) {
    val path = file.toPath()
}