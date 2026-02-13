package com.tinygenius.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.tinygenius.ui.theme.BackgroundCard
import com.tinygenius.ui.theme.ButtonPrimary
import com.tinygenius.ui.theme.TextLight
import com.tinygenius.ui.theme.TextPrimary
import com.tinygenius.utils.Constants
import kotlin.random.Random

/**
 * Parent gate dialog to prevent accidental purchases by children
 * Requires solving a simple math problem
 */
@Composable
fun ParentGate(
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    var userAnswer by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    
    // Generate random math problem
    val (num1, num2, correctAnswer) = remember {
        val a = Random.nextInt(Constants.PARENT_GATE_MIN, Constants.PARENT_GATE_MAX)
        val b = Random.nextInt(Constants.PARENT_GATE_MIN, Constants.PARENT_GATE_MAX)
        Triple(a, b, a + b)
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = BackgroundCard
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Title
                Text(
                    text = "Parent Verification",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
                
                // Instruction
                Text(
                    text = "Please solve this simple math problem to continue:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
                
                // Math problem
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = ButtonPrimary.copy(alpha = 0.1f)
                    )
                ) {
                    Text(
                        text = "$num1 + $num2 = ?",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    )
                }
                
                // Answer input
                OutlinedTextField(
                    value = userAnswer,
                    onValueChange = { 
                        userAnswer = it.filter { char -> char.isDigit() }
                        showError = false
                    },
                    label = { Text("Your Answer") },
                    singleLine = true,
                    isError = showError,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ButtonPrimary,
                        focusedLabelColor = ButtonPrimary
                    )
                )
                
                if (showError) {
                    Text(
                        text = "Incorrect answer. Please try again.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Cancel button
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "Cancel",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                    
                    // Submit button
                    Button(
                        onClick = {
                            val answer = userAnswer.toIntOrNull()
                            if (answer == correctAnswer) {
                                onSuccess()
                            } else {
                                showError = true
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ButtonPrimary,
                            contentColor = TextLight
                        ),
                        enabled = userAnswer.isNotEmpty()
                    ) {
                        Text(
                            text = "Submit",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
