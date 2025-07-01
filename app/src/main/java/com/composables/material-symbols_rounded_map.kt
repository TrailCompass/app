package com.composables

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Map: ImageVector
    get() {
        val current = _Map
        if (current != null) return current

        return ImageVector.Builder(
            name = "space.itoncek.trailcompass.ui.theme.ComposeTestTheme.Map",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 960.0f,
            viewportHeight = 960.0f,
        ).apply {
            // m574 -129 -214 -75 -186 72 q-10 4 -19.5 2.5 T137 -136 t-12.5 -13.5 T120 -169 v-561 q0 -13 7.5 -23 t20.5 -15 l186 -63 q6 -2 12.5 -3 t13.5 -1 13.5 1 12.5 3 l214 75 186 -72 q10 -4 19.5 -2.5 T823 -824 t12.5 13.5 T840 -791 v561 q0 13 -7.5 23 T812 -192 l-186 63 q-6 2 -12.5 3 t-13.5 1 -13.5 -1 -12.5 -3 m-14 -89 v-468 l-160 -56 v468z m80 0 120 -40 v-474 l-120 46z m-440 -10 120 -46 v-468 l-120 40z m440 -458 v468z m-320 -56 v468z
            path(
                fill = SolidColor(Color(0xFF000000)),
            ) {
                // M 574 831
                moveTo(x = 574.0f, y = 831.0f)
                // l -214 -75
                lineToRelative(dx = -214.0f, dy = -75.0f)
                // l -186 72
                lineToRelative(dx = -186.0f, dy = 72.0f)
                // q -10 4 -19.5 2.5
                quadToRelative(
                    dx1 = -10.0f,
                    dy1 = 4.0f,
                    dx2 = -19.5f,
                    dy2 = 2.5f,
                )
                // T 137 824
                reflectiveQuadTo(
                    x1 = 137.0f,
                    y1 = 824.0f,
                )
                // t -12.5 -13.5
                reflectiveQuadToRelative(
                    dx1 = -12.5f,
                    dy1 = -13.5f,
                )
                // T 120 791
                reflectiveQuadTo(
                    x1 = 120.0f,
                    y1 = 791.0f,
                )
                // l 0 -561
                lineToRelative(dx = 0.0f, dy = -561.0f)
                // q 0 -13 7.5 -23
                quadToRelative(
                    dx1 = 0.0f,
                    dy1 = -13.0f,
                    dx2 = 7.5f,
                    dy2 = -23.0f,
                )
                // t 20.5 -15
                reflectiveQuadToRelative(
                    dx1 = 20.5f,
                    dy1 = -15.0f,
                )
                // l 186 -63
                lineToRelative(dx = 186.0f, dy = -63.0f)
                // q 6 -2 12.5 -3
                quadToRelative(
                    dx1 = 6.0f,
                    dy1 = -2.0f,
                    dx2 = 12.5f,
                    dy2 = -3.0f,
                )
                // t 13.5 -1
                reflectiveQuadToRelative(
                    dx1 = 13.5f,
                    dy1 = -1.0f,
                )
                // t 13.5 1
                reflectiveQuadToRelative(
                    dx1 = 13.5f,
                    dy1 = 1.0f,
                )
                // t 12.5 3
                reflectiveQuadToRelative(
                    dx1 = 12.5f,
                    dy1 = 3.0f,
                )
                // l 214 75
                lineToRelative(dx = 214.0f, dy = 75.0f)
                // l 186 -72
                lineToRelative(dx = 186.0f, dy = -72.0f)
                // q 10 -4 19.5 -2.5
                quadToRelative(
                    dx1 = 10.0f,
                    dy1 = -4.0f,
                    dx2 = 19.5f,
                    dy2 = -2.5f,
                )
                // T 823 136
                reflectiveQuadTo(
                    x1 = 823.0f,
                    y1 = 136.0f,
                )
                // t 12.5 13.5
                reflectiveQuadToRelative(
                    dx1 = 12.5f,
                    dy1 = 13.5f,
                )
                // T 840 169
                reflectiveQuadTo(
                    x1 = 840.0f,
                    y1 = 169.0f,
                )
                // l 0 561
                lineToRelative(dx = 0.0f, dy = 561.0f)
                // q 0 13 -7.5 23
                quadToRelative(
                    dx1 = 0.0f,
                    dy1 = 13.0f,
                    dx2 = -7.5f,
                    dy2 = 23.0f,
                )
                // T 812 768
                reflectiveQuadTo(
                    x1 = 812.0f,
                    y1 = 768.0f,
                )
                // l -186 63
                lineToRelative(dx = -186.0f, dy = 63.0f)
                // q -6 2 -12.5 3
                quadToRelative(
                    dx1 = -6.0f,
                    dy1 = 2.0f,
                    dx2 = -12.5f,
                    dy2 = 3.0f,
                )
                // t -13.5 1
                reflectiveQuadToRelative(
                    dx1 = -13.5f,
                    dy1 = 1.0f,
                )
                // t -13.5 -1
                reflectiveQuadToRelative(
                    dx1 = -13.5f,
                    dy1 = -1.0f,
                )
                // t -12.5 -3
                reflectiveQuadToRelative(
                    dx1 = -12.5f,
                    dy1 = -3.0f,
                )
                // m -14 -89
                moveToRelative(dx = -14.0f, dy = -89.0f)
                // l 0 -468
                lineToRelative(dx = 0.0f, dy = -468.0f)
                // l -160 -56
                lineToRelative(dx = -160.0f, dy = -56.0f)
                // l 0 468z
                lineToRelative(dx = 0.0f, dy = 468.0f)
                close()
                // m 80 0
                moveToRelative(dx = 80.0f, dy = 0.0f)
                // l 120 -40
                lineToRelative(dx = 120.0f, dy = -40.0f)
                // l 0 -474
                lineToRelative(dx = 0.0f, dy = -474.0f)
                // l -120 46z
                lineToRelative(dx = -120.0f, dy = 46.0f)
                close()
                // m -440 -10
                moveToRelative(dx = -440.0f, dy = -10.0f)
                // l 120 -46
                lineToRelative(dx = 120.0f, dy = -46.0f)
                // l 0 -468
                lineToRelative(dx = 0.0f, dy = -468.0f)
                // l -120 40z
                lineToRelative(dx = -120.0f, dy = 40.0f)
                close()
                // m 440 -458
                moveToRelative(dx = 440.0f, dy = -458.0f)
                // l 0 468z
                lineToRelative(dx = 0.0f, dy = 468.0f)
                close()
                // m -320 -56
                moveToRelative(dx = -320.0f, dy = -56.0f)
                // l 0 468z
                lineToRelative(dx = 0.0f, dy = 468.0f)
                close()
            }
        }.build().also { _Map = it }
    }

private var _Map: ImageVector? = null


