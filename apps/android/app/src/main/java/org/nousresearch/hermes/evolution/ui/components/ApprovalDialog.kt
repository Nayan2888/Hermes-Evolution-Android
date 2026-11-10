package org.nousresearch.hermes.evolution.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.nousresearch.hermes.evolution.ui.theme.AmberWaiting
import org.nousresearch.hermes.evolution.ui.theme.EmeraldReady
import org.nousresearch.hermes.evolution.ui.theme.RoseError
import org.nousresearch.hermes.evolution.ui.theme.SurfaceContainer
import org.nousresearch.hermes.evolution.ui.theme.SurfaceDark
import org.nousresearch.hermes.evolution.ui.theme.TextPrimary
import org.nousresearch.hermes.evolution.ui.theme.TextSecondary

@Composable
fun ApprovalDialog(
    callId: String,
    toolName: String,
    arguments: Map<String, Any?>,
    riskLevel: String,
    onDecision: (Boolean) -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDecision(false) },
        containerColor = SurfaceDark,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Security Authorization",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    color = when (riskLevel.uppercase()) {
                        "CRITICAL" -> RoseError
                        "HIGH" -> AmberWaiting
                        else -> AmberWaiting.copy(alpha = 0.5f)
                    },
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = riskLevel.uppercase(),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SurfaceDark
                    )
                }
            }
        },
        text = {
            Column {
                Text(
                    text = "Hermes Agent requests permission to execute:",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = toolName,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = SurfaceContainer,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Parameters:",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = arguments.entries.joinToString("\n") { (k, v) -> "$k: $v" },
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onDecision(true) },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldReady)
            ) {
                Text("Allow", color = SurfaceDark, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = { onDecision(false) },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = RoseError)
            ) {
                Text("Deny")
            }
        }
    )
}
