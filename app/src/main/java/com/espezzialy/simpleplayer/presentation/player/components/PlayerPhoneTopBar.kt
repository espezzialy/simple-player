package com.espezzialy.simpleplayer.presentation.player.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.espezzialy.simpleplayer.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerPhoneTopBar(
    onBack: () -> Unit,
    title: String,
    onOverflowClick: () -> Unit,
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    TopAppBar(
        title = {
            Text(
                text = title,
                style = typography.titleLarge,
                color = colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_left),
                    contentDescription = stringResource(R.string.content_desc_back),
                    tint = colorScheme.onBackground,
                )
            }
        },
        actions = {
            IconButton(onClick = onOverflowClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_more),
                    contentDescription = stringResource(R.string.content_desc_menu),
                    tint = colorScheme.onSurface,
                )
            }
        },
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = colorScheme.background,
                titleContentColor = colorScheme.onBackground,
                navigationIconContentColor = colorScheme.onBackground,
            ),
    )
}
