package com.mena97villalobos.designsystem.core

import androidx.compose.ui.Modifier

inline fun Modifier.conditional(condition: Boolean, modifier: Modifier.() -> Modifier): Modifier = if (condition) {
    then(modifier(Modifier))
} else {
    this
}
