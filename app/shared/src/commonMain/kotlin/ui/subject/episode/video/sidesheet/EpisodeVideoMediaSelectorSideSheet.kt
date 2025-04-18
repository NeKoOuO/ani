/*
 * Copyright (C) 2024-2025 OpenAni and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license, which can be found at the following link.
 *
 * https://github.com/open-ani/ani/blob/main/LICENSE
 */

package me.him188.ani.app.ui.subject.episode.video.sidesheet

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import me.him188.ani.app.ui.mediafetch.MediaSelectorState
import me.him188.ani.app.ui.mediafetch.MediaSelectorView
import me.him188.ani.app.ui.mediafetch.MediaSourceResultListPresentation
import me.him188.ani.app.ui.mediafetch.MediaSourceResultsView
import me.him188.ani.app.ui.mediafetch.ViewKind
import me.him188.ani.app.ui.subject.episode.TAG_MEDIA_SELECTOR_SHEET
import me.him188.ani.app.ui.subject.episode.video.components.EpisodeVideoSideSheets
import me.him188.ani.app.ui.subject.episode.video.settings.SideSheetLayout

@Suppress("UnusedReceiverParameter")
@Composable
fun EpisodeVideoSideSheets.MediaSelectorSheet(
    mediaSelectorState: MediaSelectorState,
    mediaSourceResultListPresentation: MediaSourceResultListPresentation,
    viewKind: ViewKind,
    onViewKindChange: (ViewKind) -> Unit,
    onDismissRequest: () -> Unit,
    onRefresh: () -> Unit,
    onRestartSource: (instanceId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    SideSheetLayout(
        title = { Text(text = "选择数据源") },
        onDismissRequest = onDismissRequest,
        Modifier.testTag(TAG_MEDIA_SELECTOR_SHEET),
        closeButton = {
            IconButton(onClick = onDismissRequest) {
                Icon(Icons.Rounded.Close, contentDescription = "关闭")
            }
        },
    ) {
        MediaSelectorView(
            mediaSelectorState,
            viewKind,
            onViewKindChange,
            sourceResults = {
                MediaSourceResultsView(
                    mediaSourceResultListPresentation,
                    mediaSelectorState,
                    onRefresh,
                    onRestartSource,
                )
            },
            onRestartSource = onRestartSource,
            modifier.padding(horizontal = 16.dp)
                .fillMaxWidth()
                .navigationBarsPadding(),
            stickyHeaderBackgroundColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            onClickItem = {
                mediaSelectorState.select(it)
                onDismissRequest()
            },
            singleLineFilter = true,
        )
    }
}
