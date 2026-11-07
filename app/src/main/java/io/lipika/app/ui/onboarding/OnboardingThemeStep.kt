package io.lipika.app.ui.onboarding

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
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class ThemeOptionData(
    val id: String,
    val title: String,
    val subtitle: String,
    val gradientBrush: Brush
)

@Composable
fun OnboardingThemeStep(
    initialTheme: String = "ocean_teal",
    onContinue: (selectedThemeId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTheme by remember {
        mutableStateOf(
            if (initialTheme in listOf("ocean_teal", "sunset_coral", "minimal_grey", "soft_lavender")) {
                initialTheme
            } else {
                "ocean_teal"
            }
        )
    }

    val themes = remember {
        listOf(
            ThemeOptionData(
                id = "ocean_teal",
                title = "Ocean Teal",
                subtitle = "Fresh & energizing",
                gradientBrush = Brush.horizontalGradient(
                    listOf(Color(0xFF14B8A6), Color(0xFF2DD4BF), Color(0xFF5EEAD4))
                )
            ),
            ThemeOptionData(
                id = "sunset_coral",
                title = "Sunset Coral",
                subtitle = "Warm & creative",
                gradientBrush = Brush.horizontalGradient(
                    listOf(Color(0xFFF97316), Color(0xFFFB923C), Color(0xFFFBBF24))
                )
            ),
            ThemeOptionData(
                id = "minimal_grey",
                title = "Minimal Grey",
                subtitle = "Clean & focused",
                gradientBrush = Brush.horizontalGradient(
                    listOf(Color(0xFF4B5563), Color(0xFF6B7280), Color(0xFF9CA3AF))
                )
            ),
            ThemeOptionData(
                id = "soft_lavender",
                title = "Soft lavender",
                subtitle = "Calm & soothing",
                gradientBrush = Brush.horizontalGradient(
                    listOf(Color(0xFF8B5CF6), Color(0xFFA78BFA), Color(0xFFC4B5FD))
                )
            )
        )
    }

    val backgroundGradient = Brush.verticalGradient(
        listOf(
            Color(0xFFE2F7F4),
            Color(0xFFC6EFE9),
            Color(0xFF80DBCB),
            Color(0xFF40C2AF)
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp, bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Palette Squircle Badge
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .shadow(12.dp, RoundedCornerShape(18.dp), ambientColor = Color(0xFF134E4A).copy(alpha = 0.25f))
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF1B6B63), Color(0xFF2B8C80), Color(0xFF38A394))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Palette,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Title
            Text(
                text = "Customize your space",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F2E2B),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Subtitle
            Text(
                text = "Choose a theme that feels right for you",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF2C5550),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 2x2 Grid of Theme Cards
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                themes.chunked(2).forEach { rowThemes ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        rowThemes.forEach { item ->
                            val isSelected = selectedTheme == item.id
                            OnboardingThemeCard(
                                item = item,
                                isSelected = isSelected,
                                modifier = Modifier.weight(1f),
                                onClick = { selectedTheme = item.id }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f, fill = false))
            Spacer(modifier = Modifier.height(36.dp))

            // Bottom Continue Button
            Button(
                onClick = { onContinue(selectedTheme) },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF064E3B),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .shadow(10.dp, RoundedCornerShape(16.dp), ambientColor = Color(0xFF064E3B).copy(alpha = 0.3f))
            ) {
                Text(
                    text = "Continue",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun OnboardingThemeCard(
    item: ThemeOptionData,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 6.dp else 2.dp),
        border = BorderStroke(
            width = if (isSelected) 2.5.dp else 1.dp,
            color = if (isSelected) Color(0xFF064E3B) else Color.White.copy(alpha = 0.9f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Preview Pill
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(item.gradientBrush)
            ) {
                // Miniature white base keyboard representation
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(26.dp)
                        .align(Alignment.BottomCenter)
                        .background(Color.White.copy(alpha = 0.92f))
                )

                // Selection checkmark
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .padding(6.dp)
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF064E3B))
                            .align(Alignment.TopEnd),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Selected",
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Title
            Text(
                text = item.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A),
                textAlign = TextAlign.Start,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(3.dp))

            // Subtitle
            Text(
                text = item.subtitle,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Start,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
