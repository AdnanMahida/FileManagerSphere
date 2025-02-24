package com.etb.filemanager.manager.media.image

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.etb.filemanager.manager.media.model.Media
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ZoomablePagerImage(
    modifier: Modifier = Modifier,
    media: Media,
    scrollEnabled: MutableState<Boolean>,
    maxScale: Float = 25f,
    maxImageSize: Int,
    onItemClick: () -> Unit
) {

    val zoomState = rememberZoomState(
        maxScale = maxScale
    )
    val label = media.uri.toString().substringAfterLast("/")
    val key = "media_${label}_0"
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(media.uri)
            .memoryCacheKey(key)
            .diskCacheKey(key)
            .size(maxImageSize)
            .build(),
        contentScale = ContentScale.Fit,
        filterQuality = FilterQuality.None,
        onSuccess = {
            zoomState.setContentSize(it.painter.intrinsicSize)
        }
    )

    LaunchedEffect(zoomState.scale) {
        scrollEnabled.value = zoomState.scale == 1f
    }

    Image(
        modifier = modifier
            .fillMaxSize()
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onDoubleClick = {},
                onClick = onItemClick
            )
            .zoomable(
                zoomState = zoomState
            ),
        painter = painter,
        contentScale = ContentScale.Fit,
        contentDescription = label
    )
}