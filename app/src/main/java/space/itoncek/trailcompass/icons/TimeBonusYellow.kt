package space.itoncek.trailcompass.icons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

public class TimeBonusYellow(primary: Color) {
    val vector : ImageVector =  ImageVector.Builder(
            name = "space.itoncek.trailcompass.ui.theme.ComposeTestTheme.TimeBonusYellow",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            // M11 21 a8.8 8.8 0 0 1 -6.37 -2.62 A9.02 9.02 0 0 1 12 3.06 9 9 0 0 1 13 3.25 V5.3 A7 7 0 0 0 11 5 a6.8 6.8 0 0 0 -4.97 2.03 A6.8 6.8 0 0 0 4 12 a6.8 6.8 0 0 0 2.03 4.98 A6.8 6.8 0 0 0 11 19 a6.8 6.8 0 0 0 4.97 -2.02 A6.8 6.8 0 0 0 17.9 11 h2.05 A3 3 0 0 1 20 11.5 V12 a8.8 8.8 0 0 1 -2.62 6.38 A9 9 0 0 1 11 21 m7 -12 V6 h-3 V4 h3 V1 h2 v3 h3 v2 h-3 v3z
            path(
                pathFillType = PathFillType.NonZero,
                fill = SolidColor(Color(0xFF000000)),
            ) {
                // M 11 21
                moveTo(x = 11.0f, y = 21.0f)
                // a 8.8 8.8 0 0 1 -6.37 -2.62
                arcToRelative(
                    a = 8.8f,
                    b = 8.8f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -6.37f,
                    dy1 = -2.62f,
                )
                // A 9.02 9.02 0 0 1 12 3.06
                arcTo(
                    horizontalEllipseRadius = 9.02f,
                    verticalEllipseRadius = 9.02f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    x1 = 12.0f,
                    y1 = 3.06f,
                )
                // A 9 9 0 0 1 13 3.25
                arcTo(
                    horizontalEllipseRadius = 9.0f,
                    verticalEllipseRadius = 9.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    x1 = 13.0f,
                    y1 = 3.25f,
                )
                // V 5.3
                verticalLineTo(y = 5.3f)
                // A 7 7 0 0 0 11 5
                arcTo(
                    horizontalEllipseRadius = 7.0f,
                    verticalEllipseRadius = 7.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    x1 = 11.0f,
                    y1 = 5.0f,
                )
                // a 6.8 6.8 0 0 0 -4.97 2.03
                arcToRelative(
                    a = 6.8f,
                    b = 6.8f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -4.97f,
                    dy1 = 2.03f,
                )
                // A 6.8 6.8 0 0 0 4 12
                arcTo(
                    horizontalEllipseRadius = 6.8f,
                    verticalEllipseRadius = 6.8f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    x1 = 4.0f,
                    y1 = 12.0f,
                )
                // a 6.8 6.8 0 0 0 2.03 4.98
                arcToRelative(
                    a = 6.8f,
                    b = 6.8f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 2.03f,
                    dy1 = 4.98f,
                )
                // A 6.8 6.8 0 0 0 11 19
                arcTo(
                    horizontalEllipseRadius = 6.8f,
                    verticalEllipseRadius = 6.8f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    x1 = 11.0f,
                    y1 = 19.0f,
                )
                // a 6.8 6.8 0 0 0 4.97 -2.02
                arcToRelative(
                    a = 6.8f,
                    b = 6.8f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 4.97f,
                    dy1 = -2.02f,
                )
                // A 6.8 6.8 0 0 0 17.9 11
                arcTo(
                    horizontalEllipseRadius = 6.8f,
                    verticalEllipseRadius = 6.8f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    x1 = 17.9f,
                    y1 = 11.0f,
                )
                // h 2.05
                horizontalLineToRelative(dx = 2.05f)
                // A 3 3 0 0 1 20 11.5
                arcTo(
                    horizontalEllipseRadius = 3.0f,
                    verticalEllipseRadius = 3.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    x1 = 20.0f,
                    y1 = 11.5f,
                )
                // V 12
                verticalLineTo(y = 12.0f)
                // a 8.8 8.8 0 0 1 -2.62 6.38
                arcToRelative(
                    a = 8.8f,
                    b = 8.8f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -2.62f,
                    dy1 = 6.38f,
                )
                // A 9 9 0 0 1 11 21
                arcTo(
                    horizontalEllipseRadius = 9.0f,
                    verticalEllipseRadius = 9.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    x1 = 11.0f,
                    y1 = 21.0f,
                )
                // m 7 -12
                moveToRelative(dx = 7.0f, dy = -12.0f)
                // V 6
                verticalLineTo(y = 6.0f)
                // h -3
                horizontalLineToRelative(dx = -3.0f)
                // V 4
                verticalLineTo(y = 4.0f)
                // h 3
                horizontalLineToRelative(dx = 3.0f)
                // V 1
                verticalLineTo(y = 1.0f)
                // h 2
                horizontalLineToRelative(dx = 2.0f)
                // v 3
                verticalLineToRelative(dy = 3.0f)
                // h 3
                horizontalLineToRelative(dx = 3.0f)
                // v 2
                verticalLineToRelative(dy = 2.0f)
                // h -3
                horizontalLineToRelative(dx = -3.0f)
                // v 3z
                verticalLineToRelative(dy = 3.0f)
                close()
            }
            // M16.23 9.3 a2.3 2.3 0 0 0 -2.36 -2.27 v2.28z
            path(
                fill = SolidColor(Color(0xFFFFC800)),
            ) {
                // M 11.013037 6.6086617
                moveTo(x = 11.013037f, y = 6.6086617f)
                // a 5.2566037 5.2566037 0 0 0 5.18804 5.3937325
                arcToRelative(
                    a = 5.2566037f,
                    b = 5.2566037f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 5.18804f,
                    dy1 = 5.3937325f,
                )
                // l -5.2108946 0z
                lineToRelative(dx = -5.2108946f, dy = 0.0f)
                close()
            }
            // M11 5.52 V3.5
            path(
                stroke = SolidColor(Color(0xFF000000)),
            ) {
                // M 11 5.4893374
                moveTo(x = 11.0f, y = 5.4893374f)
                // L 11 3.5200799
                lineTo(x = 11.0f, y = 3.5200799f)
            }
            // m13.09 6.2 -1.46 -1.14
            path(
                stroke = SolidColor(Color(0xFF000000)),
            ) {
                // M 18.164299 16.13733
                moveTo(x = 18.164299f, y = 16.13733f)
                // l -1.5258269 -0.880754
                lineToRelative(dx = -1.5258269f, dy = -0.880754f)
            }
            // m13.09 6.2 -1.46 -1.14
            path(
                stroke = SolidColor(Color(0xFF000000)),
            ) {
                // M 15.1354885 19.165102
                moveTo(x = 15.1354885f, y = 19.165102f)
                // l -0.8810326 -1.5256652
                lineToRelative(dx = -0.8810326f, dy = -1.5256652f)
            }
            // m13.09 6.2 -1.46 -1.14
            path(
                stroke = SolidColor(Color(0xFF000000)),
            ) {
                // M 10.999195 20.273685
                moveTo(x = 10.999195f, y = 20.273685f)
                // l -1.6182661E-4 -1.7617719
                lineToRelative(dx = -1.6182661E-4f, dy = -1.7617719f)
            }
            // m13.09 6.2 -1.46 -1.14
            path(
                stroke = SolidColor(Color(0xFF000000)),
            ) {
                // M 6.862672 19.164299
                moveTo(x = 6.862672f, y = 19.164299f)
                // l 0.880754 -1.5258269
                lineToRelative(dx = 0.880754f, dy = -1.5258269f)
            }
            // m13.09 6.2 -1.46 -1.14
            path(
                stroke = SolidColor(Color(0xFF000000)),
            ) {
                // M 3.834898 16.135489
                moveTo(x = 3.834898f, y = 16.135489f)
                // l 1.5256652 -0.8810326
                lineToRelative(dx = 1.5256652f, dy = -0.8810326f)
            }
            // m13.09 6.2 -1.46 -1.14
            path(
                stroke = SolidColor(Color(0xFF000000)),
            ) {
                // M 2.7263145 11.999196
                moveTo(x = 2.7263145f, y = 11.999196f)
                // l 1.7617719 -1.6182661E-4
                lineToRelative(dx = 1.7617719f, dy = -1.6182661E-4f)
            }
            // M11 5.52 V3.5
            path(
                stroke = SolidColor(Color(0xFF000000)),
            ) {
                // M 17.510538 12
                moveTo(x = 17.510538f, y = 12.0f)
                // L 19.03083 12
                lineTo(x = 19.03083f, y = 12.0f)
            }
            // m13.09 6.2 -1.46 -1.14
            path(
                stroke = SolidColor(Color(0xFF000000)),
            ) {
                // M 3.835701 7.862672
                moveTo(x = 3.835701f, y = 7.862672f)
                // l 1.5258269 0.880754
                lineToRelative(dx = 1.5258269f, dy = 0.880754f)
            }
            // m13.09 6.2 -1.46 -1.14
            path(
                stroke = SolidColor(Color(0xFF000000)),
            ) {
                // M 6.8645115 4.834898
                moveTo(x = 6.8645115f, y = 4.834898f)
                // l 0.8810326 1.5256652
                lineToRelative(dx = 0.8810326f, dy = 1.5256652f)
            }
        }.build().also { _timeBonusYellow = it }
    }

@Preview
@Composable
private fun IconPreview() {
    space.itoncek.trailcompass.ui.theme.ComposeTestTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                imageVector = TimeBonusYellow(MaterialTheme.colorScheme.onPrimary).vector,
                contentDescription = null,
                modifier = Modifier
                    .width((24.0).dp)
                    .height((24.0).dp),
            )
        }
    }
}

@Suppress("ObjectPropertyName")
private var _timeBonusYellow: ImageVector? = null
