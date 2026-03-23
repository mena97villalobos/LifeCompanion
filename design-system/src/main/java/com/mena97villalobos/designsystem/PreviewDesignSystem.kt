package com.mena97villalobos.designsystem

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview

@Preview(name = "Light", showBackground = true, uiMode = UI_MODE_NIGHT_NO, backgroundColor = 0xFFFFFFFF)
@Preview(name = "Dark", showBackground = true, uiMode = UI_MODE_NIGHT_YES, backgroundColor = 0xFFD0D0D0)
annotation class PreviewDesignSystem
