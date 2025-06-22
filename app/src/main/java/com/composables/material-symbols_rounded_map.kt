package com.composables
//  https://composeicons.com/
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Map: ImageVector
    get() {
        if (_Map != null) return _Map!!
        
        _Map = ImageVector.Builder(
            name = "Map",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000))
            ) {
                moveToRelative(574f, -129f)
                lineToRelative(-214f, -75f)
                lineToRelative(-186f, 72f)
                quadToRelative(-10f, 4f, -19.5f, 2.5f)
                reflectiveQuadTo(137f, 824f)
                reflectiveQuadToRelative(-12.5f, -13.5f)
                reflectiveQuadTo(120f, 791f)
                verticalLineToRelative(-561f)
                quadToRelative(0f, -13f, 7.5f, -23f)
                reflectiveQuadToRelative(20.5f, -15f)
                lineToRelative(186f, -63f)
                quadToRelative(6f, -2f, 12.5f, -3f)
                reflectiveQuadToRelative(13.5f, -1f)
                reflectiveQuadToRelative(13.5f, 1f)
                reflectiveQuadToRelative(12.5f, 3f)
                lineToRelative(214f, 75f)
                lineToRelative(186f, -72f)
                quadToRelative(10f, -4f, 19.5f, -2.5f)
                reflectiveQuadTo(823f, 136f)
                reflectiveQuadToRelative(12.5f, 13.5f)
                reflectiveQuadTo(840f, 169f)
                verticalLineToRelative(561f)
                quadToRelative(0f, 13f, -7.5f, 23f)
                reflectiveQuadTo(812f, 768f)
                lineToRelative(-186f, 63f)
                quadToRelative(-6f, 2f, -12.5f, 3f)
                reflectiveQuadToRelative(-13.5f, 1f)
                reflectiveQuadToRelative(-13.5f, -1f)
                reflectiveQuadToRelative(-12.5f, -3f)
                moveToRelative(-14f, -89f)
                verticalLineToRelative(-468f)
                lineToRelative(-160f, -56f)
                verticalLineToRelative(468f)
                close()
                moveToRelative(80f, 0f)
                lineToRelative(120f, -40f)
                verticalLineToRelative(-474f)
                lineToRelative(-120f, 46f)
                close()
                moveToRelative(-440f, -10f)
                lineToRelative(120f, -46f)
                verticalLineToRelative(-468f)
                lineToRelative(-120f, 40f)
                close()
                moveToRelative(440f, -458f)
                verticalLineToRelative(468f)
                close()
                moveToRelative(-320f, -56f)
                verticalLineToRelative(468f)
                close()
            }
        }.build()
        
        return _Map!!
    }

private var _Map: ImageVector? = null

