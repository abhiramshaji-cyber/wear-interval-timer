package com.abhiram.intervaltimer

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.Text

private val WorkColor = Color(0xFF4CAF50)
private val RestColor = Color(0xFF42A5F5)
private val ReadyColor = Color(0xFFFFC107)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Wear sleeps the display within seconds otherwise, hiding the count mid-interval.
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContent { App() }
    }
}

@Composable
fun App(vm: TimerViewModel = viewModel()) {
    var showSettings by remember { mutableStateOf(false) }
    if (showSettings) {
        SettingsScreen(
            work = vm.workSeconds,
            rest = vm.restSeconds,
            onDone = { work, rest ->
                vm.applyDurations(work, rest)
                showSettings = false
            },
        )
    } else {
        TimerScreen(
            vm = vm,
            onOpenSettings = {
                vm.pause()
                showSettings = true
            },
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TimerScreen(vm: TimerViewModel, onOpenSettings: () -> Unit) {
    val accent = when (vm.phase) {
        Phase.READY -> ReadyColor
        Phase.WORK -> WorkColor
        Phase.REST -> RestColor
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .combinedClickable(
                onClick = { vm.togglePause() },
                onLongClick = { vm.reset() },
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = vm.phase.label,
                color = accent,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = formatSeconds(vm.secondsLeft),
                color = Color.White,
                fontSize = 54.sp,
                fontWeight = FontWeight.Bold,
            )
            // Rendered even when running so pausing never shifts the layout.
            Text(
                text = if (vm.running) "" else "PAUSED",
                color = Color(0xFF9E9E9E),
                fontSize = 14.sp,
            )
            Text(
                text = "SETTINGS",
                color = Color(0xFF757575),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable(onClick = onOpenSettings)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            )
        }
    }
}

@Composable
fun SettingsScreen(work: Int, rest: Int, onDone: (Int, Int) -> Unit) {
    var workSeconds by remember { mutableIntStateOf(work) }
    var restSeconds by remember { mutableIntStateOf(rest) }
    BackHandler { onDone(workSeconds, restSeconds) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            DurationRow("WORK", WorkColor, workSeconds) { workSeconds = it }
            DurationRow("REST", RestColor, restSeconds) { restSeconds = it }
            Text(
                text = "DONE",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable { onDone(workSeconds, restSeconds) }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }
    }
}

@Composable
private fun DurationRow(label: String, color: Color, seconds: Int, onChange: (Int) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = label,
            color = color,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(44.dp),
        )
        Stepper("–") { onChange(snapDuration(seconds - STEP_SECONDS)) }
        Text(
            text = formatSeconds(seconds),
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(58.dp),
        )
        Stepper("+") { onChange(snapDuration(seconds + STEP_SECONDS)) }
    }
}

@Composable
private fun Stepper(symbol: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = symbol, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
    }
}

private fun formatSeconds(total: Int) = "${total / 60}:${(total % 60).toString().padStart(2, '0')}"
