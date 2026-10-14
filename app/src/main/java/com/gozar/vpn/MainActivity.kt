package com.gozar.vpn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.windowInsetsPadding

private val Background = Color(0xFF07100E)
private val Surface = Color(0xFF0C1714)
private val Surface2 = Color(0xFF101F1A)
private val Border = Color(0xFF1D302A)
private val Muted = Color(0xFF71817A)
private val TextMain = Color(0xFFE7F0EB)
private val Accent = Color(0xFF25E6AD)
private val AccentDark = Color(0xFF173B30)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.rgb(7, 16, 14)
        window.navigationBarColor = android.graphics.Color.rgb(7, 16, 14)
        window.decorView.systemUiVisibility = 0
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = Accent,
                    background = Background,
                    surface = Surface,
                    onBackground = TextMain,
                    onSurface = TextMain
                )
            ) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    GozarScreen()
                }
            }
        }
    }
}

@Composable
private fun GozarScreen() {
    var connected by remember { mutableStateOf(false) }
    var menuOpen by remember { mutableStateOf(false) }
    var showAddMessage by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf("همه") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        TopBar(onMenu = { menuOpen = true }, onAdd = { showAddMessage = true })
        if (menuOpen) {
            AlertDialog(
                onDismissRequest = { menuOpen = false },
                containerColor = Surface2,
                title = { Text("منوی Gozar", color = TextMain) },
                text = { Text("این نسخه فقط رابط کاربری است.", color = Muted) },
                confirmButton = {
                    TextButton(onClick = { menuOpen = false }) { Text("بستن", color = Accent) }
                }
            )
        }
        if (showAddMessage) {
            AlertDialog(
                onDismissRequest = { showAddMessage = false },
                containerColor = Surface2,
                title = { Text("افزودن سرور", color = TextMain) },
                text = { Text("اتصال سرور در نسخه نمایشی پیاده‌سازی نشده است.", color = Muted) },
                confirmButton = {
                    TextButton(onClick = { showAddMessage = false }) { Text("متوجه شدم", color = Accent) }
                }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 26.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(AccentDark)
                    .border(1.dp, Color(0xFF258D6D), CircleShape)
                    .clickable { selectedTab = "همه" }
                    .padding(horizontal = 24.dp, vertical = 10.dp)
            ) {
                Text(selectedTab, color = Accent, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            MainConnectionCard(connected = connected, onToggle = { connected = !connected })

            ServerEmptyCard(
                onAdd = { showAddMessage = true },
                onBrowse = { showAddMessage = true }
            )
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun TopBar(onMenu: () -> Unit, onAdd: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(74.dp)
            .border(width = 0.5.dp, color = Border)
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onMenu) {
            Icon(Icons.Default.Menu, contentDescription = "منو", tint = TextMain, modifier = Modifier.size(27.dp))
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
            Text("Gozar", color = TextMain, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("فیلترشکن امن و سریع", color = Muted, fontSize = 12.sp)
        }
        IconButton(onClick = onAdd) {
            Icon(Icons.Default.Add, contentDescription = "افزودن", tint = TextMain, modifier = Modifier.size(28.dp))
        }
        IconButton(onClick = onAdd) {
            Icon(Icons.Default.FilterList, contentDescription = "فیلتر", tint = TextMain, modifier = Modifier.size(26.dp))
        }
    }
}

@Composable
private fun MainConnectionCard(connected: Boolean, onToggle: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Surface)
            .border(1.dp, Border, RoundedCornerShape(28.dp))
            .padding(horizontal = 24.dp, vertical = 26.dp)
    ) {
        Text("وضعیت اتصال", color = Muted, fontSize = 14.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End)
        Text(
            if (connected) "اتصال برقرار است" else "هیچ کانفیگی انتخاب نشده",
            color = TextMain,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.End
        )

        Spacer(Modifier.height(34.dp))
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(252.dp)
                    .border(7.dp, if (connected) Color(0xFF1A7659) else Color(0xFF172923), CircleShape)
                    .padding(10.dp)
                    .border(1.dp, if (connected) Accent else Color(0xFF294039), CircleShape)
                    .clip(CircleShape)
                    .background(if (connected) Color(0xFF102C22) else Surface2)
                    .clickable { onToggle() },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.PowerSettingsNew,
                        contentDescription = "اتصال",
                        tint = if (connected) Accent else Color(0xFF91A59B),
                        modifier = Modifier.size(42.dp)
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        if (connected) "متصل شد" else "اتصال",
                        color = TextMain,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        if (connected) "برای قطع، لمس کنید" else "برای بررسی بزنید",
                        color = Muted,
                        fontSize = 13.sp
                    )
                }
            }
        }
        Spacer(Modifier.height(34.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 0.5.dp, color = Border)
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Metric("پینگ", if (connected) "۴۲ ms" else "—")
            Metric("وضعیت", if (connected) "فعال" else "—")
            Metric("امنیت", if (connected) "—" else "—")
            Metric("پروتکل", if (connected) "VPN" else "—")
        }
        Spacer(Modifier.height(72.dp))
        HorizontalDivider(color = Border, thickness = 1.dp)
        Spacer(Modifier.height(18.dp))
        Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(Icons.Default.Security, contentDescription = null, tint = Muted, modifier = Modifier.size(18.dp))
            Text(
                "تونل واقعی در این نسخه فعال نیست؛ این صفحه فقط ظاهر برنامه را نمایش می‌دهد. برای اتصال واقعی باید سرویس VPN و کانفیگ سرور اضافه شود.",
                color = Muted,
                fontSize = 12.sp,
                lineHeight = 21.sp,
                textAlign = TextAlign.Start
            )
        }
    }
}

@Composable
private fun Metric(label: String, value: String) {
    Column(
        modifier = Modifier.width(64.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, color = Muted, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Clip)
        Spacer(Modifier.height(8.dp))
        Text(value, color = TextMain, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun ServerEmptyCard(onAdd: () -> Unit, onBrowse: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .border(1.dp, Border, RoundedCornerShape(26.dp))
            .padding(horizontal = 22.dp, vertical = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "هنوز کانفیگی اضافه نشده",
            color = Muted,
            fontSize = 19.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(Modifier.height(8.dp))
        Text("با دکمه + یا «سرورهای رایگان» شروع کنید.", color = Muted, fontSize = 14.sp, textAlign = TextAlign.Center)
        Spacer(Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = onBrowse,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(15.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Border),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMain),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 13.dp)
            ) {
                Icon(Icons.Default.Public, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(7.dp))
                Text("سرورهای رایگان", fontSize = 12.sp, maxLines = 1)
            }
            Button(
                onClick = onAdd,
                modifier = Modifier.weight(0.85f),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = Background),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 14.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(5.dp))
                Text("افزودن", fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.height(18.dp))
        Text("G O Z A R  •  VPN UI", color = Color(0xFF3D5A4F), fontSize = 10.sp, letterSpacing = 2.sp)
    }
}
