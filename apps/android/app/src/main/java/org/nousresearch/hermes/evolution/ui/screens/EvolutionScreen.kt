package org.nousresearch.hermes.evolution.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.nousresearch.hermes.evolution.data.model.EvolutionRecord
import org.nousresearch.hermes.evolution.ui.theme.CyanGlow
import org.nousresearch.hermes.evolution.ui.theme.EmeraldReady
import org.nousresearch.hermes.evolution.ui.theme.RoseError
import org.nousresearch.hermes.evolution.ui.theme.SurfaceContainer
import org.nousresearch.hermes.evolution.ui.theme.SurfaceDark
import org.nousresearch.hermes.evolution.ui.theme.TextMuted
import org.nousresearch.hermes.evolution.ui.theme.TextPrimary
import org.nousresearch.hermes.evolution.ui.theme.TextSecondary
import org.nousresearch.hermes.evolution.ui.theme.VoidBlack

@Composable
fun EvolutionScreen() {
    val evolutionRecords = remember {
        listOf(
            EvolutionRecord(
                skillName = "pdf-data-extractor",
                version = "1.1.0",
                testDate = System.currentTimeMillis() - 3600000,
                passed = true,
                baselineScore = 72.0,
                improvedScore = 96.5,
                errorDetails = null,
                canRollback = true
            ),
            EvolutionRecord(
                skillName = "hindi-transliteration",
                version = "1.0.2",
                testDate = System.currentTimeMillis() - 86400000,
                passed = true,
                baselineScore = 80.0,
                improvedScore = 91.0,
                errorDetails = null,
                canRollback = true
            ),
            EvolutionRecord(
                skillName = "android-media-transcode",
                version = "0.9.0",
                testDate = System.currentTimeMillis() - 172800000,
                passed = false,
                baselineScore = 65.0,
                improvedScore = 58.0,
                errorDetails = "Exceeded 16KB memory page limit on Android 16 test",
                canRollback = true
            )
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(VoidBlack),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Evolution Laboratory", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Automated test history, capability improvement tracking, and rollback.", color = TextSecondary, fontSize = 13.sp)
        }

        // Summary Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Active Skills", color = TextSecondary, fontSize = 11.sp)
                        Text("187", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Avg Pass Rate", color = TextSecondary, fontSize = 11.sp)
                        Text("94.2%", color = EmeraldReady, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Self-Improvements", color = TextSecondary, fontSize = 11.sp)
                        Text("12 Verified", color = CyanGlow, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }

        item {
            Text("Recent Evolution History", color = TextSecondary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }

        items(evolutionRecords) { record ->
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(record.skillName, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Surface(
                            color = if (record.passed) EmeraldReady.copy(alpha = 0.2f) else RoseError.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = if (record.passed) "PASSED (${record.improvedScore}%)" else "FAILED",
                                color = if (record.passed) EmeraldReady else RoseError,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(6.dp, 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Version: v${record.version} · Baseline: ${record.baselineScore}% → Improved: ${record.improvedScore}%",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    if (record.errorDetails != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Failure Reason: ${record.errorDetails}",
                            color = RoseError,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = { /* Rollback */ },
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainer),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.Undo, contentDescription = null, tint = CyanGlow, modifier = Modifier.height(14.dp))
                            Text("Rollback Version", fontSize = 11.sp, color = TextPrimary)
                        }
                    }
                }
            }
        }
    }
}
