package com.rio.rostry.ui.social.stories

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.rio.rostry.data.repository.social.SocialRepository
import com.rio.rostry.session.CurrentUserProvider
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class StoryCreatorViewModel @Inject constructor(
    private val socialRepository: SocialRepository,
    private val currentUserProvider: CurrentUserProvider
) : ViewModel() {
    fun createStory(uri: Uri, onComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                val userId = currentUserProvider.userIdOrNull()
                if (userId == null) {
                    Timber.e("StoryCreator: Cannot create story, user not logged in")
                    return@launch
                }
                socialRepository.createStory(userId, uri, false)
                onComplete()
            } catch (e: Exception) {
                Timber.e(e, "StoryCreator: Failed to create story")
            }
        }
    }
}

@Composable
fun StoryCreatorScreen(
    onBack: () -> Unit,
    vm: StoryCreatorViewModel = hiltViewModel()
) {
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (e: Exception) { }
            selectedUri = uri
        }
    }

    Scaffold(
        bottomBar = {
            if (selectedUri != null) {
                Button(
                    onClick = { vm.createStory(selectedUri!!) { onBack() } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Share to Story")
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            if (selectedUri != null) {
                AsyncImage(
                    model = selectedUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Button(onClick = { launcher.launch(arrayOf("image/*")) }) {
                    Text("Select Image")
                }
            }
        }
    }
}
