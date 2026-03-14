package com.rio.rostry.feature.login.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rio.rostry.domain.model.UserType

/**
 * Role-first welcome screen with authentication options.
 * 
 * Features:
 * - Role selection (Farmer, Enthusiast, General)
 * - Google Sign-In
 * - Email/Password Sign-In & Sign-Up
 * - Continue as Guest (preview mode)
 * - Special admin detection for specific emails
 */
@Composable
fun AuthWelcomeScreen(
    onSignInWithGoogle: (UserType) -> Unit,
    onSignInWithEmail: (UserType) -> Unit,
    onSignUpWithEmail: (UserType) -> Unit,
    onContinueAsGuest: (UserType) -> Unit,
    onNavigateToPasswordReset: () -> Unit,
    isLoading: Boolean = false
) {
    var selectedRole by remember { mutableStateOf<UserType?>(null) }
    var showAuthOptions by remember { mutableStateOf(false) }
    var detectedAdminEmail by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
            .windowInsetsPadding(WindowInsets.safeDrawing),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))

        // Animated App Logo
        val logoScale by animateFloatAsState(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            label = "logo_scale"
        )

        Surface(
            modifier = Modifier
                .size(120.dp)
                .scale(logoScale),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            shadowElevation = 8.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "ROSTRY Logo",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Welcome to ROSTRY",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Your Poultry Management Platform",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        // Admin Badge (shown when admin email is detected)
        detectedAdminEmail?.let { email ->
            Spacer(modifier = Modifier.height(8.dp))
            AssistChip(
                onClick = { },
                label = { Text("Admin Access Detected") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Role Selection
        Text(
            text = "Select your role to continue",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Role Selection Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
        ) {
            UserType.entries.filter { it in listOf(UserType.FARMER, UserType.ENTHUSIAST, UserType.GENERAL) }.forEach { role ->
                FilterChip(
                    selected = selectedRole == role,
                    onClick = {
                        selectedRole = role
                        showAuthOptions = true
                    },
                    label = { Text(role.displayName) },
                    leadingIcon = {
                        Icon(
                            imageVector = when (role) {
                                UserType.FARMER -> Icons.Default.Agriculture
                                UserType.ENTHUSIAST -> Icons.Default.Favorite
                                else -> Icons.Default.Person
                            },
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Auth Options (shown after role selection)
        AnimatedVisibility(
            visible = showAuthOptions && selectedRole != null,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            AuthOptionsCard(
                selectedRole = selectedRole!!,
                isLoading = isLoading,
                onGoogleSignIn = { onSignInWithGoogle(selectedRole!!) },
                onEmailSignIn = { onSignInWithEmail(selectedRole!!) },
                onEmailSignUp = { onSignUpWithEmail(selectedRole!!) },
                onContinueAsGuest = { onContinueAsGuest(selectedRole!!) },
                onNavigateToPasswordReset = onNavigateToPasswordReset,
                onAdminEmailDetected = { detectedAdminEmail = it }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Help Text
        Text(
            text = "Why we ask? We customize your experience based on your role to show relevant features and content.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Terms
        Text(
            text = "By continuing, you agree to our Terms of Service\nand Privacy Policy",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }
}

/**
 * Authentication options card with Google, Email, and Guest options
 */
@Composable
private fun AuthOptionsCard(
    selectedRole: UserType,
    isLoading: Boolean,
    onGoogleSignIn: () -> Unit,
    onEmailSignIn: () -> Unit,
    onEmailSignUp: () -> Unit,
    onContinueAsGuest: () -> Unit,
    onNavigateToPasswordReset: () -> Unit
) {
    var showEmailAuthSheet by remember { mutableStateOf(false) }
    var isSignUpMode by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Google Sign-In Button
            Button(
                onClick = onGoogleSignIn,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Login,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Continue with Google")
            }

            // Divider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f))
                Text(
                    text = "or",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                HorizontalDivider(modifier = Modifier.weight(1f))
            }

            // Email Sign-In/Sign-Up Button
            OutlinedButton(
                onClick = {
                    isSignUpMode = false
                    showEmailAuthSheet = true
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sign in with Email")
            }

            // Create Account Button
            OutlinedButton(
                onClick = {
                    isSignUpMode = true
                    showEmailAuthSheet = true
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Create Account")
            }

            // Divider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f))
                Text(
                    text = "or",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                HorizontalDivider(modifier = Modifier.weight(1f))
            }

            // Guest Mode Button
            TextButton(
                onClick = onContinueAsGuest,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Continue as ${selectedRole.displayName} (Preview)")
            }

            Text(
                text = "Preview features without signing in",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    // Email Auth Bottom Sheet
    if (showEmailAuthSheet) {
        EmailAuthBottomSheet(
            isSignUpMode = isSignUpMode,
            onDismiss = { showEmailAuthSheet = false },
            onEmailSignIn = {
                showEmailAuthSheet = false
                onEmailSignIn()
            },
            onEmailSignUp = {
                showEmailAuthSheet = false
                onEmailSignUp()
            },
            onNavigateToPasswordReset = {
                showEmailAuthSheet = false
                onNavigateToPasswordReset()
            }
        )
    }
}

/**
 * Bottom sheet for email/password authentication
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmailAuthBottomSheet(
    isSignUpMode: Boolean,
    onDismiss: () -> Unit,
    onEmailSignIn: () -> Unit,
    onEmailSignUp: () -> Unit,
    onNavigateToPasswordReset: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = if (isSignUpMode) "Create Account" else "Sign In",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            // Display Name (Sign Up only)
            if (isSignUpMode) {
                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    label = { Text("Display Name") },
                    placeholder = { Text("Your name") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it; error = null },
                label = { Text("Email") },
                placeholder = { Text("you@example.com") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                isError = error != null
            )

            // Password
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; error = null },
                label = { Text("Password") },
                placeholder = { Text("Enter password") },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                isError = error != null
            )

            // Error message
            error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Forgot Password (Sign In only)
            if (!isSignUpMode) {
                TextButton(
                    onClick = onNavigateToPasswordReset,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Forgot Password?")
                }
            }

            // Submit Button
            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        error = "Please fill in all fields"
                        return@Button
                    }
                    if (isSignUpMode && displayName.isBlank()) {
                        error = "Please enter your name"
                        return@Button
                    }
                    if (isSignUpMode) {
                        onEmailSignUp()
                    } else {
                        onEmailSignIn()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                Text(if (isSignUpMode) "Create Account" else "Sign In")
            }

            // Switch between Sign In / Sign Up
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isSignUpMode) "Already have an account?" else "Don't have an account?",
                    style = MaterialTheme.typography.bodyMedium
                )
                TextButton(onClick = { isSignUpMode = !isSignUpMode }) {
                    Text(if (isSignUpMode) "Sign In" else "Create Account")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}