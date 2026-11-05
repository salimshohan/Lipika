package io.kiebord.app.ui.settings

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.kiebord.data.prefs.KiebordPreferences

private data class FontItem(
    val id: String,
    val name: String
)

@Composable
fun FontsScreen(
    preferences: KiebordPreferences,
    onUpdateSelectedFont: (String) -> Unit,
    onUpdateCustomFontPath: (String) -> Unit,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val colors = LocalSettingsColors.current
    val scrollState = rememberScrollState()

    val fontLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val targetFile = java.io.File(context.filesDir, "custom_font.ttf")
                targetFile.outputStream().use { out -> inputStream?.copyTo(out) }
                onUpdateCustomFontPath(targetFile.absolutePath)
                onUpdateSelectedFont("custom")
                Toast.makeText(context, "Custom font imported successfully!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to import font: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val fontList = remember {
        listOf(
            FontItem(KiebordPreferences.FONT_TW_CENT_MT, "Tw Cent MT"),
            FontItem(KiebordPreferences.FONT_KALPURUSH, "Kalpurush (কালপুরুষ)"),
            FontItem(KiebordPreferences.FONT_SEGOE_UI, "Segoe UI"),
            FontItem(KiebordPreferences.FONT_GEORGIA, "Georgia"),
            FontItem(KiebordPreferences.FONT_CONSOLAS, "Consolas"),
            FontItem(KiebordPreferences.FONT_CANDARA, "Candara"),
            FontItem(KiebordPreferences.FONT_COMIC_SANS, "Comic Sans"),
            FontItem(KiebordPreferences.FONT_TREBUCHET, "Trebuchet MS"),
            FontItem(KiebordPreferences.FONT_VERDANA, "Verdana"),
            FontItem(KiebordPreferences.FONT_CALIBRI, "Calibri"),
            FontItem(KiebordPreferences.FONT_CORBEL, "Corbel")
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Custom font import card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { fontLauncher.launch(arrayOf("*/*")) },
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
            border = BorderStroke(1.dp, colors.accent.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.accent.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = "Import",
                        tint = colors.accent,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Import Custom Font",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Text(
                        text = if (preferences.selectedFont == "custom" && preferences.customFontPath.isNotBlank())
                            "Active: Custom font loaded"
                        else
                            "Select any .ttf or .otf file from storage",
                        fontSize = 12.sp,
                        color = colors.textSecondary
                    )
                }
                if (preferences.selectedFont == "custom") {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(colors.accent),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Active",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Predefined fonts
        fontList.forEach { item ->
            val isSelected = preferences.selectedFont == item.id
            val resolvedFontFamily = remember(item.id) {
                io.kiebord.theme.resolveKeyboardFontFamily(item.id)
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onUpdateSelectedFont(item.id)
                        Toast.makeText(context, "Applied: ${item.name}", Toast.LENGTH_SHORT).show()
                    },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
                border = BorderStroke(
                    1.dp,
                    if (isSelected) colors.accent else colors.cardBorder
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) colors.accent else colors.textPrimary,
                            modifier = Modifier.weight(1f)
                        )

                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(colors.accent),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = "Selected",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Preview sentence: The quick brown fox jumps over the lazy dog
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(colors.inputBackground)
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "The quick brown fox jumps over the lazy dog",
                            fontFamily = resolvedFontFamily,
                            fontSize = 14.5.sp,
                            color = colors.textPrimary
                        )
                    }
                }
            }
        }
    }
}
