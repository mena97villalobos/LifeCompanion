@file:Suppress("MagicNumber")

package com.mena97villalobos.designsystem.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/* ===================================================== */
/* Base Brand Color */
/* ===================================================== */

val BrandPrimary = Color(0xFFC7EA46)

val Black5 = Color.Black.copy(alpha = 0.05f)
val Black10 = Color.Black.copy(alpha = 0.1f)
val Black20 = Color.Black.copy(alpha = 0.2f)
val Black30 = Color.Black.copy(alpha = 0.3f)
val Black40 = Color.Black.copy(alpha = 0.4f)
val Black50 = Color.Black.copy(alpha = 0.5f)
val Black60 = Color.Black.copy(alpha = 0.6f)
val Black70 = Color.Black.copy(alpha = 0.7f)
val Black80 = Color.Black.copy(alpha = 0.8f)
val Black90 = Color.Black.copy(alpha = 0.9f)
val White10 = Color.White.copy(alpha = 0.1f)
val White20 = Color.White.copy(alpha = 0.2f)
val White30 = Color.White.copy(alpha = 0.3f)
val White60 = Color.White.copy(alpha = 0.6f)
val White70 = Color.White.copy(alpha = 0.7f)
val White80 = Color.White.copy(alpha = 0.8f)
val White90 = Color.White.copy(alpha = 0.9f)

val BlueShadow = Color(0xFF0DA9EC)
val Blue50 = Color(0xFFF8F9FC)
val Blue100 = Color(0xFFF2F4F9)
val Blue200 = Color(0xFFDFE5F1)
val Blue300 = Color(0xFFB2BCD7)
val Blue400 = Color(0xFF7484B8)
val Blue450 = Color(0xFF4862A7)
val Blue500 = Color(0xFF425380)
val Blue600 = Color(0XFF253A5B)
val Blue700 = Color(0xFF152649)
val Blue800 = Color(0xFF0F1E39)
val Blue900 = Color(0xFF051121)
val Green200 = Color(0xFFD8EBE2)
val Green300 = Color(0xFFA3D5BD)
val Green400 = Color(0xFF60A483)
val Green450 = Color(0xFF42936C)
val Green450_60 = Green450.copy(alpha = 0.6f)
val Green500 = Color(0xFF456E5A)
val Green500_90 = Green500.copy(alpha = 0.9f)
val Green600 = Color(0xFF345847)
val Green700 = Color(0xFF1F3F2F)
val Green800 = Color(0xFF12241B)
val Green900 = Color(0xFF0B1711)
val Link100 = Color(0xFFD7F3FF)
val Link200 = Color(0xFFAAE5FF)
val Link600 = Color(0xFF0072A3)
val Link700 = Color(0xFF005174)
val Purple200 = Color(0xFFFEE2F1)
val Purple400 = Color(0xFFF086BF)
val Purple500 = Color(0xFFC74D90)
val Purple600 = Color(0xFFA13E74)
val Purple700 = Color(0xFF743457)
val Purple800 = Color(0xFF481C34)
val Purple900 = Color(0xFF321425)
val RedShadow = Color(0xB2EA3E15)
val Red200 = Color(0xFFFEDCD4)
val Red300 = Color(0xFFFBAC99)
val Red400 = Color(0xFFED6443)
val Red450 = Color(0xFFEA3E15)
val Red500 = Color(0xFFB83313)
val Yellow50 = Color(0xFFFFFBF2)
val Yellow200 = Color(0xFFFFEECC)
val Yellow300 = Color(0xFFFFDC99)
val Yellow400 = Color(0xFFFCC353)
val Yellow450 = Color(0xFFEFA515)
val Yellow475 = Color(0xFFDC970F)
val Yellow500 = Color(0xFFCC8804)
val Yellow600 = Color(0xFF855307)
val Yellow900 = Color(0xFF301D01)

// SecondaryColors
val Teal100 = Color(0xFFEEF7F7)
val Teal300 = Color(0xFF9AD0D0)
val Teal400 = Color(0xFF4FAAAA)
val Teal500 = Color(0xFF337777)
val Teal600 = Color(0xFF285959)
val Teal700 = Color(0xFF234949)
val Teal800 = Color(0xFF193333)
val Teal900 = Color(0xFF0F1E1E)

val CoolGrey100 = Color(0xFFEEF3F4)
val CoolGrey200 = Color(0xFFE4ECED)
val CoolGrey300 = Color(0xFFC1CCD3)
val CoolGrey400 = Color(0xFF8D95A1)
val CoolGrey500 = Color(0xFF575F6A)
val CoolGrey600 = Color(0xFF3F494F)
val CoolGrey700 = Color(0xFF283138)
val CoolGrey800 = Color(0xFF182026)
val CoolGrey900 = Color(0xFF0F161B)

val WarmGrey50 = Color(0xFFFAF9F7)
val WarmGrey100 = Color(0xFFF4F2EE)
val WarmGrey200 = Color(0xFFEDE9E3)
val WarmGrey300 = Color(0xFFCAC1B5)
val WarmGrey300_70 = WarmGrey300.copy(alpha = 0.7f)
val WarmGrey400 = Color(0xFF958C7E)
val WarmGrey500 = Color(0xFF6A6357)
val WarmGrey700 = Color(0XFF383128)
val WarmGrey800 = Color(0XFF262018)
val WarmGrey900 = Color(0xFF1B160F)

val Error500 = Red500
val Success300 = Color(0xFF9EE177)
val Success400 = Color(0xFF53C215)
val Success450 = Color(0xFF47AE0C)
val Success500 = Color(0xFF337D09)
val Warning400 = Color(0xFFFCC353)

val MainTextColor = Color.Black
val MainBackgroundColor = WarmGrey200
val DarkBottomSheetColor = WarmGrey100

val PositiveColor = Color(0xFF2E7D32)
val NegativeColor = Color(0xFFC62828)

/* ===================================================== */
/* Light Theme Palette */
/* ===================================================== */

val LightColorScheme = lightColorScheme(

    /* Primary */
    primary = Color(0xFF6F8F00),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE8F7A8),
    onPrimaryContainer = Color(0xFF1F2A00),

    /* Secondary */
    secondary = Color(0xFF5E6E2E),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE2F0B6),
    onSecondaryContainer = Color(0xFF1A1F0A),

    /* Tertiary */
    tertiary = Color(0xFF3F7F5C),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFC1F1D6),
    onTertiaryContainer = Color(0xFF002114),

    /* Background & Surface */
    background = Color(0xFFFFFDF5),
    onBackground = Color(0xFF1B1C18),

    surface = Color(0xFFFFFDF5),
    onSurface = Color(0xFF1B1C18),

    surfaceVariant = Color(0xFFE5E8D8),
    onSurfaceVariant = Color(0xFF45483C),

    /* Inverse */
    inverseSurface = Color(0xFF30312C),
    inverseOnSurface = Color(0xFFF2F1E8),
    inversePrimary = BrandPrimary,

    /* Outline */
    outline = Color(0xFF75786B),
    outlineVariant = Color(0xFFC5C8BA),

    /* Error */
    error = Color(0xFFB3261E),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),

    /* Surface Elevation */
    surfaceDim = Color(0xFFDEDCD3),
    surfaceBright = Color(0xFFFFFDF5),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF8F7ED),
    surfaceContainer = Color(0xFFF2F1E8),
    surfaceContainerHigh = Color(0xFFECEBE2),
    surfaceContainerHighest = Color(0xFFE6E5DC),
)

/* ===================================================== */
/* Dark Theme Palette */
/* ===================================================== */

val DarkColorScheme = darkColorScheme(

    /* Primary */
    primary = BrandPrimary,
    onPrimary = Color(0xFF2F3A00),
    primaryContainer = Color(0xFF4F6600),
    onPrimaryContainer = Color(0xFFE8F7A8),

    /* Secondary */
    secondary = Color(0xFFC6D69A),
    onSecondary = Color(0xFF2D330F),
    secondaryContainer = Color(0xFF454E20),
    onSecondaryContainer = Color(0xFFE2F0B6),

    /* Tertiary */
    tertiary = Color(0xFFA6D9BF),
    onTertiary = Color(0xFF003828),
    tertiaryContainer = Color(0xFF22513F),
    onTertiaryContainer = Color(0xFFC1F1D6),

    /* Background & Surface */
    background = Color(0xFF12130F),
    onBackground = Color(0xFFE4E3DA),

    surface = Color(0xFF12130F),
    onSurface = Color(0xFFE4E3DA),

    surfaceVariant = Color(0xFF45483C),
    onSurfaceVariant = Color(0xFFC5C8BA),

    /* Inverse */
    inverseSurface = Color(0xFFE4E3DA),
    inverseOnSurface = Color(0xFF2F312C),
    inversePrimary = Color(0xFF6F8F00),

    /* Outline */
    outline = Color(0xFF8F9284),
    outlineVariant = Color(0xFF45483C),

    /* Error */
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC),

    /* Surface Elevation */
    surfaceDim = Color(0xFF12130F),
    surfaceBright = Color(0xFF383A34),
    surfaceContainerLowest = Color(0xFF0D0E0B),
    surfaceContainerLow = Color(0xFF1B1C18),
    surfaceContainer = Color(0xFF1F201B),
    surfaceContainerHigh = Color(0xFF292B25),
    surfaceContainerHighest = Color(0xFF34362F),
)
