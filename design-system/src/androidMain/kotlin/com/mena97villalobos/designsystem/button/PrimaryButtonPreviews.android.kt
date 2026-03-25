package com.mena97villalobos.designsystem.button

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mena97villalobos.designsystem.PreviewDesignSystem
import com.mena97villalobos.designsystem.theme.LifeCompanionTheme

@PreviewDesignSystem
@Composable
private fun PreviewPrimaryButton() {
    LifeCompanionTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            PrimaryButton(text = "Large", onClick = { }, modifier = Modifier.fillMaxWidth())
            PrimaryButton(
                text = "Large",
                onClick = { },
                modifier = Modifier.fillMaxWidth(),
                buttonIconInfo = ButtonIconInfo(
                    icon = Icons.Filled.Star,
                    iconColor = Color.White,
                    iconAlignment = IconAlignment.END,
                ),
            )
            PrimaryButton(
                text = "Large",
                onClick = { },
                modifier = Modifier.fillMaxWidth(),
                buttonIconInfo = ButtonIconInfo(
                    icon = Icons.Filled.Star,
                    iconColor = Color.White,
                    iconAlignment = IconAlignment.START,
                ),
            )
            PrimaryButton(text = "Medium", onClick = { }, buttonStyle = ButtonStyle.MEDIUM)
            PrimaryButton(text = "Small", onClick = { }, buttonStyle = ButtonStyle.SMALL)
            PrimaryButton(text = "X Small", onClick = { }, buttonStyle = ButtonStyle.X_SMALL)
        }
    }
}

@PreviewDesignSystem
@Composable
private fun PreviewSecondaryButton() {
    LifeCompanionTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SecondaryButton(text = "Large", onClick = { }, modifier = Modifier.fillMaxWidth())
            SecondaryButton(
                text = "Large",
                onClick = { },
                modifier = Modifier.fillMaxWidth(),
                buttonIconInfo = ButtonIconInfo(
                    icon = Icons.Filled.Star,
                    iconColor = Color.White,
                    iconAlignment = IconAlignment.END,
                ),
            )
            SecondaryButton(
                text = "Large",
                onClick = { },
                modifier = Modifier.fillMaxWidth(),
                buttonIconInfo = ButtonIconInfo(
                    icon = Icons.Filled.Star,
                    iconColor = Color.White,
                    iconAlignment = IconAlignment.START,
                ),
            )
            SecondaryButton(text = "Medium", onClick = { }, buttonStyle = ButtonStyle.MEDIUM)
            SecondaryButton(text = "Small", onClick = { }, buttonStyle = ButtonStyle.SMALL)
            SecondaryButton(text = "X Small", onClick = { }, buttonStyle = ButtonStyle.X_SMALL)
        }
    }
}
