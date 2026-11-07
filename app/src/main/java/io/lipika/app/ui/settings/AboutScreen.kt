package io.lipika.app.ui.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.FontDownload
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.lipika.app.R

private const val GITHUB_REPO_URL = "https://github.com/salimshohan/Lipika"
private const val GITHUB_ISSUES_URL = "https://github.com/salimshohan/Lipika/issues"
private const val DEVELOPER_EMAIL = "salimshohan@proton.me"

private fun openUrl(context: Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    } catch (_: Exception) {
        Toast.makeText(context, "Could not open browser", Toast.LENGTH_SHORT).show()
    }
}

private fun sendEmail(context: Context) {
    try {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$DEVELOPER_EMAIL")
            putExtra(Intent.EXTRA_SUBJECT, "Lipika Keyboard Feedback")
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        Toast.makeText(context, "Could not open email client", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun AboutScreen(
    onResetDefaults: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val colors = LocalSettingsColors.current
    val strings = LocalLipikaStrings.current
    val isBn = strings === LipikaStringsBn
    val versionName = androidx.compose.runtime.remember {
        try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "0.2.6"
        } catch (_: Exception) {
            "0.2.6"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 18.dp, vertical = 16.dp)
            .padding(bottom = 32.dp)
    ) {
        // 1. Hero Brand Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
            border = BorderStroke(1.dp, colors.accent.copy(alpha = 0.35f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_launcher),
                    contentDescription = "Lipika Logo",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(20.dp))
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = strings.appNameHeader,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )

                Box(
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(colors.accent.copy(alpha = 0.15f))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isBn) "ভার্সন $versionName" else "Version $versionName",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.accent
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (isBn) "লিপিকা কীবোর্ড একটি উচ্চ ক্ষমতাসম্পন্ন বহুভাষিক বাংলা কীবোর্ড, যা বাংলা, ইংরেজি ও আরবি ভাষার দ্রুততম টাইপিং নিশ্চিত করে।" else "A next-generation, high-performance multilingual keyboard engineered with a hybrid Rust-Kotlin engine for ultra-fast Bengali, English, and Arabic typing.",
                    fontSize = 13.5.sp,
                    color = colors.textSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 19.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 2. Architectural Highlights & Features
        SettingsCategoryHeader(title = if (isBn) "মূল বৈশিষ্ট্যসমূহ" else "CORE ARCHITECTURE & FEATURES")

        SettingsGroup {
            AboutFeatureItem(
                icon = Icons.Outlined.Speed,
                title = if (isBn) "হাইব্রিড ফোনেটিক ইঞ্জিন" else "Hybrid Rust Phonetic Engine",
                description = if (isBn) "অভ্র ও প্রভাত লেআউটের জন্য অত্যন্ত দ্রুত ক্যান্ডিডেট প্রেডিকশন ও ট্রান্সলিটারেশন।" else "Zero-allocation native Rust core delivering microsecond-latency candidate prediction and transliteration for Avro & Provat.",
                position = GroupPosition.TOP
            )

            AboutFeatureItem(
                icon = Icons.Outlined.Lock,
                title = if (isBn) "সম্পূর্ণ অফলাইন ও নিরাপদ" else "100% Offline & Private",
                description = if (isBn) "কোনো ইন্টারনেট অনুমতির প্রয়োজন নেই। আপনার টাইপিং ও ডেটা সম্পূর্ণ ডিভাইসেই সুরক্ষিত থাকে।" else "Zero internet permissions requested. Keystrokes, clipboard items, and learned vocabulary never leave your device storage.",
                position = GroupPosition.MIDDLE
            )

            AboutFeatureItem(
                icon = Icons.Outlined.Translate,
                title = if (isBn) "সমৃদ্ধ বহুভাষিক লেআউট" else "Comprehensive Multilingual Layouts",
                description = if (isBn) "ইংরেজি, বাংলা প্রভাত, অভ্র ফোনেটিক, জাতীয় (BSTI) এবং আরবি লেআউট সমর্থিত।" else "Built-in English (QWERTY), Bengali Provat (প্রভাত), Bengali Avro (অভ্র), Bengali National (জাতীয় BSTI), and Arabic (العربية).",
                position = GroupPosition.MIDDLE
            )

            AboutFeatureItem(
                icon = Icons.Outlined.FontDownload,
                title = if (isBn) "কাস্টম ফন্ট স্টুডিও" else "Universal Font Studio",
                description = if (isBn) "যেকোনো বাংলা ও ইংরেজি কাস্টম ফন্ট নির্বাচন ও ব্যবহারের সুবিধা।" else "Custom typography renderer supporting built-in and external TTF/OTF fonts with full preview across all Bengali and Latin scripts.",
                position = GroupPosition.MIDDLE
            )

            AboutFeatureItem(
                icon = Icons.Outlined.TouchApp,
                title = if (isBn) "কার্সর ট্র্যাকপ্যাড ও জেসচার" else "Spacebar Cursor Trackpad & Gestures",
                description = if (isBn) "স্পেসবারে সোয়াইপ করে কার্সর পরিচালনা এবং ব্যাকস্পেস থেকে বামে সোয়াইপে টেক্সট মোছা।" else "Fluid horizontal thumb navigation on spacebar and selective word/character swipe delete from backspace.",
                position = GroupPosition.MIDDLE
            )

            AboutFeatureItem(
                icon = Icons.Outlined.Memory,
                title = if (isBn) "সুরক্ষিত স্যান্ডবক্সড ক্লিপবোর্ড" else "Permanent Sandboxed Clipboard",
                description = if (isBn) "টেক্সট ও ছবি নিরাপদে সংরক্ষণ ও পিন করে রাখার শক্তিশালী ক্লিপবোর্ড।" else "Persistent clipboard storage supporting text clips and rich image thumbnails safely preserved across reboots.",
                position = GroupPosition.BOTTOM
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 3. Developer Profile Card
        SettingsCategoryHeader(title = if (isBn) "ডেভেলপার পরিচিতি" else "LEAD DEVELOPER & CREATOR")

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
            border = BorderStroke(1.dp, colors.cardBorder)
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
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(colors.accent.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = "Developer",
                            tint = colors.accent,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = strings.developerName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isBn) "নির্মাতা ও একমাত্র মেইনটেইনার" else "Creator & Sole Maintainer",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = colors.accent
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Institution info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.School,
                        contentDescription = "Institution",
                        tint = colors.textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isBn) "রাজশাহী বিশ্ববিদ্যালয়" else "University of Rajshahi",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = if (isBn) "তথ্যবিজ্ঞান ও গ্রন্থাগার ব্যবস্থাপনা বিভাগ" else "Dept. of Information Science & Library Management",
                            fontSize = 12.sp,
                            color = colors.textSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Email info
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.inputBackground)
                        .clickable { sendEmail(context) }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Email,
                        contentDescription = "Email",
                        tint = colors.accent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Email Address",
                            fontSize = 11.sp,
                            color = colors.textSecondary
                        )
                        Text(
                            text = DEVELOPER_EMAIL,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = colors.textPrimary
                        )
                    }
                    Text(
                        text = "Send",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.accent
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // WhatsApp info
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.inputBackground)
                        .clickable { openUrl(context, "https://wa.me/8801522106283") }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Chat,
                        contentDescription = "WhatsApp",
                        tint = Color(0xFF25D366),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "WhatsApp",
                            fontSize = 11.sp,
                            color = colors.textSecondary
                        )
                        Text(
                            text = "01522106283",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = colors.textPrimary
                        )
                    }
                    Text(
                        text = "Chat",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF25D366)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Facebook info
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.inputBackground)
                        .clickable { openUrl(context, "https://facebook.com/salimhosenshohan") }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Public,
                        contentDescription = "Facebook",
                        tint = Color(0xFF1877F2),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Facebook Profile",
                            fontSize = 11.sp,
                            color = colors.textSecondary
                        )
                        Text(
                            text = "@salimhosenshohan",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = colors.textPrimary
                        )
                    }
                    Text(
                        text = "Open",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1877F2)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 4. Quick Links
        SettingsCategoryHeader(title = "PROJECT & REPOSITORY")

        SettingsGroup {
            SettingsNavigationItem(
                title = "GitHub Repository",
                subtitle = "Source code, release notes, and documentation",
                icon = Icons.Outlined.Code,
                position = GroupPosition.TOP,
                onClick = { openUrl(context, GITHUB_REPO_URL) }
            )

            SettingsNavigationItem(
                title = "Report an Issue",
                subtitle = "Submit bugs, crashes or request new features",
                icon = Icons.Outlined.BugReport,
                position = GroupPosition.BOTTOM,
                onClick = { openUrl(context, GITHUB_ISSUES_URL) }
            )
        }
    }
}

@Composable
private fun AboutFeatureItem(
    icon: ImageVector,
    title: String,
    description: String,
    position: GroupPosition
) {
    val colors = LocalSettingsColors.current

    SettingsItemCard(position = position) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(colors.accent.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = colors.accent,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = colors.textSecondary,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
