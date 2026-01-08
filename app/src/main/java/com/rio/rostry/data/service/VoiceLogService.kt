package com.rio.rostry.data.service

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Result from voice recognition.
 */
sealed class VoiceResult {
    data class Success(val text: String, val confidence: Float) : VoiceResult()
    data class Error(val message: String, val code: Int) : VoiceResult()
    object Listening : VoiceResult()
    object Processing : VoiceResult()
}

/**
 * Service for voice-to-text recognition using Android SpeechRecognizer.
 * 
 * Usage:
 * ```
 * voiceLogService.startListening().collect { result ->
 *     when (result) {
 *         is VoiceResult.Success -> handleTranscription(result.text)
 *         is VoiceResult.Error -> showError(result.message)
 *         VoiceResult.Listening -> showListeningUI()
 *         VoiceResult.Processing -> showProcessingUI()
 *     }
 * }
 * ```
 */
@Singleton
class VoiceLogService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private var speechRecognizer: SpeechRecognizer? = null
    
    /**
     * Check if speech recognition is available on this device.
     */
    fun isAvailable(): Boolean {
        return SpeechRecognizer.isRecognitionAvailable(context)
    }
    
    /**
     * Start listening for voice input.
     * Returns a Flow that emits VoiceResult states.
     */
    fun startListening(): Flow<VoiceResult> = callbackFlow {
        if (!isAvailable()) {
            trySend(VoiceResult.Error("Speech recognition not available", -1))
            close()
            return@callbackFlow
        }
        
        // Create speech recognizer on main thread
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            // Prompt for the user
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your log entry...")
            // Short silence detection for quick responses
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 1500)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 1000)
        }
        
        val listener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                Timber.d("VoiceLog: Ready for speech")
                trySend(VoiceResult.Listening)
            }
            
            override fun onBeginningOfSpeech() {
                Timber.d("VoiceLog: Speech started")
            }
            
            override fun onRmsChanged(rmsdB: Float) {
                // Audio level changed - could use for visualization
            }
            
            override fun onBufferReceived(buffer: ByteArray?) {
                // Audio buffer received
            }
            
            override fun onEndOfSpeech() {
                Timber.d("VoiceLog: Speech ended")
                trySend(VoiceResult.Processing)
            }
            
            override fun onError(error: Int) {
                val errorMessage = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                    SpeechRecognizer.ERROR_CLIENT -> "Client error"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission required"
                    SpeechRecognizer.ERROR_NETWORK -> "Network error"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                    SpeechRecognizer.ERROR_NO_MATCH -> "No speech detected"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognition service busy"
                    SpeechRecognizer.ERROR_SERVER -> "Server error"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
                    else -> "Unknown error: $error"
                }
                Timber.e("VoiceLog Error: $errorMessage ($error)")
                trySend(VoiceResult.Error(errorMessage, error))
            }
            
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val confidences = results?.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES)
                
                if (!matches.isNullOrEmpty()) {
                    val text = matches[0]
                    val confidence = confidences?.getOrNull(0) ?: 0.5f
                    Timber.d("VoiceLog Result: \"$text\" (confidence: $confidence)")
                    trySend(VoiceResult.Success(text, confidence))
                } else {
                    trySend(VoiceResult.Error("No speech detected", SpeechRecognizer.ERROR_NO_MATCH))
                }
            }
            
            override fun onPartialResults(partialResults: Bundle?) {
                // Partial results during speech - could show live transcription
                val partial = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                partial?.firstOrNull()?.let { text ->
                    Timber.d("VoiceLog Partial: \"$text\"")
                }
            }
            
            override fun onEvent(eventType: Int, params: Bundle?) {
                Timber.d("VoiceLog Event: $eventType")
            }
        }
        
        speechRecognizer?.setRecognitionListener(listener)
        speechRecognizer?.startListening(intent)
        
        awaitClose {
            stopListening()
        }
    }
    
    /**
     * Stop the current listening session.
     */
    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
            speechRecognizer?.cancel()
            speechRecognizer?.destroy()
            speechRecognizer = null
        } catch (e: Exception) {
            Timber.e(e, "Error stopping speech recognizer")
        }
    }
    
    /**
     * Cancel the current recognition.
     */
    fun cancel() {
        speechRecognizer?.cancel()
    }
}
