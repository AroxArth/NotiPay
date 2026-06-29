package com.notipay.app.ui.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.notipay.app.R
import com.notipay.app.data.local.PaymentEntity
import com.notipay.app.domain.formatSoles
import com.notipay.app.notification.PaymentAnnouncer
import com.notipay.app.ui.theme.CardSurface
import com.notipay.app.ui.theme.PausedAmber
import com.notipay.app.ui.theme.ScreenBackground
import com.notipay.app.ui.theme.SuccessGreen
import com.notipay.app.ui.theme.Teal
import com.notipay.app.ui.theme.TextPrimary
import com.notipay.app.ui.theme.TextSecondary
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    val context = LocalContext.current

    val isPaused by viewModel.isPaused.collectAsState()
    val filter by viewModel.filter.collectAsState()
    val payments by viewModel.payments.collectAsState()
    val totalCents by viewModel.totalCents.collectAsState()

    var accessEnabled by remember { mutableStateOf(isListenerEnabled(context)) }
    var batteryUnrestricted by remember { mutableStateOf(isIgnoringBatteryOptimizations(context)) }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        accessEnabled = isListenerEnabled(context)
        batteryUnrestricted = isIgnoringBatteryOptimizations(context)
    }

    val announcer = remember { PaymentAnnouncer(context) }
    DisposableEffect(Unit) {
        onDispose { announcer.shutdown() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("NotiPay", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(
                        onClick = {
                            context.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://github.com/AroxArth"),
                                ),
                            )
                        },
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_github),
                            contentDescription = "GitHub",
                            tint = Color.White,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Teal,
                    titleContentColor = Color.White,
                ),
            )
        },
        containerColor = ScreenBackground,
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item { Spacer(Modifier.height(4.dp)) }
            item {
                StatusCard(
                    accessEnabled = accessEnabled,
                    isPaused = isPaused,
                    batteryUnrestricted = batteryUnrestricted,
                    onGrantAccess = {
                        context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                    },
                    onTogglePause = viewModel::setPaused,
                    onTestVoice = { announcer.announceTest() },
                    onFixBattery = {
                        context.startActivity(
                            Intent(
                                Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                                Uri.parse("package:${context.packageName}"),
                            ),
                        )
                    },
                )
            }
            item {
                SummaryCard(filter = filter, totalCents = totalCents, count = payments.size)
            }
            item {
                FilterRow(selected = filter, onSelect = viewModel::setFilter)
            }
            if (payments.isEmpty()) {
                item { EmptyState() }
            } else {
                items(payments, key = { it.id }) { payment ->
                    PaymentCard(payment)
                }
            }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun StatusCard(
    accessEnabled: Boolean,
    isPaused: Boolean,
    batteryUnrestricted: Boolean,
    onGrantAccess: () -> Unit,
    onTogglePause: (Boolean) -> Unit,
    onTestVoice: () -> Unit,
    onFixBattery: () -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            val (statusText, statusColor) = when {
                !accessEnabled -> "Acceso a notificaciones desactivado" to PausedAmber
                isPaused -> "Pausado" to PausedAmber
                else -> "Escuchando los pagos de Yape" to SuccessGreen
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(Modifier.size(12.dp).clip(CircleShape).background(statusColor))
                Text(statusText, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
            }

            if (!accessEnabled) {
                Button(
                    onClick = onGrantAccess,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Teal),
                ) {
                    Text("Activar acceso a notificaciones")
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Pausar lectura", style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
                    Switch(
                        checked = isPaused,
                        onCheckedChange = onTogglePause,
                        colors = SwitchDefaults.colors(checkedTrackColor = PausedAmber),
                    )
                }
            }

            if (accessEnabled && !batteryUnrestricted) {
                OutlinedButton(onClick = onFixBattery, modifier = Modifier.fillMaxWidth()) {
                    Text("Evitar que el sistema lo cierre")
                }
            }

            OutlinedButton(onClick = onTestVoice, modifier = Modifier.fillMaxWidth()) {
                Text("Prueba de audio")
            }
        }
    }
}

@Composable
private fun SummaryCard(filter: DateFilter, totalCents: Long, count: Int) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Teal),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                "Total · ${filter.label}",
                color = Color.White.copy(alpha = 0.85f),
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                formatSoles(totalCents),
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
            )
            Text(
                "$count ${if (count == 1) "pago recibido" else "pagos recibidos"}",
                color = Color.White.copy(alpha = 0.85f),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterRow(selected: DateFilter, onSelect: (DateFilter) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        DateFilter.entries.forEach { option ->
            FilterChip(
                selected = option == selected,
                onClick = { onSelect(option) },
                label = { Text(option.label) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Teal,
                    selectedLabelColor = Color.White,
                ),
            )
        }
    }
}

@Composable
private fun PaymentCard(payment: PaymentEntity) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier.size(44.dp).clip(CircleShape).background(Teal.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    payment.senderName.take(1).uppercase(Locale("es")),
                    color = Teal,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    payment.senderName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    formatTime(payment.receivedAt),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                )
            }
            Text(
                formatSoles(payment.amountCents),
                style = MaterialTheme.typography.titleMedium,
                color = SuccessGreen,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            "Todavía no hay pagos en este período",
            color = TextSecondary,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

private fun isListenerEnabled(context: Context): Boolean {
    val enabled = Settings.Secure.getString(
        context.contentResolver,
        "enabled_notification_listeners",
    ) ?: return false
    return enabled.split(":").any { it.contains(context.packageName) }
}

private fun isIgnoringBatteryOptimizations(context: Context): Boolean {
    val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    return powerManager.isIgnoringBatteryOptimizations(context.packageName)
}

private val timeFormatter = DateTimeFormatter.ofPattern("dd MMM, HH:mm", Locale("es"))

private fun formatTime(millis: Long): String =
    Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).format(timeFormatter)
