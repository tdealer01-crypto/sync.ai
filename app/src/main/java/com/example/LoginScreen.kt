package com.example

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = Login, 1 = Free Trial

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App Logo / Title
        Icon(
            imageVector = Icons.Default.Security,
            contentDescription = "Logo",
            tint = ArchTextMain,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "DSG ONEProofGate",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = ArchTextMain
        )
        Text(
            text = "Control Plane",
            fontSize = 16.sp,
            color = ArchTextSecondary
        )
        
        Spacer(modifier = Modifier.height(32.dp))

        // Tab Row for separation (Suggestion 1)
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = ArchTextMain,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = ArchBlue
                )
            },
            divider = { HorizontalDivider(color = ArchBorder) }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Sign In") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Start Free Trial") }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(ArchSurface)
                .border(1.dp, ArchBorder, RoundedCornerShape(12.dp))
                .padding(24.dp)
        ) {
            Crossfade(targetState = selectedTab, label = "tabs") { tab ->
                when (tab) {
                    0 -> SignInForm(onLoginSuccess)
                    1 -> FreeTrialForm(onLoginSuccess)
                }
            }
        }
    }
}

@Composable
fun SignInForm(onLoginSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showRecovery by remember { mutableStateOf(false) }

    if (showRecovery) {
        RecoveryForm(onBack = { showRecovery = false })
        return
    }

    Column {
        // Error Message with Icon (Suggestion 2)
        AnimatedVisibility(visible = errorMessage != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .background(ArchDanger.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    .border(1.dp, ArchDanger, RoundedCornerShape(8.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.ErrorOutline, contentDescription = "Error", tint = ArchDanger)
                Spacer(modifier = Modifier.width(8.dp))
                Text(errorMessage ?: "", color = ArchTextMain, fontSize = 14.sp)
            }
        }

        OutlinedTextField(
            value = email,
            onValueChange = { email = it; errorMessage = null },
            label = { Text("Email", color = ArchTextSecondary) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ArchBlue,
                unfocusedBorderColor = ArchBorder,
                focusedTextColor = ArchTextMain,
                unfocusedTextColor = ArchTextMain
            ),
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = ArchTextSecondary) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it; errorMessage = null },
            label = { Text("Password", color = ArchTextSecondary) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ArchBlue,
                unfocusedBorderColor = ArchBorder,
                focusedTextColor = ArchTextMain,
                unfocusedTextColor = ArchTextMain
            ),
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = ArchTextSecondary) },
            trailingIcon = {
                val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = "Toggle password visibility", tint = ArchTextSecondary)
                }
            }
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Forgot password?",
            color = ArchBlue,
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.End)
                .clickable { showRecovery = true }
                .padding(4.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (email.isEmpty() || password.isEmpty()) {
                    errorMessage = "Please enter both email and password."
                } else if (!email.contains("@")) {
                    errorMessage = "Invalid email format."
                } else if (password.length < 8) {
                    errorMessage = "Password must be at least 8 characters."
                } else {
                    onLoginSuccess()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ArchBlue),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Continue with password", fontWeight = FontWeight.Bold, color = ArchTextMain)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedButton(
            onClick = { onLoginSuccess() },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = ArchTextMain),
            border = androidx.compose.foundation.BorderStroke(1.dp, ArchBorder),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(Icons.Default.Language, contentDescription = "SSO", tint = ArchTextMain)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Continue with SSO", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun RecoveryForm(onBack: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }
    var isSuccess by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = ArchTextMain)
            }
            Text("Send a recovery link", color = ArchTextMain, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        if (isSuccess) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .background(ArchEmerald.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    .border(1.dp, ArchEmerald, RoundedCornerShape(8.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = "Success", tint = ArchEmerald)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Recovery link sent to your email.", color = ArchTextMain, fontSize = 14.sp)
            }
            
            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ArchBorder)
            ) {
                Text("Back to Sign In", color = ArchTextMain)
            }
        } else {
            AnimatedVisibility(visible = errorMessage != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .background(ArchDanger.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .border(1.dp, ArchDanger, RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.ErrorOutline, contentDescription = "Error", tint = ArchDanger)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(errorMessage ?: "", color = ArchTextMain, fontSize = 14.sp)
                }
            }

            Text("Enter your business email to receive a recovery link.", color = ArchTextSecondary, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it; errorMessage = null },
                label = { Text("Business Email", color = ArchTextSecondary) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ArchBlue,
                    unfocusedBorderColor = ArchBorder,
                    focusedTextColor = ArchTextMain,
                    unfocusedTextColor = ArchTextMain
                ),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Business, contentDescription = null, tint = ArchTextSecondary) }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = {
                    if (!email.contains("@")) {
                        errorMessage = "Please enter a valid email address."
                    } else {
                        isSending = true
                        errorMessage = null
                        scope.launch {
                            delay(2000) // Simulate network request
                            isSending = false
                            isSuccess = true
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ArchBlue),
                shape = RoundedCornerShape(8.dp),
                enabled = !isSending
            ) {
                if (isSending) {
                    // Progress Indicator (Suggestion 3)
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = ArchTextMain,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Sending...", fontWeight = FontWeight.Bold, color = ArchTextMain)
                } else {
                    Text("Send recovery link", fontWeight = FontWeight.Bold, color = ArchTextMain)
                }
            }
        }
    }
}

@Composable
fun FreeTrialForm(onLoginSuccess: () -> Unit) {
    var companyName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column {
        AnimatedVisibility(visible = errorMessage != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .background(ArchDanger.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    .border(1.dp, ArchDanger, RoundedCornerShape(8.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.ErrorOutline, contentDescription = "Error", tint = ArchDanger)
                Spacer(modifier = Modifier.width(8.dp))
                Text(errorMessage ?: "", color = ArchTextMain, fontSize = 14.sp)
            }
        }

        OutlinedTextField(
            value = companyName,
            onValueChange = { companyName = it; errorMessage = null },
            label = { Text("Company Name", color = ArchTextSecondary) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ArchBlue,
                unfocusedBorderColor = ArchBorder,
                focusedTextColor = ArchTextMain,
                unfocusedTextColor = ArchTextMain
            ),
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Business, contentDescription = null, tint = ArchTextSecondary) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it; errorMessage = null },
            label = { Text("Work Email", color = ArchTextSecondary) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ArchBlue,
                unfocusedBorderColor = ArchBorder,
                focusedTextColor = ArchTextMain,
                unfocusedTextColor = ArchTextMain
            ),
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = ArchTextSecondary) }
        )
        
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (companyName.isEmpty() || email.isEmpty()) {
                    errorMessage = "Please fill in all fields."
                } else if (!email.contains("@")) {
                    errorMessage = "Invalid email format."
                } else {
                    onLoginSuccess()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ArchBlue),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Start Workspace Trial", fontWeight = FontWeight.Bold, color = ArchTextMain)
        }
    }
}
