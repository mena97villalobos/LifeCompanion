package com.mena97villalobos.designsystem.imagepicker

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

@Composable
actual fun ImagePicker(
    onSelectImage: (String) -> Unit,
    modifier: Modifier,
) {
    val context = LocalContext.current
    val cameraLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.TakePicturePreview(),
        ) { bitmap ->
            bitmap?.let {
                val uri = saveBitmapToCache(context, it)
                onSelectImage(uri.toString())
            }
        }
    val galleryLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent(),
        ) { uri ->
            uri?.let { onSelectImage(it.toString()) }
        }

    ImagePickerActions(
        onCameraClick = { cameraLauncher.launch(null) },
        onGalleryClick = { galleryLauncher.launch("image/*") },
        modifier = modifier,
    )
}

fun saveBitmapToCache(
    context: Context,
    bitmap: Bitmap,
): Uri {
    val file = File(
        context.cacheDir,
        "warranty_${UUID.randomUUID()}.jpg",
    )

    FileOutputStream(file).use { stream ->
        bitmap.compress(
            Bitmap.CompressFormat.JPEG,
            100,
            stream,
        )
    }

    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file,
    )
}
