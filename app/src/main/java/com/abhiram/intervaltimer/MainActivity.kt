package com.abhiram.intervaltimer

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.Text

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Wear sleeps the display within seconds otherwise, hiding the count mid-interval.
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContent { TimerScreen() }
    }
}

@Composable
fun TimerScreen(vm: TimerViewModel = viewModel()) {
    val accent = when (vm.phase) {
        Phase.READY -> Color(0xFFFFC107)
        Phase.WORK -> Color(0xFF4CAF50)
        Phase.REST -> Color(0xFF42A5F5)
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable { vm.togglePause() },
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
        }
    }
}

private fun formatSeconds(total: Int) = "${total / 60}:${(total % 60).toString().padStart(2, '0')}"
