package io.lipika.app.ui.settings

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.BrightnessMedium
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.ContentPaste
import androidx.compose.material.icons.outlined.FontDownload
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.VolumeUp
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import io.lipika.app.R
import io.lipika.data.prefs.LipikaPreferences
import io.lipika.theme.resolveKeyboardFontFamily
import kotlinx.coroutines.delay

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

    val context = androidx.compose.ui.platform.LocalContext.current
    val activeFontFamily = remember(preferences.selectedFont, preferences.customFontPath) {
        resolveKeyboardFontFamily(preferences.selectedFont, preferences.customFontPath)
    }

    val activeThemeName = remember(preferences.theme, context) {
        io.lipika.theme.ThemeDisplayUtils.resolveDisplayName(preferences.theme, context)
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
        // 1. Header Design & Minimal Typing Animation (Replaces sunset mountain illustration)
        MinimalTypingHeader(
            colors = colors,
            activeFontFamily = activeFontFamily
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 2. Dashboard Main Card (Lipika Keyboard + Active Status + Theme/Layout pills)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 3.dp,
                        shape = RoundedCornerShape(22.dp),
                        ambientColor = Color(0x1A000000),
                        spotColor = Color(0x24000000)
                    ),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colors.cardBackground
                ),
                border = BorderStroke(1.dp, colors.cardBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    // Header Row: [Logo + Lipika Keyboard]  [• Active]
                    val isActive = isImeEnabled && isImeSelected
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Image(
                                painter = painterResource(R.drawable.ic_launcher),
                                contentDescription = "Lipika Logo",
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            Text(
                                text = "Lipika Keyboard",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                        }

                        // Green Active Status Indicator
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isActive) Color(0xFF10B981).copy(alpha = 0.12f)
                                    else Color(0xFFF59E0B).copy(alpha = 0.12f)
                                )
                                .clickable {
                                    if (!isImeEnabled) onOpenSettings()
                                    else if (!isImeSelected) onSwitchIme()
                                }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (isActive) Color(0xFF10B981) else Color(0xFFF59E0B))
                            )
                            Text(
                                text = if (isActive) "Active" else "Enable",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isActive) Color(0xFF10B981) else Color(0xFFF59E0B)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Split Sub-Card: Theme & Layout info
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = colors.inputBackground),
                        border = BorderStroke(1.dp, colors.cardBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Left Column: Theme
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onNavigate(SettingsScreen.AppearanceThemes(0)) }
                                    .padding(vertical = 4.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(colors.accent.copy(alpha = 0.14f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Palette,
                                        contentDescription = "Theme",
                                        tint = colors.accent,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Theme",
                                        fontSize = 14.sp,
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
                                    .height(36.dp)
                                    .background(colors.cardBorder)
                            )

                            // Right Column: Layout
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onNavigate(SettingsScreen.KeyboardLayouts) }
                                    .padding(vertical = 4.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(colors.accent.copy(alpha = 0.14f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Keyboard,
                                        contentDescription = "Layout",
                                        tint = colors.accent,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Layout",
                                        fontSize = 14.sp,
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

            // Quick Setup Alert (if IME is not active)
            if (!isImeEnabled || !isImeSelected) {
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

            // 3. Live Keyboard Testing (Moved to TOP immediately below Main Card / Setup Alert)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 2.dp,
                        shape = RoundedCornerShape(20.dp),
                        ambientColor = Color(0x14000000),
                        spotColor = Color(0x20000000)
                    ),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
                border = BorderStroke(1.dp, colors.cardBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Live Keyboard Testing",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = activeLayoutName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = colors.accent
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = testInputText,
                        onValueChange = { testInputText = it },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(
                            fontFamily = activeFontFamily,
                            fontSize = 15.sp,
                            color = colors.textPrimary
                        ),
                        placeholder = {
                            Text(
                                text = "Tap here to test typing",
                                fontSize = 14.sp,
                                fontFamily = activeFontFamily,
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

            // 4. Rectangular Frosted Glass Option Cards (2-column clean grid, replaces circular buttons)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Row 1: Smart Typing & Fonts
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DashboardOptionCard(
                        modifier = Modifier.weight(1f),
                        title = "Smart Typing",
                        subtitle = "Corrections & suggestions",
                        icon = Icons.Outlined.AutoAwesome,
                        colors = colors,
                        onClick = { onNavigate(SettingsScreen.SmartTyping) }
                    )
                    DashboardOptionCard(
                        modifier = Modifier.weight(1f),
                        title = "Fonts",
                        subtitle = "Styles & typography",
                        icon = Icons.Outlined.FontDownload,
                        colors = colors,
                        onClick = { onNavigate(SettingsScreen.Fonts) }
                    )
                }

                // Row 2: Theme & Language
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DashboardOptionCard(
                        modifier = Modifier.weight(1f),
                        title = "Theme",
                        subtitle = activeThemeName,
                        icon = Icons.Outlined.Palette,
                        colors = colors,
                        onClick = { onNavigate(SettingsScreen.AppearanceThemes(0)) }
                    )
                    DashboardOptionCard(
                        modifier = Modifier.weight(1f),
                        title = "Language",
                        subtitle = "Layouts & input",
                        icon = Icons.Outlined.Language,
                        colors = colors,
                        onClick = { onNavigate(SettingsScreen.KeyboardLayouts) }
                    )
                }

                // Row 3: Preferences & Sound & Vibration
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DashboardOptionCard(
                        modifier = Modifier.weight(1f),
                        title = "Preferences",
                        subtitle = "Keys, popup & delay",
                        icon = Icons.Outlined.Tune,
                        colors = colors,
                        onClick = { onNavigate(SettingsScreen.Preferences) }
                    )
                    DashboardOptionCard(
                        modifier = Modifier.weight(1f),
                        title = "Sound & Vibration",
                        subtitle = "Volume, haptics & pulse",
                        icon = Icons.Outlined.VolumeUp,
                        colors = colors,
                        onClick = { onNavigate(SettingsScreen.SoundVibration) }
                    )
                }

                // Row 4: Clipboard & Appearance
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DashboardOptionCard(
                        modifier = Modifier.weight(1f),
                        title = "Clipboard",
                        subtitle = "History & pins",
                        icon = Icons.Outlined.ContentPaste,
                        colors = colors,
                        onClick = { onNavigate(SettingsScreen.Clipboard) }
                    )
                    DashboardOptionCard(
                        modifier = Modifier.weight(1f),
                        title = "Appearance",
                        subtitle = "Display, themes & dark style",
                        icon = Icons.Outlined.BrightnessMedium,
                        colors = colors,
                        onClick = { onNavigate(SettingsScreen.Appearance) }
                    )
                }

                // Row 5: Voice Typing & Typing Insights
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DashboardOptionCard(
                        modifier = Modifier.weight(1f),
                        title = "Voice Typing",
                        subtitle = "Speech to text input",
                        icon = Icons.Outlined.Mic,
                        colors = colors,
                        onClick = { onNavigate(SettingsScreen.VoiceTyping) }
                    )
                    DashboardOptionCard(
                        modifier = Modifier.weight(1f),
                        title = "Typing Insights",
                        subtitle = "Keystrokes & metrics",
                        icon = Icons.Outlined.Analytics,
                        colors = colors,
                        onClick = { onNavigate(SettingsScreen.TypingStatistics) }
                    )
                }

                // Row 6: Backup & Restore & About
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DashboardOptionCard(
                        modifier = Modifier.weight(1f),
                        title = "Backup & Restore",
                        subtitle = "Cloud & file backup",
                        icon = Icons.Outlined.CloudUpload,
                        colors = colors,
                        onClick = { onNavigate(SettingsScreen.BackupRestore) }
                    )
                    DashboardOptionCard(
                        modifier = Modifier.weight(1f),
                        title = "About",
                        subtitle = "Version & creator",
                        icon = Icons.Outlined.Info,
                        colors = colors,
                        onClick = { onNavigate(SettingsScreen.About) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun MinimalTypingHeader(
    colors: SettingsColors,
    activeFontFamily: FontFamily
) {
    val phrases = remember {
        listOf(
            // Bengali Short Words & Phrases
            "লিপিকা কীবোর্ড",
            "সহজ ও সুন্দর বাংলা টাইপিং",
            "আমার সোনার বাংলা",
            "বাংলায় লিখুন সহজে",
            "স্মার্ট কীবোর্ড লিপিকা",
            "শব্দ ও বাক্যের জাদু",
            "দ্রুত ও নির্ভুল টাইপিং",
            "মাতৃভাষায় মনের ভাব",
            "ভাষার ভালোবাসায় লিপিকা",
            "বর্ণ থেকে শব্দ",
            "অ আ ক খ • বাংলা লিপি",
            "প্রভাত ও অভ্র লেআউট",
            "ঝড়ের গতিতে টাইপ করুন",
            "সুন্দর ফন্ট ও থিম",
            "স্মার্ট প্রেডিকশন ও সাজেশন",
            "স্মার্টফোন হোক বাংলাবান্ধব",
            "স্বপ্নের মতো টাইপিং অভিজ্ঞতা",
            "নতুন দিনের নতুন ভাবনা",
            "বাংলা বর্ণমালার সুর",
            "মন খুলে লিখুন বাংলায়",
            "একুশের চেতনা ও বর্ণমালা",
            "যুক্তাক্ষর লিখুন অনায়াসে",
            "হাতেখড়ি থেকে পেশাদার",
            "স্মার্ট বানান সংশোধনী",
            "কবিতা ও গানের কথামালা",
            "কথা হবে প্রাণ খুলে",
            "বাংলা ভাষার ডিজিটাল রূপ",
            "ধ্বনি ও অক্ষরের মিলন",
            "রঙিন থিমে সাজুক কীবোর্ড",
            "প্রতিটি স্পর্শে শুদ্ধ উচ্চারণ",
            "আপনার নিজস্ব কীবোর্ড",

            // English Words & Phrases
            "Type freely in বাংলা & English",
            "Smart. Fast. Beautiful.",
            "Crafted for effortless typing",
            "The quick brown fox jumps",
            "Next-word prediction engine",
            "Express yourself with emojis",
            "Designed with love in Bangladesh",
            "Precision typography & layouts",
            "Speed meets elegance",
            "Flawless typing, every time",
            "Your personal typing companion",
            "Type without limits",
            "Smooth haptics & keypress sound",
            "Beautiful frosted glass themes",
            "Where speed meets precision",
            "Write with confidence",
            "Effortless bilingual switching",
            "Smart autocorrect & learning",
            "Type, tap, and connect",
            "Unleash your typing speed",
            "Fluid typing on any screen",
            "Natural text suggestions",
            "Intelligent spell checking",
            "Personalized dictionary",
            "Modern glassmorphism UI",
            "Fastest way to type Bangla",
            "Pure typographic delight",
            "Every keystroke counts",
            "A keyboard made for you"
        )
    }

    var playlist by remember { mutableStateOf(phrases.shuffled()) }
    var playlistIndex by remember { mutableStateOf(0) }
    var displayedLength by remember { mutableStateOf(0) }
    var isDeleting by remember { mutableStateOf(false) }

    LaunchedEffect(playlistIndex, isDeleting) {
        val currentPhrase = playlist.getOrElse(playlistIndex) { phrases[0] }
        if (!isDeleting) {
            while (displayedLength < currentPhrase.length) {
                delay(65L)
                displayedLength++
            }
            delay(1700L)
            isDeleting = true
        } else {
            while (displayedLength > 0) {
                delay(30L)
                displayedLength--
            }
            delay(250L)
            isDeleting = false
            val nextIndex = playlistIndex + 1
            if (nextIndex >= playlist.size) {
                val lastItem = playlist.lastOrNull()
                var reshuffled = phrases.shuffled()
                if (reshuffled.firstOrNull() == lastItem && reshuffled.size > 1) {
                    reshuffled = reshuffled.drop(1) + reshuffled.take(1)
                }
                playlist = reshuffled
                playlistIndex = 0
            } else {
                playlistIndex = nextIndex
            }
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "HeaderCursorTransition")
    val cursorAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "CursorBlink"
    )

    val floatOffset1 by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Float1"
    )
    val floatOffset2 by infiniteTransition.animateFloat(
        initialValue = 6f,
        targetValue = -6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Float2"
    )

    val currentPhrase = playlist.getOrElse(playlistIndex) { phrases[0] }
    val displayedText = currentPhrase.take(displayedLength)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        colors.accent.copy(alpha = 0.12f),
                        colors.accent.copy(alpha = 0.03f),
                        Color.Transparent
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Floating ambient characters
        Text(
            text = "ক",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = colors.accent.copy(alpha = 0.08f),
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 28.dp, y = (16 + floatOffset1).dp)
        )
        Text(
            text = "A",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary.copy(alpha = 0.07f),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = 52.dp, y = (-18 + floatOffset2).dp)
        )
        Text(
            text = "ল",
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            color = colors.accent.copy(alpha = 0.08f),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-32).dp, y = (18 + floatOffset2).dp)
        )
        Text(
            text = "B",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary.copy(alpha = 0.07f),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-56).dp, y = (-16 + floatOffset1).dp)
        )

        // Center Content: Badge & Typing Text
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            // Minimalist badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(colors.accent.copy(alpha = 0.12f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF10B981))
                )
                Text(
                    text = "LIPIKA SMART ENGINE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = colors.accent
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Animated Typing Text Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.height(30.dp)
            ) {
                Text(
                    text = displayedText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = activeFontFamily,
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = " |",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Light,
                    color = colors.accent.copy(alpha = cursorAlpha)
                )
            }
        }
    }
}

@Composable
private fun DashboardOptionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    colors: SettingsColors,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(90.dp)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(18.dp),
                ambientColor = Color(0x14000000),
                spotColor = Color(0x20000000)
            )
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.cardBackground
        ),
        border = BorderStroke(1.dp, colors.cardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(colors.accent.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = colors.accent,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
