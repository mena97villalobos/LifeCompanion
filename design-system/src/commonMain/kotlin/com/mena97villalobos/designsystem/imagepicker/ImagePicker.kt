package com.mena97villalobos.designsystem.imagepicker

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Platform image picker (camera / gallery). Invokes [onSelectImage] with a URI string suitable for
 * [com.mena97villalobos.domain.usecases.UploadWarrantyImageUseCase] on each platform.
 */
@Composable
expect fun ImagePicker(
    onSelectImage: (String) -> Unit,
    modifier: Modifier = Modifier,
)

@Composable
internal fun ImagePickerActions(
    onCameraClick: (() -> Unit)?,
    onGalleryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        Button(
            onClick = { onCameraClick?.invoke() },
            enabled = onCameraClick != null,
        ) {
            Text("Camera")
        }
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = onGalleryClick) {
            Text("Gallery")
        }
    }
}
