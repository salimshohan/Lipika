package io.lipika.app.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.FontDownload
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.ContentPaste
import androidx.compose.material.icons.outlined.DashboardCustomize
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Poll
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.lipika.data.prefs.LipikaPreferences

@Composable
fun SettingsMainScreen(
    preferences: LipikaPreferences,
    isImeEnabled: Boolean,
    isImeSelected: Boolean,
    onOpenSettings: () -> Unit,
    onSwitchIme: () -> Unit,
    onNavigate: (SettingsScreen) -> Unit
) {
    val scrollState = rememberScrollState()
    val colors = LocalSettingsColors.current
    var testInputText by remember { mutableStateOf("") }

    val activeThemeName = remember(preferences.theme) {
        when (preferences.theme) {
            "frosted_glass" -> "Frosted Glass Effect"
            "lipika_gradient" -> "Luminous Aurora"
            "default_dark" -> "Default Dark"
            "amoled_black" -> "AMOLED Black"
            "tokyo_night" -> "Tokyo Night"
            "nord" -> "Nord Frost"
            else -> preferences.theme.replace("_", " ").split(" ")
                .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
        }
    }

    val activeLayoutName = remember(preferences.primaryLayoutId) {
        when (preferences.primaryLayoutId) {
            "en_qwerty" -> "English"
            "bn_probhat" -> "প্রভাত"
            "bn_avro" -> "অভ্র"
            "bn_national" -> "জাতীয়"
            "ar_arabic" -> "العربية"
            else -> "English"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // 1. Scenic Mountain Sunset Header (Matching Reference Design)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                // Sky Gradient: Deep Pine Teal to Forest Teal
                val skyBrush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF092923),
                        Color(0xFF103A32),
                        Color(0xFF185346)
                    ),
                    startY = 0f,
                    endY = canvasHeight * 0.85f
                )
                drawRect(brush = skyBrush)

                // Glowing Sun: Warm Amber/Golden Sunset Disc
                val sunCenter = Offset(canvasWidth * 0.44f, canvasHeight * 0.42f)
                val sunRadius = canvasWidth * 0.16f
                val sunBrush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFEA79),
                        Color(0xFFFFB300),
                        Color(0xFFFF6F00)
                    ),
                    center = sunCenter,
                    radius = sunRadius
                )
                drawCircle(
                    brush = sunBrush,
                    radius = sunRadius,
                    center = sunCenter
                )

                // Layer 1: Distant Dark Crimson Mountains
                val backMountain = Path().apply {
                    moveTo(0f, canvasHeight * 0.65f)
                    lineTo(canvasWidth * 0.18f, canvasHeight * 0.52f)
                    lineTo(canvasWidth * 0.35f, canvasHeight * 0.58f)
                    lineTo(canvasWidth * 0.52f, canvasHeight * 0.44f)
                    lineTo(canvasWidth * 0.72f, canvasHeight * 0.56f)
                    lineTo(canvasWidth * 0.88f, canvasHeight * 0.48f)
                    lineTo(canvasWidth, canvasHeight * 0.54f)
                    lineTo(canvasWidth, canvasHeight)
                    lineTo(0f, canvasHeight)
                    close()
                }
                drawPath(path = backMountain, color = Color(0xFF881422))

                // Layer 2: Mid-ground Ruby Red Mountains
                val midMountain = Path().apply {
                    moveTo(0f, canvasHeight * 0.72f)
                    lineTo(canvasWidth * 0.22f, canvasHeight * 0.60f)
                    lineTo(canvasWidth * 0.42f, canvasHeight * 0.66f)
                    lineTo(canvasWidth * 0.62f, canvasHeight * 0.50f)
                    lineTo(canvasWidth * 0.82f, canvasHeight * 0.62f)
                    lineTo(canvasWidth, canvasHeight * 0.58f)
                    lineTo(canvasWidth, canvasHeight)
                    lineTo(0f, canvasHeight)
                    close()
                }
                drawPath(path = midMountain, color = Color(0xFFB71C1C))

                // Layer 3: Foreground Bright Crimson Mountain Silhouette
                val frontMountain = Path().apply {
                    moveTo(0f, canvasHeight * 0.82f)
                    lineTo(canvasWidth * 0.15f, canvasHeight * 0.72f)
                    lineTo(canvasWidth * 0.38f, canvasHeight * 0.76f)
                    lineTo(canvasWidth * 0.58f, canvasHeight * 0.60f)
                    lineTo(canvasWidth * 0.78f, canvasHeight * 0.74f)
                    lineTo(canvasWidth, canvasHeight * 0.68f)
                    lineTo(canvasWidth, canvasHeight)
                    lineTo(0f, canvasHeight)
                    close()
                }
                drawPath(path = frontMountain, color = Color(0xFFC62828))
            }
        }

        // 2. Floating Status & Quick Action Card (Overlapping scenic header)
        Column(
            modifier = Modifier
                .offset(y = (-32).dp)
                .padding(horizontal = 16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colors.cardBackground
                ),
                border = BorderStroke(1.dp, colors.cardBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    // Header Row: [Lipika Keyboard]  [• Active]
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Lipika Keyboard",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )

                        // Status Pill
                        val isActive = isImeEnabled && isImeSelected
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    if (!isImeEnabled) onOpenSettings()
                                    else if (!isImeSelected) onSwitchIme()
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (isActive) Color(0xFF16A34A) else Color(0xFFF59E0B))
                            )
                            Text(
                                text = if (isActive) "Active" else "Enable",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isActive) Color(0xFF16A34A) else Color(0xFFF59E0B)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Split Sub-Card: [Theme] | [Layout]
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = colors.inputBackground),
                        border = BorderStroke(1.dp, colors.cardBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Left Column: Theme
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onNavigate(SettingsScreen.AppearanceThemes(0)) }
                                    .padding(vertical = 4.dp, horizontal = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF10B981).copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Palette,
                                        contentDescription = "Theme",
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "Theme",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.textPrimary
                                    )
                                    Text(
                                        text = activeThemeName,
                                        fontSize = 12.sp,
                                        color = colors.textSecondary,
                                        maxLines = 1
                                    )
                                }
                            }

                            // Subtle Vertical Divider
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(38.dp)
                                    .background(colors.cardBorder)
                            )

                            // Right Column: Layout
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onNavigate(SettingsScreen.KeyboardLayouts) }
                                    .padding(vertical = 4.dp, horizontal = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF10B981).copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Keyboard,
                                        contentDescription = "Layout",
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "Layout",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.textPrimary
                                    )
                                    Text(
                                        text = activeLayoutName,
                                        fontSize = 12.sp,
                                        color = colors.textSecondary,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Quick Setup Alert (if not active)
            if (!isImeEnabled || !isImeSelected) {
                Spacer(modifier = Modifier.height(14.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.warningCardBg),
                    border = BorderStroke(1.dp, colors.warningCardBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (!isImeEnabled) "Lipika is not enabled" else "Lipika is not selected",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.warningCardText
                            )
                            Text(
                                text = if (!isImeEnabled) "Enable in System Settings" else "Tap to choose default keyboard",
                                fontSize = 12.sp,
                                color = colors.warningCardText.copy(alpha = 0.8f)
                            )
                        }
                        Button(
                            onClick = if (!isImeEnabled) onOpenSettings else onSwitchIme,
                            colors = ButtonDefaults.buttonColors(containerColor = colors.accent),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (!isImeEnabled) "Enable" else "Select",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // 3. Grid of 6 Circular Buttons (Exact 3 x 2 layout from screenshot)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(22.dp)
            ) {
                // Row 1: [Smart Typing]  [Fonts]  [Theme]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CircularDashboardButton(
                        icon = Icons.Outlined.AutoAwesome,
                        label = "Smart Typing",
                        colors = colors,
                        onClick = { onNavigate(SettingsScreen.SmartTyping) }
                    )
                    CircularDashboardButton(
                        icon = Icons.Outlined.FontDownload,
                        label = "Fonts",
                        colors = colors,
                        onClick = { onNavigate(SettingsScreen.Fonts) }
                    )
                    CircularDashboardButton(
                        icon = Icons.Outlined.Palette,
                        label = "Theme",
                        colors = colors,
                        onClick = { onNavigate(SettingsScreen.AppearanceThemes(0)) }
                    )
                }

                // Row 2: [Language]  [Preferences]  [Clipboard]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CircularDashboardButton(
                        icon = Icons.Outlined.Language,
                        label = "Language",
                        colors = colors,
                        onClick = { onNavigate(SettingsScreen.KeyboardLayouts) }
                    )
                    CircularDashboardButton(
                        icon = Icons.Outlined.Tune,
                        label = "Preferences",
                        colors = colors,
                        onClick = { onNavigate(SettingsScreen.Preferences) }
                    )
                    CircularDashboardButton(
                        icon = Icons.Outlined.ContentPaste,
                        label = "Clipboard",
                        colors = colors,
                        onClick = { onNavigate(SettingsScreen.Clipboard) }
                    )
                }

                // Row 3: [About]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularDashboardButton(
                        icon = Icons.Outlined.Info,
                        label = "About",
                        colors = colors,
                        onClick = { onNavigate(SettingsScreen.About) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
            // 4. Interactive Live Typing Sandbox Field
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
                border = BorderStroke(1.dp, colors.cardBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Live Keyboard Testing Sandbox",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = testInputText,
                        onValueChange = { testInputText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                text = "Tap here to test typing • লিপিকা টাইপ করুন...",
                                fontSize = 13.sp,
                                color = colors.textSecondary
                            )
                        },
                        trailingIcon = {
                            if (testInputText.isNotEmpty()) {
                                IconButton(onClick = { testInputText = "" }) {
                                    Icon(
                                        imageVector = Icons.Filled.Clear,
                                        contentDescription = "Clear",
                                        tint = colors.textSecondary
                                    )
                                }
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = colors.inputBackground,
                            unfocusedContainerColor = colors.inputBackground,
                            focusedBorderColor = colors.accent,
                            unfocusedBorderColor = colors.cardBorder,
                            focusedTextColor = colors.textPrimary,
                            unfocusedTextColor = colors.textPrimary
                        ),
                        singleLine = false,
                        maxLines = 3
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CircularDashboardButton(
    icon: ImageVector,
    label: String,
    colors: SettingsColors,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(colors.cardBackground)
                .border(BorderStroke(1.dp, colors.cardBorder), CircleShape)
                .padding(2.dp)
                .clip(CircleShape)
                .background(colors.inputBackground),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = colors.textPrimary,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = colors.textPrimary
        )
    }
}
