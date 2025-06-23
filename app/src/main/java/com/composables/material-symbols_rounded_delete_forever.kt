package com.composables

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Delete_forever: ImageVector
    get() {
        if (_Delete_forever != null) return _Delete_forever!!

        _Delete_forever = ImageVector.Builder(
            name = "Delete_forever",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000))
            ) {
                moveTo(280f, 840f)
                quadToRelative(-33f, 0f, -56.5f, -23.5f)
                reflectiveQuadTo(200f, 760f)
                verticalLineToRelative(-520f)
                quadToRelative(-17f, 0f, -28.5f, -11.5f)
                reflectiveQuadTo(160f, 200f)
                reflectiveQuadToRelative(11.5f, -28.5f)
                reflectiveQuadTo(200f, 160f)
                horizontalLineToRelative(160f)
                quadToRelative(0f, -17f, 11.5f, -28.5f)
                reflectiveQuadTo(400f, 120f)
                horizontalLineToRelative(160f)
                quadToRelative(17f, 0f, 28.5f, 11.5f)
                reflectiveQuadTo(600f, 160f)
                horizontalLineToRelative(160f)
                quadToRelative(17f, 0f, 28.5f, 11.5f)
                reflectiveQuadTo(800f, 200f)
                reflectiveQuadToRelative(-11.5f, 28.5f)
                reflectiveQuadTo(760f, 240f)
                verticalLineToRelative(520f)
                quadToRelative(0f, 33f, -23.5f, 56.5f)
                reflectiveQuadTo(680f, 840f)
                close()
                moveToRelative(400f, -600f)
                horizontalLineTo(280f)
                verticalLineToRelative(520f)
                horizontalLineToRelative(400f)
                close()
                moveToRelative(-400f, 0f)
                verticalLineToRelative(520f)
                close()
                moveToRelative(200f, 316f)
                lineToRelative(76f, 76f)
                quadToRelative(11f, 11f, 28f, 11f)
                reflectiveQuadToRelative(28f, -11f)
                reflectiveQuadToRelative(11f, -28f)
                reflectiveQuadToRelative(-11f, -28f)
                lineToRelative(-76f, -76f)
                lineToRelative(76f, -76f)
                quadToRelative(11f, -11f, 11f, -28f)
                reflectiveQuadToRelative(-11f, -28f)
                reflectiveQuadToRelative(-28f, -11f)
                reflectiveQuadToRelative(-28f, 11f)
                lineToRelative(-76f, 76f)
                lineToRelative(-76f, -76f)
                quadToRelative(-11f, -11f, -28f, -11f)
                reflectiveQuadToRelative(-28f, 11f)
                reflectiveQuadToRelative(-11f, 28f)
                reflectiveQuadToRelative(11f, 28f)
                lineToRelative(76f, 76f)
                lineToRelative(-76f, 76f)
                quadToRelative(-11f, 11f, -11f, 28f)
                reflectiveQuadToRelative(11f, 28f)
                reflectiveQuadToRelative(28f, 11f)
                reflectiveQuadToRelative(28f, -11f)
                close()
            }
        }.build()

        return _Delete_forever!!
    }

private var _Delete_forever: ImageVector? = null

