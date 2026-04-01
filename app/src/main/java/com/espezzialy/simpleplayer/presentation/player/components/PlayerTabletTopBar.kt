package com.espezzialy.simpleplayer.presentation.player.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.espezzialy.simpleplayer.R
import com.espezzialy.simpleplayer.presentation.common.components.TabletBackIconButton
import com.espezzialy.simpleplayer.presentation.common.components.TabletNavBarPaddingTop
import com.espezzialy.simpleplayer.ui.theme.SimplePlayerDimens

@Composable
fun PlayerTabletTopBar(
    onBack: () -> Unit,
    title: String,
    onOverflowClick: (() -> Unit)? = null
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = TabletNavBarPaddingTop),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TabletBackIconButton(
            onClick = onBack,
            contentDescription = stringResource(R.string.content_desc_back),
            tint = colorScheme.onBackground,
            painter = painterResource(R.drawable.ic_arrow_left),
            iconSize = 28.dp
        )
        Spacer(modifier = Modifier.width(SimplePlayerDimens.Player.topBarTitleInsetAfterBackTablet))
        Text(
            text = title,
            style = typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
            color = colorScheme.onBackground,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Start
        )
        if (onOverflowClick != null) {
            IconButton(onClick = onOverflowClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_more),
                    contentDescription = stringResource(R.string.content_desc_menu),
                    tint = colorScheme.onSurfaceVariant
                )
            }
        } else {
            Spacer(modifier = Modifier.width(SimplePlayerDimens.Player.tabletOverflowPlaceholderWidth))
        }
    }
}
