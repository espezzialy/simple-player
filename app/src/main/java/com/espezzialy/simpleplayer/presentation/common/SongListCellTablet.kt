package com.espezzialy.simpleplayer.presentation.common

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.espezzialy.simpleplayer.ui.theme.ArticulatCfFamily

const val SongListCellTabletMinWidthDp = 600

val SongListCellArtworkSizePhone = 64.dp
val SongListCellArtworkSizeTablet = 78.dp

val SongListCellTitleStyleTablet = TextStyle(
    fontFamily = ArticulatCfFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 24.sp,
    lineHeight = 28.8.sp
)

val SongListCellArtistStyleTablet = TextStyle(
    fontFamily = ArticulatCfFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 18.sp,
    lineHeight = 25.2.sp
)

val SongListCellArtistColorTablet = Color(0xFF737373)
