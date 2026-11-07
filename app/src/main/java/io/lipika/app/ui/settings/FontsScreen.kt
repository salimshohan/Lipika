package io.lipika.app.ui.settings

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.FontDownload
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.lipika.data.prefs.LipikaPreferences
import io.lipika.theme.resolveBengaliFontFamily
import io.lipika.theme.resolveKeyboardFontFamily

private data class FontItem(
    val id: String,
    val name: String,
    val isBengali: Boolean = false
)

@Composable
fun FontsScreen(
    preferences: LipikaPreferences,
    onUpdateSelectedFont: (String) -> Unit,
    onUpdateSelectedBengaliFont: (String) -> Unit,
    onUpdateCustomFontPath: (String) -> Unit,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val colors = LocalSettingsColors.current
    val scrollState = rememberScrollState()

    var selectedTab by remember { mutableIntStateOf(0) }

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

    val englishFonts = remember {
        listOf(
            FontItem(LipikaPreferences.FONT_TW_CENT_MT, "Tw Cent MT"),
            FontItem(LipikaPreferences.FONT_SEGOE_UI, "Segoe UI"),
            FontItem(LipikaPreferences.FONT_GEORGIA, "Georgia"),
            FontItem(LipikaPreferences.FONT_CONSOLAS, "Consolas"),
            FontItem(LipikaPreferences.FONT_CANDARA, "Candara"),
            FontItem(LipikaPreferences.FONT_COMIC_SANS, "Comic Sans"),
            FontItem(LipikaPreferences.FONT_TREBUCHET, "Trebuchet MS"),
            FontItem(LipikaPreferences.FONT_VERDANA, "Verdana"),
            FontItem(LipikaPreferences.FONT_CALIBRI, "Calibri"),
            FontItem(LipikaPreferences.FONT_CORBEL, "Corbel"),
            FontItem(LipikaPreferences.FONT_ROBOTO, "Roboto"),
            FontItem(LipikaPreferences.FONT_INTER, "Inter"),
            FontItem(LipikaPreferences.FONT_POPPINS, "Poppins"),
            FontItem(LipikaPreferences.FONT_OUTFIT, "Outfit"),
            FontItem(LipikaPreferences.FONT_MONTSERRAT, "Montserrat")
        )
    }

    val bengaliFonts = remember {
        listOf(
            FontItem(LipikaPreferences.FONT_KALPURUSH, "Kalpurush (কালপুরুষ)", isBengali = true),
            FontItem(LipikaPreferences.FONT_SOLAIMAN_LIPI, "SolaimanLipi (সোলাইমান লিপি)", isBengali = true),
            FontItem(LipikaPreferences.FONT_SIYAM_RUPALI, "Siyam Rupali (সিয়াম রূপালী)", isBengali = true),
            FontItem(LipikaPreferences.FONT_NOTO_SANS_BENGALI, "Noto Sans Bengali (নোটো সান্স)", isBengali = true),
            FontItem(LipikaPreferences.FONT_HIND_SILIGURI, "Hind Siliguri (হিন্দ শিলিগুড়ি)", isBengali = true),
            FontItem(LipikaPreferences.FONT_APONA_LOHIT, "AponaLohit (আপন লোহিত)", isBengali = true),
            FontItem(LipikaPreferences.FONT_BENSEN, "BenSen (বেনসেন)", isBengali = true),
            FontItem(LipikaPreferences.FONT_GALADA, "Galada (গলদা)", isBengali = true),
            FontItem(LipikaPreferences.FONT_ATMA, "Atma (আত্মা)", isBengali = true),
            FontItem(LipikaPreferences.FONT_MINA, "Mina (মীনা)", isBengali = true),
            FontItem(LipikaPreferences.FONT_SHONAR_BANGLA, "Shonar Bangla (সোনার বাংলা)", isBengali = true),
            FontItem(LipikaPreferences.FONT_SUTONNY, "SutonnyMJ (সুতন্বী)", isBengali = true),
            FontItem(LipikaPreferences.FONT_CHARU_CHANDAN, "Charu Chandan (চারু চন্দন)", isBengali = true),
            FontItem(LipikaPreferences.FONT_SAGAR, "Sagar (সাগর)", isBengali = true),
            FontItem(LipikaPreferences.FONT_ADOR, "Ador (আদর)", isBengali = true)
        )
    }

    val activeEnglishFontName = remember(preferences.selectedFont) {
        if (preferences.selectedFont == "custom") "Custom Font"
        else englishFonts.find { it.id == preferences.selectedFont }?.name ?: "Default"
    }

    val activeBengaliFontName = remember(preferences.selectedBengaliFont) {
        bengaliFonts.find { it.id == preferences.selectedBengaliFont }?.name ?: "Kalpurush"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .padding(bottom = 36.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        // Active Dual-Selection Summary Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = colors.accent.copy(alpha = 0.08f)),
            border = BorderStroke(1.dp, colors.accent.copy(alpha = 0.25f))
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
                        text = "Active English",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.accent
                    )
                    Text(
                        text = activeEnglishFontName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                }
                Box(
                    modifier = Modifier
                        .height(28.dp)
                        .width(1.dp)
                        .background(colors.cardBorder)
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 14.dp)
                ) {
                    Text(
                        text = "Active Bengali",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.accent
                    )
                    Text(
                        text = activeBengaliFontName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                }
            }
        }

        // Tabs: English Fonts vs Bengali Fonts
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(colors.cardBackground)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // English Tab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (selectedTab == 0) colors.accent else Color.Transparent)
                    .clickable { selectedTab = 0 }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.TextFields,
                        contentDescription = null,
                        tint = if (selectedTab == 0) Color.White else colors.textSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "English (${englishFonts.size})",
                        fontSize = 13.5.sp,
                        fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                        color = if (selectedTab == 0) Color.White else colors.textPrimary
                    )
                }
            }

            // Bengali Tab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (selectedTab == 1) colors.accent else Color.Transparent)
                    .clickable { selectedTab = 1 }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Language,
                        contentDescription = null,
                        tint = if (selectedTab == 1) Color.White else colors.textSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Bengali (${bengaliFonts.size})",
                        fontSize = 13.5.sp,
                        fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                        color = if (selectedTab == 1) Color.White else colors.textPrimary
                    )
                }
            }
        }

        if (selectedTab == 0) {
            // ==========================================
            // ENGLISH FONTS TAB
            // ==========================================
            // Custom font import card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { fontLauncher.launch(arrayOf("*/*")) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
                border = BorderStroke(1.dp, colors.accent.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
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
                            imageVector = Icons.Outlined.Add,
                            contentDescription = "Import",
                            tint = colors.accent,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Import Custom English Font",
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = if (preferences.selectedFont == "custom" && preferences.customFontPath.isNotBlank())
                                "Active: Custom font loaded"
                            else
                                "Select any .ttf or .otf file from storage",
                            fontSize = 11.5.sp,
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

            englishFonts.forEach { item ->
                val isSelected = preferences.selectedFont == item.id
                val resolvedFontFamily = remember(item.id) {
                    resolveKeyboardFontFamily(item.id)
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
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.name,
                                fontSize = 15.sp,
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

                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(colors.inputBackground)
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "The quick brown fox jumps over the lazy dog",
                                fontFamily = resolvedFontFamily,
                                fontSize = 14.sp,
                                color = colors.textPrimary
                            )
                        }
                    }
                }
            }
        } else {
            // ==========================================
            // BENGALI FONTS TAB
            // ==========================================
            bengaliFonts.forEach { item ->
                val isSelected = preferences.selectedBengaliFont == item.id
                val resolvedFontFamily = remember(item.id) {
                    resolveBengaliFontFamily(item.id)
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onUpdateSelectedBengaliFont(item.id)
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
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.name,
                                fontSize = 15.sp,
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

                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(colors.inputBackground)
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "আমার সোনার বাংলা, আমি তোমায় ভালোবাসি",
                                fontFamily = resolvedFontFamily,
                                fontSize = 14.sp,
                                color = colors.textPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}
