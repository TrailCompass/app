package com.composables

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Send: ImageVector
    get() {
        if (_Send != null) return _Send!!
        
        _Send = ImageVector.Builder(
            name = "Send",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000))
            ) {
                moveTo(120f, 800f)
                verticalLineToRelative(-640f)
                lineToRelative(760f, 320f)
                close()
                moveToRelative(80f, -120f)
                lineToRelative(474f, -200f)
                lineToRelative(-474f, -200f)
                verticalLineToRelative(140f)
                lineToRelative(240f, 60f)
                lineToRelative(-240f, 60f)
                close()
                moveToRelative(0f, 0f)
                verticalLineToRelative(-400f)
                close()
            }
        }.build()
        
        return _Send!!
    }

private var _Send: ImageVector? = null

