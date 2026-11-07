package io.lipika.app.ui.settings

import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.material.icons.outlined.Vibration
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.lipika.data.prefs.LipikaPreferences

@Composable
fun SoundVibrationScreen(
    preferences: LipikaPreferences,
    onUpdateSoundOnKeypress: (Boolean) -> Unit,
    onUpdateSoundVolume: (Int) -> Unit,
    onUpdateSoundProfile: (String) -> Unit = {},
    onUpdateSoundDistinctKeys: (Boolean) -> Unit = {},
    onUpdateVibrateOnKeypress: (Boolean) -> Unit,
    onUpdateVibrationType: (String) -> Unit,
    onUpdateVibrationStrength: (Int) -> Unit,
    onUpdateVibrationDuration: (Int) -> Unit,
    onUpdateVibrateOnLongPress: (Boolean) -> Unit = {},
    onUpdateVibrateOnSpaceSwipe: (Boolean) -> Unit = {},
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val colors = LocalSettingsColors.current
    val scrollState = rememberScrollState()

    fun testFeedback(keyType: String) {
        if (preferences.vibrateOnKeypress) {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
            if (preferences.vibrationType == LipikaPreferences.VIBRATION_TYPE_HAPTIC) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val effect = when (keyType) {
                        "long" -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK)
                        else -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                    }
                    vibrator?.vibrate(effect)
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(20L)
                }
            } else {
                val dur = preferences.vibrationDurationMs.toLong().coerceIn(5L, 100L)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val amp = ((preferences.vibrationStrength / 100f) * 255).toInt().coerceIn(1, 255)
                    vibrator?.vibrate(VibrationEffect.createOneShot(dur, amp))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(dur)
                }
            }
        }
        if (preferences.soundOnKeypress) {
            val am = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
            val vol = (preferences.soundVolume / 100f).coerceIn(0.01f, 1.0f)
            val fx = when (keyType) {
                "space" -> AudioManager.FX_KEYPRESS_SPACEBAR
                "delete" -> AudioManager.FX_KEYPRESS_DELETE
                "action" -> AudioManager.FX_KEYPRESS_RETURN
                else -> AudioManager.FX_KEYPRESS_STANDARD
            }
            try {
                am?.playSoundEffect(fx, vol)
            } catch (_: Throwable) {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .padding(bottom = 36.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // ==========================================
        // SECTION 1: INTERACTIVE FEEDBACK TESTER
        // ==========================================
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = colors.accent.copy(alpha = 0.10f)),
            border = BorderStroke(1.dp, colors.accent.copy(alpha = 0.35f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(colors.accent.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.TouchApp,
                            contentDescription = null,
                            tint = colors.accent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Tap to Test Feedback",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "Tap any test button below to feel vibration and hear key audio",
                            fontSize = 11.5.sp,
                            color = colors.textSecondary
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { testFeedback("normal") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colors.cardBackground),
                        border = BorderStroke(1.dp, colors.cardBorder)
                    ) {
                        Text(
                            text = "Letter Key",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textPrimary
                        )
                    }
                    Button(
                        onClick = { testFeedback("space") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colors.cardBackground),
                        border = BorderStroke(1.dp, colors.cardBorder)
                    ) {
                        Text(
                            text = "Spacebar",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textPrimary
                        )
                    }
                    Button(
                        onClick = { testFeedback("delete") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colors.cardBackground),
                        border = BorderStroke(1.dp, colors.cardBorder)
                    ) {
                        Text(
                            text = "Delete",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textPrimary
                        )
                    }
                }
            }
        }

        // ==========================================
        // SECTION 2: SOUND FEEDBACK
        // ==========================================
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
            border = BorderStroke(1.dp, colors.cardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(colors.accent.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.VolumeUp,
                            contentDescription = null,
                            tint = colors.accent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Sound on Keypress",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "Play audible click sound on every keystroke",
                            fontSize = 12.sp,
                            color = colors.textSecondary
                        )
                    }
                    Switch(
                        checked = preferences.soundOnKeypress,
                        onCheckedChange = onUpdateSoundOnKeypress,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = colors.accent
                        )
                    )
                }

                AnimatedVisibility(
                    visible = preferences.soundOnKeypress,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Sound Volume Slider
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Sound Volume",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = colors.textPrimary
                                )
                                Text(
                                    text = "${preferences.soundVolume}%",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.accent
                                )
                            }
                            Slider(
                                value = preferences.soundVolume.toFloat(),
                                onValueChange = { onUpdateSoundVolume(it.toInt()) },
                                valueRange = 0f..100f,
                                colors = SliderDefaults.colors(
                                    thumbColor = colors.accent,
                                    activeTrackColor = colors.accent,
                                    inactiveTrackColor = colors.cardBorder
                                )
                            )
                        }

                        // Sound Profiles Selector (2x2 grid)
                        Text(
                            text = "Key Sound Profile",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textPrimary
                        )

                        val profiles = listOf(
                            Triple(LipikaPreferences.SOUND_PROFILE_DEFAULT, "Modern Crisp", "Sharp clean tap"),
                            Triple(LipikaPreferences.SOUND_PROFILE_MECHANICAL, "Mechanical Switch", "Tactile clicky switch"),
                            Triple(LipikaPreferences.SOUND_PROFILE_TYPEWRITER, "Retro Typewriter", "Classic vintage strike"),
                            Triple(LipikaPreferences.SOUND_PROFILE_BUBBLE, "Soft Bubble", "Gentle popped droplet")
                        )

                        profiles.chunked(2).forEach { rowProfiles ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                rowProfiles.forEach { (profileKey, title, desc) ->
                                    val isSelected = preferences.soundProfile == profileKey
                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { onUpdateSoundProfile(profileKey) },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isSelected) colors.accent.copy(alpha = 0.12f) else colors.inputBackground
                                        ),
                                        border = BorderStroke(
                                            1.dp,
                                            if (isSelected) colors.accent else colors.cardBorder
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Outlined.MusicNote,
                                                    contentDescription = null,
                                                    tint = if (isSelected) colors.accent else colors.textSecondary,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                if (isSelected) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(16.dp)
                                                            .clip(CircleShape)
                                                            .background(colors.accent),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Filled.Check,
                                                            contentDescription = null,
                                                            tint = Color.White,
                                                            modifier = Modifier.size(10.dp)
                                                        )
                                                    }
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = title,
                                                fontSize = 12.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) colors.accent else colors.textPrimary,
                                                maxLines = 1
                                            )
                                            Text(
                                                text = desc,
                                                fontSize = 10.5.sp,
                                                color = colors.textSecondary,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Key-Specific Audio Pitch Switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Key-Specific Audio Pitch",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colors.textPrimary
                                )
                                Text(
                                    text = "Distinct pitch for Spacebar, Backspace & Enter",
                                    fontSize = 11.5.sp,
                                    color = colors.textSecondary
                                )
                            }
                            Switch(
                                checked = preferences.soundDistinctKeys,
                                onCheckedChange = onUpdateSoundDistinctKeys,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = colors.accent
                                )
                            )
                        }
                    }
                }
            }
        }

        // ==========================================
        // SECTION 3: VIBRATION & HAPTICS
        // ==========================================
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
            border = BorderStroke(1.dp, colors.cardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(colors.accent.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Vibration,
                            contentDescription = null,
                            tint = colors.accent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Vibration on Keypress",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "Tactile touch feedback when tapping keys",
                            fontSize = 12.sp,
                            color = colors.textSecondary
                        )
                    }
                    Switch(
                        checked = preferences.vibrateOnKeypress,
                        onCheckedChange = onUpdateVibrateOnKeypress,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = colors.accent
                        )
                    )
                }

                AnimatedVisibility(
                    visible = preferences.vibrateOnKeypress,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Feedback Mode",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textPrimary
                        )

                        // Feedback mode options
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            val isHaptic = preferences.vibrationType == LipikaPreferences.VIBRATION_TYPE_HAPTIC

                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onUpdateVibrationType(LipikaPreferences.VIBRATION_TYPE_HAPTIC) },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isHaptic) colors.accent.copy(alpha = 0.12f) else colors.inputBackground
                                ),
                                border = BorderStroke(
                                    1.dp,
                                    if (isHaptic) colors.accent else colors.cardBorder
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.TouchApp,
                                            contentDescription = null,
                                            tint = if (isHaptic) colors.accent else colors.textSecondary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        if (isHaptic) {
                                            Box(
                                                modifier = Modifier
                                                    .size(18.dp)
                                                    .clip(CircleShape)
                                                    .background(colors.accent),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.Check,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = "Haptic Click",
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isHaptic) colors.accent else colors.textPrimary
                                    )
                                    Text(
                                        text = "Crisp system tap",
                                        fontSize = 11.sp,
                                        color = colors.textSecondary
                                    )
                                }
                            }

                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onUpdateVibrationType(LipikaPreferences.VIBRATION_TYPE_VIBRATION) },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (!isHaptic) colors.accent.copy(alpha = 0.12f) else colors.inputBackground
                                ),
                                border = BorderStroke(
                                    1.dp,
                                    if (!isHaptic) colors.accent else colors.cardBorder
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Vibration,
                                            contentDescription = null,
                                            tint = if (!isHaptic) colors.accent else colors.textSecondary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        if (!isHaptic) {
                                            Box(
                                                modifier = Modifier
                                                    .size(18.dp)
                                                    .clip(CircleShape)
                                                    .background(colors.accent),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.Check,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = "Custom Vibe",
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (!isHaptic) colors.accent else colors.textPrimary
                                    )
                                    Text(
                                        text = "Duration & strength",
                                        fontSize = 11.sp,
                                        color = colors.textSecondary
                                    )
                                }
                            }
                        }

                        // Sliders for strength and duration when custom vibe is chosen
                        if (preferences.vibrationType == LipikaPreferences.VIBRATION_TYPE_VIBRATION) {
                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Vibration Strength",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = colors.textPrimary
                                )
                                Text(
                                    text = "${preferences.vibrationStrength}%",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.accent
                                )
                            }
                            Slider(
                                value = preferences.vibrationStrength.toFloat(),
                                onValueChange = { onUpdateVibrationStrength(it.toInt()) },
                                valueRange = 1f..100f,
                                colors = SliderDefaults.colors(
                                    thumbColor = colors.accent,
                                    activeTrackColor = colors.accent,
                                    inactiveTrackColor = colors.cardBorder
                                )
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Vibration Duration",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = colors.textPrimary
                                )
                                Text(
                                    text = "${preferences.vibrationDurationMs} ms",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.accent
                                )
                            }
                            Slider(
                                value = preferences.vibrationDurationMs.toFloat(),
                                onValueChange = { onUpdateVibrationDuration(it.toInt()) },
                                valueRange = 5f..80f,
                                colors = SliderDefaults.colors(
                                    thumbColor = colors.accent,
                                    activeTrackColor = colors.accent,
                                    inactiveTrackColor = colors.cardBorder
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Vibrate on Long Press
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Vibrate on Long-Press",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colors.textPrimary
                                )
                                Text(
                                    text = "Gentle haptic click when secondary symbol or popup triggers",
                                    fontSize = 11.5.sp,
                                    color = colors.textSecondary
                                )
                            }
                            Switch(
                                checked = preferences.vibrateOnLongPress,
                                onCheckedChange = onUpdateVibrateOnLongPress,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = colors.accent
                                )
                            )
                        }

                        // Vibrate on Spacebar Swipe
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Vibrate on Spacebar Swipe",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colors.textPrimary
                                )
                                Text(
                                    text = "Subtle tick while navigating cursor along text",
                                    fontSize = 11.5.sp,
                                    color = colors.textSecondary
                                )
                            }
                            Switch(
                                checked = preferences.vibrateOnSpaceSwipe,
                                onCheckedChange = onUpdateVibrateOnSpaceSwipe,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = colors.accent
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
