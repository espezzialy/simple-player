package com.espezzialy.simpleplayer.presentation.album.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.espezzialy.simpleplayer.R
import com.espezzialy.simpleplayer.presentation.common.components.TabletBackIconButton
import com.espezzialy.simpleplayer.presentation.common.components.TabletNavBarPaddingTop
import com.espezzialy.simpleplayer.ui.theme.AlbumTabletNavTitleTextStyle
import com.espezzialy.simpleplayer.ui.theme.SimplePlayerDimens

@Composable
fun AlbumTabletTopBar(
    onBack: () -> Unit,
    title: String
) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(
                start = SimplePlayerDimens.Album.tabletNavPaddingStart,
                top = TabletNavBarPaddingTop
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TabletBackIconButton(
            onClick = onBack,
            contentDescription = stringResource(R.string.content_desc_back),
            tint = colorScheme.onBackground,
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            iconSize = 28.dp
        )
        Spacer(modifier = Modifier.width(SimplePlayerDimens.Album.tabletNavIconToTitle))
        Text(
            text = title,
            style = AlbumTabletNavTitleTextStyle,
            color = colorScheme.onBackground
        )
    }
}
