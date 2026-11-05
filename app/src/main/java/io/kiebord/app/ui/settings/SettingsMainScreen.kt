package io.kiebord.app.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ContentPaste
import androidx.compose.material.icons.outlined.FontDownload
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Spellcheck
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.kiebord.app.R
import io.kiebord.data.prefs.KiebordPreferences

@Composable
fun SettingsMainScreen(
    preferences: KiebordPreferences,
    isImeEnabled: Boolean,
    isImeSelected: Boolean,
    onOpenSettings: () -> Unit,
    onSwitchIme: () -> Unit,
    onNavigate: (SettingsScreen) -> Unit
) {
    val scrollState = rememberScrollState()
    val colors = LocalSettingsColors.current
    var testInputText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 18.dp, vertical = 14.dp)
    ) {
        // 1. Hero Brand Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
            border = BorderStroke(1.dp, colors.accent.copy(alpha = 0.25f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_launcher),
                        contentDescription = "Lipika Logo",
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(14.dp))
                    )

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Lipika",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(colors.accent.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "v0.1.5",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.accent
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "লিপিকা — দ্রুত, আধুনিক ও সুন্দর কীবোর্ড",
                            fontSize = 13.sp,
                            color = colors.textSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Activation Status & Live Typing Test Card
        if (!isImeEnabled || !isImeSelected) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = colors.warningCardBg),
                border = BorderStroke(1.dp, colors.warningCardBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = if (!isImeEnabled) "কীবোর্ড একটিভ করা প্রয়োজন" else "লিপিকা সিলেক্ট করুন",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.warningCardText
                    )
                    Text(
                        text = if (!isImeEnabled) {
                            "টাইপিং শুরু করতে অ্যান্ড্রয়েড সিস্টেম সেটিংস থেকে Lipika অন করুন।"
                        } else {
                            "Lipika অন আছে, কিন্তু ডিফল্ট ইনপুট মেথড হিসেবে সিলেক্ট করা হয়নি।"
                        },
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = colors.warningCardSubtext,
                        modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = if (!isImeEnabled) onOpenSettings else onSwitchIme,
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.warningBtnBg,
                                contentColor = colors.warningBtnText
                            )
                        ) {
                            Icon(
                                imageVector = if (!isImeEnabled) Icons.Filled.Settings else Icons.Outlined.SwapHoriz,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (!isImeEnabled) "Turn On in Settings" else "Select Lipika",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        Button(
                            onClick = { onNavigate(SettingsScreen.Onboarding) },
                            modifier = Modifier.height(44.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.warningSecondaryBtnBg,
                                contentColor = colors.warningSecondaryBtnText
                            ),
                            border = BorderStroke(1.dp, colors.warningSecondaryBtnBorder)
                        ) {
                            Text(
                                text = "Setup Wizard",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
                border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.35f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF10B981))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Lipika is Active & Ready",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "সুপার স্পিড রেডি",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF10B981)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Live Interactive Keyboard Testing Box
                    OutlinedTextField(
                        value = testInputText,
                        onValueChange = { testInputText = it },
                        placeholder = {
                            Text(
                                text = "এখানে টাইপ করে টেস্ট করুন...",
                                fontSize = 13.sp,
                                color = colors.textSecondary.copy(alpha = 0.7f)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = colors.inputBackground,
                            unfocusedContainerColor = colors.inputBackground,
                            focusedBorderColor = colors.accent,
                            unfocusedBorderColor = colors.cardBorder,
                            focusedTextColor = colors.textPrimary,
                            unfocusedTextColor = colors.textPrimary
                        ),
                        singleLine = true
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 3. Smart Navigation Items
        SettingsCategoryHeader(title = "কাস্টমাইজেশন ও সেটিংস")

        SettingsGroup {
            SmartSettingsTile(
                title = "Appearance, Themes & Fonts",
                subtitle = "থিম এবং Tw Cent MT সহ কাস্টম ফন্ট সিলেকশন",
                icon = Icons.Outlined.Palette,
                position = GroupPosition.TOP,
                onClick = { onNavigate(SettingsScreen.AppearanceThemes) }
            )

            SmartSettingsTile(
                title = "Keyboard Layouts",
                subtitle = "English, Ovro, জাতীয়, প্রভাত, Arabic",
                icon = Icons.Outlined.Keyboard,
                position = GroupPosition.MIDDLE,
                onClick = { onNavigate(SettingsScreen.KeyboardLayouts) }
            )

            SmartSettingsTile(
                title = "Preferences & Speed",
                subtitle = "২২০ms টাইপিং রেসপন্স, হ্যাপটিক ও লেআউট স্কেল",
                icon = Icons.Outlined.Tune,
                position = GroupPosition.MIDDLE,
                onClick = { onNavigate(SettingsScreen.Preferences) }
            )

            SmartSettingsTile(
                title = "Corrections & Suggestions",
                subtitle = "স্মার্ট সাজেশন, স্বয়ংক্রিয় স্পেস ও জেসচার টাইপিং",
                icon = Icons.Outlined.Spellcheck,
                position = GroupPosition.MIDDLE,
                onClick = { onNavigate(SettingsScreen.CorrectionsSuggestions) }
            )

            SmartSettingsTile(
                title = "Clipboard Manager",
                subtitle = "কপি হিস্ট্রি ও পিনড ক্লিপস",
                icon = Icons.Outlined.ContentPaste,
                position = GroupPosition.MIDDLE,
                onClick = { onNavigate(SettingsScreen.Clipboard) }
            )

            SmartSettingsTile(
                title = "Voice Typing",
                subtitle = "ভয়েস দিয়ে সহজে কথা বলে টাইপ করুন",
                icon = Icons.Outlined.Mic,
                position = GroupPosition.BOTTOM,
                onClick = { onNavigate(SettingsScreen.VoiceTyping) }
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 4. Developer & About Card
        SettingsCategoryHeader(title = "ডেভেলপার ও পরিচিতি")

        SettingsGroup {
            SmartSettingsTile(
                title = "About Lipika",
                subtitle = "Salim Hosen Shohan • University of Rajshahi",
                icon = Icons.Outlined.Info,
                position = GroupPosition.ALONE,
                onClick = { onNavigate(SettingsScreen.About) }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun SmartSettingsTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    position: GroupPosition,
    onClick: () -> Unit
) {
    val colors = LocalSettingsColors.current

    SettingsItemCard(
        position = position,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.iconBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = colors.accent,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = colors.textSecondary,
                    lineHeight = 16.sp
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                contentDescription = null,
                tint = colors.textSecondary.copy(alpha = 0.5f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
