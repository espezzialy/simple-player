package com.espezzialy.simpleplayer.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.espezzialy.simpleplayer.R

/**
 * Articulat CF (Connary Fagen). Fonts under `res/font/` are from the official **demo** package:
 * https://connary.com/fontdemos/Demo-Articulat%20CF.zip — fine for development.
 * For Play Store release, purchase a license at https://connary.com/articulat.html
 */
val ArticulatCfFamily: FontFamily = FontFamily(
    Font(R.font.articulat_cf_regular, FontWeight.Normal),
    Font(R.font.articulat_cf_medium, FontWeight.Medium),
    Font(R.font.articulat_cf_semibold, FontWeight.SemiBold),
    Font(R.font.articulat_cf_bold, FontWeight.Bold)
)

private fun TextStyle.withArticulat(): TextStyle = copy(fontFamily = ArticulatCfFamily)

val Typography: Typography = run {
    val base = Typography()
    Typography(
        displayLarge = base.displayLarge.withArticulat(),
        displayMedium = base.displayMedium.withArticulat(),
        displaySmall = TextStyle(
            fontFamily = ArticulatCfFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 34.sp,
            lineHeight = 40.sp,
            letterSpacing = 0.sp
        ),
        headlineLarge = base.headlineLarge.withArticulat(),
        headlineMedium = base.headlineMedium.withArticulat(),
        headlineSmall = TextStyle(
            fontFamily = ArticulatCfFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            lineHeight = 28.sp,
            letterSpacing = 0.sp
        ),
        titleLarge = TextStyle(
            fontFamily = ArticulatCfFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.sp
        ),
        titleMedium = TextStyle(
            fontFamily = ArticulatCfFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 17.sp,
            lineHeight = 22.sp,
            letterSpacing = 0.sp
        ),
        titleSmall = base.titleSmall.withArticulat(),
        bodyLarge = TextStyle(
            fontFamily = ArticulatCfFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 17.sp,
            lineHeight = 22.sp,
            letterSpacing = 0.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = ArticulatCfFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 15.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.sp
        ),
        bodySmall = TextStyle(
            fontFamily = ArticulatCfFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 18.sp,
            letterSpacing = 0.sp
        ),
        labelLarge = base.labelLarge.withArticulat(),
        labelMedium = base.labelMedium.withArticulat(),
        labelSmall = TextStyle(
            fontFamily = ArticulatCfFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.sp
        )
    )
}

object AlbumPhoneHeroTextStyles {
    val title = TextStyle(
        fontFamily = ArticulatCfFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 24.sp,
        textAlign = TextAlign.Center
    )
    val artist = TextStyle(
        fontFamily = ArticulatCfFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 16.8.sp,
        textAlign = TextAlign.Center
    )
}

val AlbumTabletNavTitleTextStyle: TextStyle = TextStyle(
    fontFamily = ArticulatCfFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 18.sp,
    lineHeight = 19.44.sp
)

object AlbumTabletHeroTextStyles {
    val title = TextStyle(
        fontFamily = ArticulatCfFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 56.sp,
        lineHeight = 67.2.sp
    )
    val artist = TextStyle(
        fontFamily = ArticulatCfFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 28.sp,
        lineHeight = 33.6.sp
    )
}

object PlayerNowPlayingTextStyles {
    val trackPhone = TextStyle(
        fontFamily = ArticulatCfFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 38.4.sp
    )
    val trackTablet = TextStyle(
        fontFamily = ArticulatCfFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 40.sp,
        lineHeight = 48.sp
    )
    val artistPhone = TextStyle(
        fontFamily = ArticulatCfFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 19.2.sp
    )
    val artistTablet = TextStyle(
        fontFamily = ArticulatCfFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        lineHeight = 24.sp
    )
}
