package org.nousresearch.hermes.evolution.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.nousresearch.hermes.evolution.ui.theme.CyanGlow
import org.nousresearch.hermes.evolution.ui.theme.EmeraldReady
import org.nousresearch.hermes.evolution.ui.theme.RoseError
import org.nousresearch.hermes.evolution.ui.theme.SurfaceBorder
import org.nousresearch.hermes.evolution.ui.theme.SurfaceContainer
import org.nousresearch.hermes.evolution.ui.theme.SurfaceDark
import org.nousresearch.hermes.evolution.ui.theme.TextPrimary
import org.nousresearch.hermes.evolution.ui.theme.TextSecondary

@Composable
fun ToolCallCard(
    toolName: String,
    arguments: Map<String, Any?>,
    resultOutput: String? = null,
    isError: Boolean = false,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    Surface(
        color = SurfaceDark,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, SurfaceBorder),
        modifier = modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = "Tool Call",
                        tint = CyanGlow
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                    Text(
                        text = toolName,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 14.sp
                    )
                }

                Surface(
                    color = if (isError) RoseError.copy(alpha = 0.2f) else EmeraldReady.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = if (isError) "FAILED" else if (resultOutput != null) "DONE" else "DISPATCHED",
                        color = if (isError) RoseError else EmeraldReady,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = SurfaceContainer,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = "Arguments:",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = arguments.entries.joinToString("\n") { "${it.key}: ${it.value}" },
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        if (resultOutput != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Output:",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = resultOutput,
                                color = if (isError) RoseError else TextPrimary,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}
