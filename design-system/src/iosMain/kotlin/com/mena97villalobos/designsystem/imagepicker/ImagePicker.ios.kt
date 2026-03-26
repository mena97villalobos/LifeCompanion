package com.mena97villalobos.designsystem.imagepicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.uikit.LocalUIViewController
import kotlinx.cinterop.ExperimentalForeignApi
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerFilter
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol
import platform.darwin.NSObject

/**
 * Presents a PHPicker on iOS and returns a `file://` URL string for the selected image.
 */
@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun ImagePicker(
    onSelectImage: (String) -> Unit,
    modifier: Modifier,
) {
    val parentController = LocalUIViewController.current

    val delegate = remember {
        object : NSObject(), PHPickerViewControllerDelegateProtocol {
            override fun picker(
                picker: PHPickerViewController,
                didFinishPicking: List<*>,
            ) {
                picker.dismissViewControllerAnimated(true, null)
                val result =
                    didFinishPicking.firstOrNull() as? platform.PhotosUI.PHPickerResult ?: return
                val itemProvider = result.itemProvider
                itemProvider.loadFileRepresentationForTypeIdentifier(
                    typeIdentifier = "public.image",
                ) { url, error ->
                    if (url != null) {
                        val path = url.absoluteString ?: url.path
                        ?: return@loadFileRepresentationForTypeIdentifier
                        onSelectImage(path)
                    }
                }
            }
        }
    }

    ImagePickerActions(
        onCameraClick = null,
        onGalleryClick = {
            val config = PHPickerConfiguration()
            config.filter = PHPickerFilter.imagesFilter
            config.selectionLimit = 1
            val picker = PHPickerViewController(config)
            picker.delegate = delegate
            parentController.presentViewController(picker, animated = true, completion = null)
        },
        modifier = modifier,
    )
}
