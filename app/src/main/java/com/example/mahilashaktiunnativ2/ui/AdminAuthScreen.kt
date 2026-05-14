package com.example.mahilashaktiunnativ2.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.mahilashaktiunnativ2.ui.database.AdminEntity
import com.example.mahilashaktiunnativ2.ui.theme.AppBackground
import com.example.mahilashaktiunnativ2.ui.theme.CardBackground
import com.example.mahilashaktiunnativ2.ui.theme.LightGreen
import com.example.mahilashaktiunnativ2.ui.theme.PrimaryGreen
import com.example.mahilashaktiunnativ2.ui.theme.SecondaryText
import com.yalantis.ucrop.UCrop

@Composable
fun AdminSetupScreen(
    onCreateAdmin: (AdminEntity) -> Unit
) {
    val context =
        LocalContext.current

    var name by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var village by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var occupation by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var errorMessage by remember { mutableStateOf("") }

    val cropLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                photoUri = result.data?.let { UCrop.getOutput(it) }
            }
        }

    val galleryLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri ->
            uri?.let {
                val destinationUri =
                    Uri.fromFile(
                        java.io.File.createTempFile(
                            "admin_profile",
                            ".jpg",
                            context.cacheDir
                        )
                    )

                val cropIntent =
                    UCrop.of(it, destinationUri)
                        .withAspectRatio(1f, 1f)
                        .withMaxResultSize(800, 800)
                        .withOptions(safeCropOptions(context))
                        .getIntent(context)

                cropLauncher.launch(cropIntent)
            }
        }

    AdminAuthScaffold {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 620.dp)
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Create Admin Profile",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "This setup is required only once on this device.",
                    color = SecondaryText
                )

                Spacer(modifier = Modifier.height(20.dp))

                AdminPhotoPicker(
                    photoUri = photoUri?.toString(),
                    onPickPhoto = {
                        galleryLauncher.launch("image/*")
                    }
                )

                AdminInputField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Admin Name"
                )
                AdminInputField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = "Phone Number",
                    keyboardType = KeyboardType.Phone
                )
                AdminInputField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    keyboardType = KeyboardType.Password,
                    isPassword = true
                )
                MemberDateInputField(
                    value = dateOfBirth,
                    onDateSelected = { dateOfBirth = it },
                    label = "Date of Birth"
                )
                AdminInputField(
                    value = village,
                    onValueChange = { village = it },
                    label = "Village / City"
                )
                AdminInputField(
                    value = address,
                    onValueChange = { address = it },
                    label = "Address"
                )
                AdminInputField(
                    value = occupation,
                    onValueChange = { occupation = it },
                    label = "SHG Role / Occupation"
                )

                AnimatedVisibility(errorMessage.isNotBlank()) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                SmoothAuthButton(
                    text = "Create Admin Account",
                    onClick = {
                        val age =
                            calculateAgeFromDate(dateOfBirth)

                        errorMessage =
                            adminValidationMessage(
                                name = name,
                                phoneNumber = phoneNumber,
                                password = password,
                                dateOfBirth = dateOfBirth,
                                age = age,
                                village = village,
                                occupation = occupation
                            )

                        if (errorMessage.isBlank()) {
                            onCreateAdmin(
                                AdminEntity(
                                    name = name.trim(),
                                    phoneNumber = phoneNumber.trim(),
                                    password = password,
                                    dateOfBirth = dateOfBirth,
                                    age = age,
                                    village = village.trim(),
                                    address = address.trim(),
                                    occupation = occupation.trim(),
                                    photoUri = photoUri?.toString()
                                )
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun AdminLoginScreen(
    onLogin: (String, String, (String?) -> Unit) -> Unit
) {
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    AdminAuthScaffold {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(LightGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = PrimaryGreen,
                        modifier = Modifier.size(34.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Admin Login",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Use your registered phone number and password.",
                    color = SecondaryText
                )

                Spacer(modifier = Modifier.height(20.dp))

                AdminInputField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = "Phone Number",
                    keyboardType = KeyboardType.Phone
                )
                AdminInputField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    keyboardType = KeyboardType.Password,
                    isPassword = true
                )

                AnimatedVisibility(errorMessage.isNotBlank()) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                SmoothAuthButton(
                    text = "Login",
                    onClick = {
                        onLogin(phoneNumber.trim(), password) { message ->
                            errorMessage = message.orEmpty()
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun AdminAuthScaffold(
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF19A66A),
                            Color(0xFF0F7A4D),
                            Color(0xFF064E3B)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "MahilaShaktiUnnati",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Digital Accountant for SHGs",
                color = Color.White.copy(alpha = 0.86f),
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            content()
        }
    }
}

@Composable
fun AdminPhotoPicker(
    photoUri: String?,
    onPickPhoto: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (photoUri != null) {
            AsyncImage(
                model = photoUri,
                contentDescription = null,
                modifier = Modifier
                    .size(106.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(106.dp)
                    .clip(CircleShape)
                    .background(LightGreen),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = PrimaryGreen,
                    modifier = Modifier.size(46.dp)
                )
            }
        }

        TextButton(onClick = onPickPhoto) {
            Text("Upload Profile Photo")
        }
    }
}

@Composable
fun AdminInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false
) {
    Spacer(modifier = Modifier.height(10.dp))

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation =
            if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun SmoothAuthButton(
    text: String,
    onClick: () -> Unit
) {
    val interactionSource =
        remember { MutableInteractionSource() }
    val isPressed by
    interactionSource.collectIsPressedAsState()
    val scale by
    animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = tween(120),
        label = "admin_auth_button"
    )

    Button(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(text)
    }
}

fun adminValidationMessage(
    name: String,
    phoneNumber: String,
    password: String,
    dateOfBirth: String,
    age: Int,
    village: String,
    occupation: String
): String {
    val validPhone =
        phoneNumber.length == 10 &&
                phoneNumber.all { it.isDigit() }

    return when {
        name.isBlank() -> "Enter the admin name."
        !validPhone -> "Enter a valid 10 digit phone number."
        password.length < 4 -> "Password must be at least 4 characters."
        dateOfBirth.isBlank() -> "Select date of birth."
        age !in 18..80 -> "Admin age must be between 18 and 80."
        village.isBlank() -> "Enter village or city."
        occupation.isBlank() -> "Enter the admin SHG role or occupation."
        else -> ""
    }
}
