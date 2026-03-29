package com.espezzialy.simpleplayer.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.espezzialy.simpleplayer.R

/**
 * Articulat CF (Connary Fagen). Os ficheiros em `res/font/` vêm do pacote **demo** oficial:
 * https://connary.com/fontdemos/Demo-Articulat%20CF.zip — adequado para desenvolvimento.
 * Para publicação comercial na Play Store, adquira licença em https://connary.com/articulat.html
 */
val ArticulatCfFamily: FontFamily = FontFamily(
    Font(R.font.articulat_cf_regular, FontWeight.Normal),
    Font(R.font.articulat_cf_medium, FontWeight.Medium),
    Font(R.font.articulat_cf_semibold, FontWeight.SemiBold),
    Font(R.font.articulat_cf_bold, FontWeight.Bold)
)

private fun TextStyle.withArticulat(): TextStyle = copy(fontFamily = ArticulatCfFamily)

/**
 * Material 3 typography com Articulat CF: escala Material completa + tamanhos já usados no Simple Player.
 */
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
