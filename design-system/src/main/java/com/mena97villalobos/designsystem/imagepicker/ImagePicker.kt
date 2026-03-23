package com.mena97villalobos.designsystem.imagepicker

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

@Composable
fun ImagePicker(
    onSelectImage: (Uri) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val cameraLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.TakePicturePreview(),
        ) { bitmap ->
            bitmap?.let {
                val uri = saveBitmapToCache(context, it)
                onSelectImage(uri)
            }
        }
    val galleryLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent(),
        ) { uri ->
            uri?.let { onSelectImage(it) }
        }

    Row(modifier = modifier) {
        Button(onClick = { cameraLauncher.launch(null) }) {
            Text("Camera")
        }

        Spacer(modifier = Modifier.width(8.dp))

        Button(onClick = { galleryLauncher.launch("image/*") }) {
            Text("Gallery")
        }
    }
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
