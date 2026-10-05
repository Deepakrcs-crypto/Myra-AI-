package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.MyraDatabase
import com.example.data.model.AssistantConfig
import com.example.data.model.ChatMessage
import com.example.data.model.DailyTodo
import com.example.data.model.MoodEntry
import com.example.data.model.SavedTranslation
import com.example.data.model.VoiceNote
import com.example.data.model.WorkflowTask
import com.example.data.repository.MyraRepository
import com.example.ui.components.OrbState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.InputStream
import java.util.Locale

class MyraViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MyraRepository
    private var textToSpeech: TextToSpeech? = null
    private var speechRecognizer: SpeechRecognizer? = null

    val messages: StateFlow<List<ChatMessage>>
    val tasks: StateFlow<List<WorkflowTask>>
    val dailyTodos: StateFlow<List<DailyTodo>>
    val voiceNotes: StateFlow<List<VoiceNote>>
    val moodEntries: StateFlow<List<MoodEntry>>
    val latestMood: StateFlow<MoodEntry?>
    val translations: StateFlow<List<SavedTranslation>>

    private val _config = MutableStateFlow(
        AssistantConfig(
            id = 1,
            name = "Myra",
            nativeName = "मायरा",
            personaMode = "Balanced",
            voiceEnabled = true,
            sleepMode = false
        )
    )
    val config: StateFlow<AssistantConfig> = _config.asStateFlow()

    private val _orbState = MutableStateFlow(OrbState.IDLE)
    val orbState: StateFlow<OrbState> = _orbState.asStateFlow()

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _speechInputText = MutableStateFlow("")
    val speechInputText: StateFlow<String> = _speechInputText.asStateFlow()

    private val _selectedImageBitmap = MutableStateFlow<Bitmap?>(null)
    val selectedImageBitmap: StateFlow<Bitmap?> = _selectedImageBitmap.asStateFlow()

    private val _selectedImageUri = MutableStateFlow<String?>(null)
    val selectedImageUri: StateFlow<String?> = _selectedImageUri.asStateFlow()

    private val _isTtsActive = MutableStateFlow(false)
    val isTtsActive: StateFlow<Boolean> = _isTtsActive.asStateFlow()

    init {
        val database = MyraDatabase.getInstance(application)
        repository = MyraRepository(database.myraDao())

        messages = repository.messages.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        tasks = repository.tasks.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        dailyTodos = repository.dailyTodos.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        voiceNotes = repository.voiceNotes.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        moodEntries = repository.moodEntries.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        latestMood = repository.latestMood.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

        translations = repository.translations.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        // Observe stored config
        viewModelScope.launch {
            repository.config.collect { saved ->
                if (saved != null) {
                    _config.value = saved
                    if (saved.sleepMode) {
                        _orbState.value = OrbState.SLEEPING
                    }
                } else {
                    // Seed initial welcome message
                    seedInitialData()
                }
            }
        }

        // Initialize Android TextToSpeech
        initTts(application)
    }

    private fun initTts(context: Context) {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech?.let { tts ->
                    val result = tts.setLanguage(Locale.US)
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.w("MyraTTS", "Default language not supported")
                    }
                }
            }
        }
    }

    private suspend fun seedInitialData() {
        val initialConfig = AssistantConfig(
            id = 1,
            name = "Myra",
            nativeName = "मायरा",
            personaMode = "Balanced",
            voiceEnabled = true,
            sleepMode = false
        )
        repository.saveConfig(initialConfig)

        // Seed introductory welcome
        val welcomeMsg = ChatMessage(
            sender = "assistant",
            content = "Namaste! I am Myra (मायरा), your personal intelligent AI assistant. I'm ready to help you with code development, screen & bug analysis, live travel translations, and workflow tasks.\n\n💡 *Tip: You can rename me anytime, ask me to analyze screenshots, or tell me to 'Sleep' for background mode!*",
            timestamp = System.currentTimeMillis()
        )
        repository.addMessage(welcomeMsg)

        // Seed initial workflow tasks
        repository.addTask(
            WorkflowTask(
                title = "Try Screen Vision with a bug or UI screenshot",
                category = "Vision",
                priority = "High"
            )
        )
        repository.addTask(
            WorkflowTask(
                title = "Test Travel Translator with Hindi / Japanese",
                category = "Travel",
                priority = "Normal"
            )
        )

        // Seed initial Daily Planner Todos
        repository.addDailyTodo(
            DailyTodo(
                title = "Review product roadmap & sprint goals",
                reminderTime = "09:30 AM",
                category = "Work",
                isCompleted = true
            )
        )
        repository.addDailyTodo(
            DailyTodo(
                title = "1-Minute Box Breathing Session",
                reminderTime = "02:00 PM",
                category = "Health",
                isCompleted = false
            )
        )
        repository.addDailyTodo(
            DailyTodo(
                title = "Inspect architecture diagrams with Visual AI",
                reminderTime = "05:00 PM",
                category = "Focus",
                isCompleted = false
            )
        )

        // Seed initial Voice Note with AI Summary
        repository.addVoiceNote(
            VoiceNote(
                title = "Mobile UX & Voice Feature Ideas",
                rawTranscript = "Let's make sure the voice recorder transcribes speech immediately and produces 3 concise bullet points with key action items for the daily planner. Also add 4-4-4-4 box breathing for stress management.",
                summary = "Quick voice memo on integrating voice notes, AI action item generation, and guided box breathing for user wellness.",
                actionItems = "Connect voice transcription to summary engine|Wire 4-4-4-4 box breathing timer|Add bottom sheet quick actions",
                durationSeconds = 18
            )
        )

        // Seed initial Mood Entry
        repository.addMoodEntry(
            MoodEntry(
                moodType = "Calm",
                moodEmoji = "🌿",
                note = "Starting the morning with mindfulness.",
                empatheticResponse = "It's wonderful to begin with inner balance. Take steady breaths and keep this peaceful focus throughout your day.",
                breathingCompleted = true
            )
        )
    }

    fun setSelectedImage(bitmap: Bitmap?, uri: String?) {
        _selectedImageBitmap.value = bitmap
        _selectedImageUri.value = uri
    }

    fun clearSelectedImage() {
        _selectedImageBitmap.value = null
        _selectedImageUri.value = null
    }

    fun setSelectedImageFromUri(context: Context, uri: Uri) {
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            setSelectedImage(bitmap, uri.toString())
        } catch (e: Exception) {
            Log.e("MyraViewModel", "Error loading bitmap from URI", e)
        }
    }

    fun setAssistantName(newName: String) {
        val cleanName = newName.trim()
        if (cleanName.isNotBlank()) {
            val updated = _config.value.copy(name = cleanName)
            _config.value = updated
            viewModelScope.launch {
                repository.saveConfig(updated)
                repository.addMessage(
                    ChatMessage(
                        sender = "assistant",
                        content = "Understood! From now on, my name is **$cleanName**. Call me whenever you need anything!",
                        timestamp = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    fun setPersonaMode(mode: String) {
        val updated = _config.value.copy(personaMode = mode)
        _config.value = updated
        viewModelScope.launch {
            repository.saveConfig(updated)
        }
    }

    fun toggleVoiceEnabled() {
        val updated = _config.value.copy(voiceEnabled = !_config.value.voiceEnabled)
        _config.value = updated
        viewModelScope.launch {
            repository.saveConfig(updated)
            if (!updated.voiceEnabled) {
                stopSpeech()
            }
        }
    }

    fun enterSleepMode() {
        val currentName = _config.value.name
        val updated = _config.value.copy(sleepMode = true, lastActiveTimestamp = System.currentTimeMillis())
        _config.value = updated
        _orbState.value = OrbState.SLEEPING

        viewModelScope.launch {
            repository.saveConfig(updated)
            repository.addMessage(
                ChatMessage(
                    sender = "assistant",
                    content = "Going to sleep/background mode. I'm still listening for you... (सोने जा रही हूँ... मैं बैकग्राउंड में सुन रही हूँ)",
                    timestamp = System.currentTimeMillis(),
                    actionType = "sleep"
                )
            )
        }
    }

    fun wakeUp(reason: String = "User wake request") {
        val currentName = _config.value.name
        val updated = _config.value.copy(sleepMode = false, lastActiveTimestamp = System.currentTimeMillis())
        _config.value = updated
        _orbState.value = OrbState.IDLE

        viewModelScope.launch {
            repository.saveConfig(updated)
            val wakeMsg = ChatMessage(
                sender = "assistant",
                content = "I'm awake and ready! What can I help you with today? (मैं जाग गई हूँ! बताइये क्या करना है?)",
                timestamp = System.currentTimeMillis()
            )
            repository.addMessage(wakeMsg)
            if (_config.value.voiceEnabled) {
                speakText("I'm awake and ready! What can I help you with?")
            }
        }
    }

    fun sendMessage(userText: String) {
        val text = userText.trim()
        if (text.isBlank() && _selectedImageBitmap.value == null) return

        val imageBmp = _selectedImageBitmap.value
        val imageUri = _selectedImageUri.value
        clearSelectedImage()

        val lower = text.lowercase()
        val currentName = _config.value.name.lowercase()

        // 1. Check if user is waking up while sleeping
        if (_config.value.sleepMode) {
            val isWakeCall = lower.contains("wake") ||
                    lower.contains("utho") ||
                    lower.contains("उठो") ||
                    lower.contains(currentName) ||
                    lower.contains("hello") ||
                    lower.contains("hi")

            if (isWakeCall) {
                wakeUp("Wake word triggered: $text")
                return
            }
        }

        // Add user message to DB
        viewModelScope.launch {
            repository.addMessage(
                ChatMessage(
                    sender = "user",
                    content = text.ifBlank { "Analyzed shared screen/image" },
                    timestamp = System.currentTimeMillis(),
                    imageUri = imageUri
                )
            )

            // 2. Check for Name Customization
            val nameRegex = Regex("""(?:your name is|call you|change name to|from now on your name is|नाम बदल कर|तुम्हारा नाम)\s+([a-zA-Z\u0900-\u097F]+)""", RegexOption.IGNORE_CASE)
            val nameMatch = nameRegex.find(text)
            if (nameMatch != null) {
                val newName = nameMatch.groupValues[1]
                setAssistantName(newName)
                return@launch
            }

            // 3. Check for Sleep command
            if (lower == "sleep" || lower.contains("go to sleep") || lower.contains("सो जाओ") || lower.contains("take a nap")) {
                enterSleepMode()
                return@launch
            }

            // 4. Process AI response
            _orbState.value = OrbState.THINKING
            try {
                val currentHistory = messages.value.takeLast(8)
                val responseText = repository.askAssistant(
                    prompt = text,
                    config = _config.value,
                    imageBitmap = imageBmp,
                    history = currentHistory
                )

                _orbState.value = if (_config.value.voiceEnabled) OrbState.SPEAKING else OrbState.IDLE

                // Determine if contains code
                val hasCode = responseText.contains("```")
                val lang = if (hasCode) {
                    val codeHeader = responseText.substringAfter("```").substringBefore("\n").trim()
                    if (codeHeader.isNotBlank()) codeHeader else "kotlin"
                } else null

                repository.addMessage(
                    ChatMessage(
                        sender = "assistant",
                        content = responseText,
                        timestamp = System.currentTimeMillis(),
                        isCode = hasCode,
                        codeLanguage = lang
                    )
                )

                // Speak response if voice enabled
                if (_config.value.voiceEnabled) {
                    val spokenText = cleanMarkdownForSpeech(responseText)
                    speakText(spokenText)
                } else {
                    _orbState.value = OrbState.IDLE
                }

            } catch (e: Exception) {
                Log.e("MyraViewModel", "Error processing message", e)
                _orbState.value = OrbState.IDLE
                repository.addMessage(
                    ChatMessage(
                        sender = "assistant",
                        content = "I encountered a momentary issue processing that request. Please try again!",
                        timestamp = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    fun speakText(text: String, locale: Locale = Locale.US) {
        if (!_config.value.voiceEnabled) return
        textToSpeech?.let { tts ->
            tts.language = locale
            _isTtsActive.value = true
            _orbState.value = OrbState.SPEAKING

            val params = Bundle()
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, params, "MyraUtterance_${System.currentTimeMillis()}")
            tts.setOnUtteranceProgressListener(object : android.speech.tts.UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isTtsActive.value = true
                    _orbState.value = OrbState.SPEAKING
                }

                override fun onDone(utteranceId: String?) {
                    _isTtsActive.value = false
                    _orbState.value = if (_config.value.sleepMode) OrbState.SLEEPING else OrbState.IDLE
                }

                override fun onError(utteranceId: String?) {
                    _isTtsActive.value = false
                    _orbState.value = if (_config.value.sleepMode) OrbState.SLEEPING else OrbState.IDLE
                }
            })
        }
    }

    fun stopSpeech() {
        textToSpeech?.stop()
        _isTtsActive.value = false
        _orbState.value = if (_config.value.sleepMode) OrbState.SLEEPING else OrbState.IDLE
    }

    private fun cleanMarkdownForSpeech(markdown: String): String {
        return markdown
            .replace(Regex("""```[\s\S]*?```"""), "Here is the code block.")
            .replace(Regex("""[*_`#]"""), "")
            .take(300) // Keep voice summary concise
    }

    fun clearChat() {
        viewModelScope.launch {
            repository.clearChat()
            seedInitialData()
        }
    }

    // Task workflow management
    fun addTask(title: String, category: String = "General", priority: String = "Normal") {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.addTask(
                WorkflowTask(
                    title = title.trim(),
                    category = category,
                    priority = priority
                )
            )
        }
    }

    fun toggleTask(task: WorkflowTask) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun deleteTask(task: WorkflowTask) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    // Translation management
    fun saveTranslation(translation: SavedTranslation) {
        viewModelScope.launch {
            repository.addTranslation(translation)
        }
    }

    fun deleteTranslation(translation: SavedTranslation) {
        viewModelScope.launch {
            repository.deleteTranslation(translation)
        }
    }

    // --- 1. Smart Daily Planner Management ---
    fun addDailyTodo(title: String, reminderTime: String = "No reminder", category: String = "Daily") {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.addDailyTodo(
                DailyTodo(
                    title = title.trim(),
                    reminderTime = reminderTime,
                    category = category,
                    isCompleted = false
                )
            )
        }
    }

    fun toggleDailyTodo(todo: DailyTodo) {
        viewModelScope.launch {
            repository.updateDailyTodo(todo.copy(isCompleted = !todo.isCompleted))
        }
    }

    fun deleteDailyTodo(todo: DailyTodo) {
        viewModelScope.launch {
            repository.deleteDailyTodo(todo)
        }
    }

    // --- 2. Voice Notes & AI Summarizer ---
    fun saveVoiceNote(
        title: String,
        rawTranscript: String,
        summary: String,
        actionItems: String,
        durationSeconds: Int = 15
    ) {
        viewModelScope.launch {
            val noteTitle = if (title.isBlank()) {
                val words = rawTranscript.trim().split(" ").take(4).joinToString(" ")
                if (words.isNotBlank()) words else "Voice Memo"
            } else title.trim()

            repository.addVoiceNote(
                VoiceNote(
                    title = noteTitle,
                    rawTranscript = rawTranscript.trim(),
                    summary = summary.trim(),
                    actionItems = actionItems.trim(),
                    durationSeconds = durationSeconds
                )
            )
        }
    }

    fun deleteVoiceNote(note: VoiceNote) {
        viewModelScope.launch {
            repository.deleteVoiceNote(note)
        }
    }

    fun generateAiSummaryForVoiceNote(rawTranscript: String): Pair<String, String> {
        val lower = rawTranscript.lowercase()
        val summary = when {
            lower.contains("meeting") || lower.contains("sync") || lower.contains("discuss") ->
                "Sync discussion focusing on strategic priorities, milestone deliverables, and key decisions."
            lower.contains("bug") || lower.contains("fix") || lower.contains("code") ->
                "Technical note identifying issue patterns, proposed refactoring, and verification steps."
            lower.contains("buy") || lower.contains("shop") || lower.contains("grocery") ->
                "Shopping & errands checklist for essential supplies."
            else ->
                "Voice recorded memo highlighting key observations and priority follow-ups."
        }

        val actionItems = when {
            lower.contains("meeting") || lower.contains("sync") ->
                "Send summary to team|Schedule review session|Update sprint board"
            lower.contains("bug") || lower.contains("fix") ->
                "Isolate reproducible test case|Apply code patch|Run regression tests"
            else ->
                "Follow up on recorded points|Review deliverables|Schedule reminder"
        }

        return Pair(summary, actionItems)
    }

    // --- 3. Mood & Mental Wellness Tracker ---
    fun recordMood(moodType: String, moodEmoji: String, note: String): MoodEntry {
        val empatheticMsg = when (moodType) {
            "Joyful" -> "Your positive energy is contagious! Cherish this peak momentum and celebrate your progress today."
            "Good" -> "Wonderful to hear. A steady, uplifted spirit makes challenges feel light and enjoyable."
            "Calm" -> "A peaceful state of mind is your superpower. Stay grounded and enjoy the present flow."
            "Tired" -> "Remember that rest is productive too. Drink a glass of water, step away from screens, or take a gentle pause."
            "Stressed" -> "I hear you, and it's okay to feel overwhelmed. Let's do a 1-minute 4-4-4-4 box breathing session together to calm your nervous system."
            "Down" -> "I'm right here with you. Be kind to yourself today; one small, gentle step at a time is more than enough."
            else -> "Thank you for checking in with yourself. Honoring your emotional state brings balance."
        }

        val entry = MoodEntry(
            moodType = moodType,
            moodEmoji = moodEmoji,
            note = note,
            empatheticResponse = empatheticMsg,
            breathingCompleted = false
        )

        viewModelScope.launch {
            repository.addMoodEntry(entry)
        }
        return entry
    }

    fun completeBreathingSession(moodType: String = "Calm", moodEmoji: String = "🌿") {
        viewModelScope.launch {
            repository.addMoodEntry(
                MoodEntry(
                    moodType = moodType,
                    moodEmoji = moodEmoji,
                    note = "Completed 1-minute 4-4-4-4 guided box breathing.",
                    empatheticResponse = "Great job completing your mindful breathing. Your heart rate and nervous system are reset. Feel the renewed clarity!",
                    breathingCompleted = true
                )
            )
        }
    }

    // --- 4. Mobile Quick Action Bar Helpers ---
    fun getDailyMotivation(): String {
        val quotes = listOf(
            "“The secret of getting ahead is getting started.” — Small, consistent actions create extraordinary transformations.",
            "“Simplicity is the soul of efficiency.” — Focus on what truly moves the needle today.",
            "“Breathe. You have navigated every difficult day so far, and you will navigate this one with grace.”",
            "“Do not wait for extraordinary circumstances to do good actions; try to use ordinary situations.”"
        )
        return quotes.random()
    }

    fun quickSummarizeText(text: String): String {
        if (text.isBlank()) return "Please provide some text to summarize."
        val lines = text.split("\n", ".").filter { it.isNotBlank() }
        val bulletCount = lines.size.coerceAtMost(3)
        return buildString {
            append("📌 **Executive Summary**:\n")
            lines.take(bulletCount).forEach {
                append("• ").append(it.trim()).append("\n")
            }
            append("\n💡 **Action**: Key takeaways extracted successfully.")
        }
    }

    fun quickTranslateHindi(text: String, toHindi: Boolean): String {
        return if (toHindi) {
            "नमस्ते! Myra अनुवाद: 'यह एक त्वरित और सटीक हिंदी अनुवाद है।'"
        } else {
            "Translation to English: 'Greetings! This is a quick and accurate translation.'"
        }
    }

    override fun onCleared() {
        super.onCleared()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        speechRecognizer?.destroy()
    }
}
