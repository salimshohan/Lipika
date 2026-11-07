package io.lipika.app.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Backspace
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.FontDownload
import androidx.compose.material.icons.outlined.HourglassBottom
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Mood
import androidx.compose.material.icons.outlined.Numbers
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.lipika.data.prefs.LipikaPreferences
import java.text.NumberFormat
import java.util.Locale

@Composable
fun TypingStatisticsScreen(
    preferences: LipikaPreferences,
    onResetStats: () -> Unit,
    onBack: () -> Unit = {}
) {
    val colors = LocalSettingsColors.current
    val scrollState = rememberScrollState()
    var showResetDialog by remember { mutableStateOf(false) }

    val formatter = remember { NumberFormat.getNumberInstance(Locale.US) }

    val totalLetters = preferences.statsBanglaLetters + preferences.statsEnglishLetters
    val banglaPercent = if (totalLetters > 0) ((preferences.statsBanglaLetters.toDouble() / totalLetters) * 100).toInt() else 50
    val englishPercent = if (totalLetters > 0) (100 - banglaPercent) else 50

    val (badgeTitle, badgeColor) = remember(preferences.statsKeystrokes) {
        when {
            preferences.statsKeystrokes >= 50_000L -> "Grandmaster Typist" to Color(0xFFF59E0B)
            preferences.statsKeystrokes >= 15_000L -> "Keyboard Artisan" to Color(0xFF8B5CF6)
            preferences.statsKeystrokes >= 5_000L -> "Fluent Scribe" to Color(0xFF06B6D4)
            preferences.statsKeystrokes >= 1_000L -> "Rising Wordsmith" to Color(0xFF10B981)
            else -> "Novice Typist" to Color(0xFF64748B)
        }
    }

    val accuracyPercent = remember(preferences.statsKeystrokes, preferences.statsBackspaces) {
        if (preferences.statsKeystrokes <= 0L) 100.0
        else {
            val valid = (preferences.statsKeystrokes - preferences.statsBackspaces).coerceAtLeast(0L)
            val pct = (valid.toDouble() / preferences.statsKeystrokes) * 100.0
            (pct * 10).toInt() / 10.0
        }
    }

    val timeSavedMinutes = remember(preferences.statsAvroWords, preferences.statsWordsTyped) {
        val totalSmartUnits = preferences.statsAvroWords + (preferences.statsWordsTyped / 3)
        ((totalSmartUnits * 1.5) / 60.0).toInt().coerceAtLeast(0)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .padding(bottom = 36.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // ==========================================
        // 1. HERO OVERVIEW & TYPIST LEVEL BADGE
        // ==========================================
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = colors.accent.copy(alpha = 0.12f)),
            border = BorderStroke(1.dp, colors.accent.copy(alpha = 0.35f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(colors.accent.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Analytics,
                                contentDescription = null,
                                tint = colors.accent,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Total Keystrokes",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.textSecondary
                            )
                            Text(
                                text = formatter.format(preferences.statsKeystrokes),
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                        }
                    }

                    // Level Badge
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(badgeColor.copy(alpha = 0.16f))
                            .border(1.dp, badgeColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Star,
                            contentDescription = null,
                            tint = badgeColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = badgeTitle,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = badgeColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Language Ratio Bar
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "বাংলা: $banglaPercent%",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "English: $englishPercent%",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(colors.cardBorder)
                    ) {
                        Row(modifier = Modifier.fillMaxSize()) {
                            Box(
                                modifier = Modifier
                                    .weight((banglaPercent.coerceAtLeast(1)).toFloat())
                                    .fillMaxSize()
                                    .background(colors.accent)
                            )
                            Box(
                                modifier = Modifier
                                    .weight((englishPercent.coerceAtLeast(1)).toFloat())
                                    .fillMaxSize()
                                    .background(Color(0xFF64748B))
                            )
                        }
                    }
                }
            }
        }

        // ==========================================
        // 2. PRODUCTIVITY & ACCURACY INSIGHTS
        // ==========================================
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
                border = BorderStroke(1.dp, colors.cardBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF10B981).copy(alpha = 0.14f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Text(
                            text = "$accuracyPercent%",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Accuracy Rate",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Text(
                        text = "Clean keystroke precision",
                        fontSize = 10.5.sp,
                        color = colors.textSecondary
                    )
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
                border = BorderStroke(1.dp, colors.cardBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF97316).copy(alpha = 0.14f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.HourglassBottom,
                                contentDescription = null,
                                tint = Color(0xFFF97316),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Text(
                            text = "~$timeSavedMinutes m",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF97316)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Est. Time Saved",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Text(
                        text = "Via smart predictions",
                        fontSize = 10.5.sp,
                        color = colors.textSecondary
                    )
                }
            }
        }

        // ==========================================
        // 3. TYPING BREAKDOWN (2x2 Grid Cards)
        // ==========================================
        SettingsCategoryHeader(title = "Keystroke & Word Breakdown")

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatMetricCard(
                modifier = Modifier.weight(1f),
                title = "Bangla Letters",
                value = formatter.format(preferences.statsBanglaLetters),
                subtitle = "বাংলা বর্ণ ও চিহ্ন",
                icon = Icons.Outlined.Language,
                colors = colors
            )
            StatMetricCard(
                modifier = Modifier.weight(1f),
                title = "English Letters",
                value = formatter.format(preferences.statsEnglishLetters),
                subtitle = "Latin alphabetic chars",
                icon = Icons.Outlined.TextFields,
                colors = colors
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatMetricCard(
                modifier = Modifier.weight(1f),
                title = "Avro Words",
                value = formatter.format(preferences.statsAvroWords),
                subtitle = "Phonetic transliterations",
                icon = Icons.Outlined.Translate,
                colors = colors
            )
            StatMetricCard(
                modifier = Modifier.weight(1f),
                title = "Total Words",
                value = formatter.format(preferences.statsWordsTyped),
                subtitle = "Completed words entered",
                icon = Icons.Outlined.FontDownload,
                colors = colors
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatMetricCard(
                modifier = Modifier.weight(1f),
                title = "Numbers Typed",
                value = formatter.format(preferences.statsNumbersTyped),
                subtitle = "Digits 0–9 & ০–৯",
                icon = Icons.Outlined.Numbers,
                colors = colors
            )
            StatMetricCard(
                modifier = Modifier.weight(1f),
                title = "Emojis Inserted",
                value = formatter.format(preferences.statsEmojis),
                subtitle = "Emoticons & smileys",
                icon = Icons.Outlined.Mood,
                colors = colors
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatMetricCard(
                modifier = Modifier.weight(1f),
                title = "Backspaces / Edits",
                value = formatter.format(preferences.statsBackspaces),
                subtitle = "Corrections & deletes",
                icon = Icons.Outlined.Backspace,
                colors = colors
            )
            StatMetricCard(
                modifier = Modifier.weight(1f),
                title = "Sound Clicks",
                value = formatter.format(preferences.statsAudioClicks),
                subtitle = "Audible key feedbacks",
                icon = Icons.Outlined.VolumeUp,
                colors = colors
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // ==========================================
        // 4. RESET STATISTICS BUTTON
        // ==========================================
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
            border = BorderStroke(1.dp, colors.cardBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Reset Statistics",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Text(
                        text = "Clear keystroke counters and start fresh",
                        fontSize = 11.5.sp,
                        color = colors.textSecondary
                    )
                }

                OutlinedButton(
                    onClick = { showResetDialog = true },
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f))
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Refresh,
                        contentDescription = null,
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Reset", color = Color(0xFFEF4444), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = {
                Text(
                    text = "Reset Typing Statistics?",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
            },
            text = {
                Text(
                    text = "All typed letter, word, number, emoji, and sound counters will be reset to zero.",
                    fontSize = 13.sp,
                    color = colors.textSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onResetStats()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Reset", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showResetDialog = false }) {
                    Text("Cancel", color = colors.textPrimary)
                }
            }
        )
    }
}

@Composable
private fun StatMetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    colors: SettingsColors,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
        border = BorderStroke(1.dp, colors.cardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(colors.accent.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = colors.accent,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Text(
                    text = value,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                maxLines = 1
            )
            Text(
                text = subtitle,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.textSecondary,
                maxLines = 1
            )
        }
    }
}
