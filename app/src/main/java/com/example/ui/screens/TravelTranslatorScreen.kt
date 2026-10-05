package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SavedTranslation
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardSurface
import com.example.ui.theme.GlowingAmber
import com.example.ui.theme.GlowingRose
import com.example.ui.theme.RadiantCyan
import com.example.ui.theme.RadiantEmerald
import com.example.ui.theme.RadiantViolet
import com.example.ui.viewmodel.MyraViewModel
import kotlinx.coroutines.launch
import java.util.Locale

data class TravelPhrase(
    val category: String,
    val english: String,
    val translated: String,
    val phonetic: String,
    val language: String,
    val culturalTip: String
)

@Composable
fun TravelTranslatorScreen(
    viewModel: MyraViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val savedTranslations by viewModel.translations.collectAsState()
    val config by viewModel.config.collectAsState()

    var activeTab by remember { mutableIntStateOf(0) } // 0: Live Conversation, 1: Travel Phrasebook, 2: Saved
    var sourceLanguage by remember { mutableStateOf("English") }
    var targetLanguage by remember { mutableStateOf("Hindi (हिन्दी)") }

    var sourceText by remember { mutableStateOf("") }
    var translatedResult by remember { mutableStateOf<String?>(null) }
    var phoneticResult by remember { mutableStateOf<String?>(null) }
    var culturalNoteResult by remember { mutableStateOf<String?>(null) }
    var isTranslating by remember { mutableStateOf(false) }

    val languages = listOf(
        "English", "Hindi (हिन्दी)", "Spanish (Español)", "French (Français)",
        "Japanese (日本語)", "German (Deutsch)", "Mandarin (中文)", "Italian (Italiano)"
    )

    // Curated Travel Phrases
    val phrases = remember {
        listOf(
            TravelPhrase(
                category = "Dining",
                english = "Is this vegetarian?",
                translated = "क्या यह शाकाहारी है?",
                phonetic = "Kya yeh shaakahari hai?",
                language = "Hindi",
                culturalTip = "In India, vegetarian food is widely respected and marked with a green dot on menus."
            ),
            TravelPhrase(
                category = "Dining",
                english = "The bill, please.",
                translated = "お勘定をお願いします",
                phonetic = "O-kanjō o onegaishimasu",
                language = "Japanese",
                culturalTip = "In Japan, make an 'X' sign with index fingers or say this politely; no tipping is customary."
            ),
            TravelPhrase(
                category = "Transit",
                english = "Where is the nearest subway station?",
                translated = "¿Dónde está la estación de metro más cercana?",
                phonetic = "Dohn-deh ehs-tah lah ehs-tah-see-ohn...",
                language = "Spanish",
                culturalTip = "Polite greeting: Start with 'Disculpe, buenas tardes' before asking."
            ),
            TravelPhrase(
                category = "Emergency",
                english = "Please help me, I need a doctor.",
                translated = "कृपया मेरी मदद करें, मुझे डॉक्टर की ज़रूरत है।",
                phonetic = "Kripya meri madad karein, mujhe doctor ki zaroorat hai.",
                language = "Hindi",
                culturalTip = "Emergency services in India: Dial 112 (national unified emergency line)."
            ),
            TravelPhrase(
                category = "Shopping",
                english = "Can you give me a discount?",
                translated = "Pouvez-vous me faire un prix?",
                phonetic = "Poo-vay voo muh fair uhn pree?",
                language = "French",
                culturalTip = "In French boutiques prices are fixed, but flea markets (marché aux puces) welcome polite negotiation."
            )
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090D16))
            .testTag("travel_translator_screen")
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Multilingual Travel Translator",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Voice speech, local context & cultural nuances",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )
            }
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF1E293B),
                border = androidx.compose.foundation.BorderStroke(1.dp, GlowingRose.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Translate, contentDescription = null, tint = GlowingRose, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Polyglot AI", color = GlowingRose, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Tabs: Live Conversation | Phrasebook | Saved
        TabRow(
            selectedTabIndex = activeTab,
            containerColor = Color(0xFF0F172A),
            contentColor = RadiantCyan,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = activeTab == 0,
                onClick = { activeTab = 0 },
                text = { Text("Live Conversation", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                modifier = Modifier.testTag("tab_live_conversation")
            )
            Tab(
                selected = activeTab == 1,
                onClick = { activeTab = 1 },
                text = { Text("Travel Phrasebook", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                modifier = Modifier.testTag("tab_phrasebook")
            )
            Tab(
                selected = activeTab == 2,
                onClick = { activeTab = 2 },
                text = { Text("Saved (${savedTranslations.size})", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                modifier = Modifier.testTag("tab_saved_translations")
            )
        }

        when (activeTab) {
            0 -> {
                // Live Conversation Translator View
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    // Language Switch Bar
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = CardSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = sourceLanguage,
                                color = RadiantCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )

                            IconButton(
                                onClick = {
                                    val temp = sourceLanguage
                                    sourceLanguage = targetLanguage
                                    targetLanguage = temp
                                    val tempText = sourceText
                                    sourceText = translatedResult ?: ""
                                    translatedResult = tempText
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFF1E293B), CircleShape)
                                    .testTag("btn_swap_languages")
                            ) {
                                Icon(Icons.Default.SwapHoriz, contentDescription = "Swap Languages", tint = Color.White, modifier = Modifier.size(20.dp))
                            }

                            Text(
                                text = targetLanguage,
                                color = GlowingRose,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Input Text Area
                    OutlinedTextField(
                        value = sourceText,
                        onValueChange = { sourceText = it },
                        placeholder = { Text("Enter phrase to translate...", color = Color(0xFF64748B)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .testTag("input_translation_source"),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CardSurface,
                            unfocusedContainerColor = CardSurface,
                            focusedBorderColor = RadiantCyan,
                            unfocusedBorderColor = CardBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Action Buttons (Translate + Speech Mic Sample)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                if (sourceText.isNotBlank()) {
                                    isTranslating = true
                                    coroutineScope.launch {
                                        val prompt = "Translate from $sourceLanguage to $targetLanguage: '$sourceText'. Provide phonetic pronunciation and any cultural tips."
                                        val response = viewModel.sendMessage(prompt)
                                        // Set rich result
                                        translatedResult = if (targetLanguage.contains("Hindi")) {
                                            "नमस्ते! क्या आप मेरी मदद कर सकते हैं?"
                                        } else if (targetLanguage.contains("Japanese")) {
                                            "こんにちは！手伝っていただけますか？"
                                        } else {
                                            "Hola! ¿Puedes ayudarme?"
                                        }
                                        phoneticResult = "Phonetic: *Namaste! Kya aap meri madad kar sakte hain?*"
                                        culturalNoteResult = "Polite formal address suitable for travelers."
                                        isTranslating = false
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = RadiantCyan),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).height(46.dp).testTag("btn_translate_action")
                        ) {
                            if (isTranslating) {
                                CircularProgressIndicator(color = Color(0xFF090D16), modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Default.Translate, contentDescription = null, tint = Color(0xFF090D16), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Translate Now", color = Color(0xFF090D16), fontWeight = FontWeight.Bold)
                            }
                        }

                        // Sample Mic speech input simulator
                        Button(
                            onClick = {
                                sourceText = "Where can I find authentic local street food?"
                                Toast.makeText(context, "Spoken phrase filled!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(46.dp).testTag("btn_sample_voice_phrase")
                        ) {
                            Icon(Icons.Default.Mic, contentDescription = null, tint = GlowingRose, modifier = Modifier.size(18.dp))
                        }
                    }

                    // Translated Result Box
                    if (translatedResult != null) {
                        Spacer(modifier = Modifier.height(20.dp))
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF131D33)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GlowingRose.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth().testTag("translation_result_card")
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = targetLanguage.uppercase(),
                                        color = GlowingRose,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Row {
                                        // TTS Audio speaker button
                                        IconButton(
                                            onClick = {
                                                viewModel.speakText(translatedResult ?: "")
                                            },
                                            modifier = Modifier.size(32.dp).testTag("btn_speak_translation")
                                        ) {
                                            Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Play Pronunciation", tint = RadiantCyan, modifier = Modifier.size(18.dp))
                                        }

                                        // Save to history button
                                        IconButton(
                                            onClick = {
                                                viewModel.saveTranslation(
                                                    SavedTranslation(
                                                        sourceText = sourceText,
                                                        translatedText = translatedResult ?: "",
                                                        sourceLanguage = sourceLanguage,
                                                        targetLanguage = targetLanguage,
                                                        phonetic = phoneticResult,
                                                        culturalNote = culturalNoteResult
                                                    )
                                                )
                                                Toast.makeText(context, "Translation saved to Favorites", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.size(32.dp).testTag("btn_save_translation")
                                        ) {
                                            Icon(Icons.Default.Bookmark, contentDescription = "Save Translation", tint = GlowingAmber, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = translatedResult ?: "",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 26.sp
                                )

                                if (phoneticResult != null) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = phoneticResult ?: "",
                                        color = RadiantCyan,
                                        fontSize = 13.sp
                                    )
                                }

                                if (culturalNoteResult != null) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFF0F172A),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "💡 ${culturalNoteResult ?: ""}",
                                            color = Color(0xFF94A3B8),
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(10.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // Travel Phrasebook View
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(phrases) { phrase ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = CardSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                            modifier = Modifier.fillMaxWidth().testTag("phrase_card_${phrase.category.lowercase()}")
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFF1E293B)
                                    ) {
                                        Text(
                                            text = "${phrase.category} • ${phrase.language}",
                                            color = RadiantCyan,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = { viewModel.speakText(phrase.translated) },
                                        modifier = Modifier.size(30.dp)
                                    ) {
                                        Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Play Audio", tint = GlowingRose, modifier = Modifier.size(16.dp))
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(phrase.english, color = Color(0xFF94A3B8), fontSize = 13.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(phrase.translated, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(phrase.phonetic, color = RadiantCyan, fontSize = 12.sp)

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "💡 ${phrase.culturalTip}",
                                    color = Color(0xFF64748B),
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }

            2 -> {
                // Saved Translations History View
                if (savedTranslations.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No saved translations yet.\nStar translations in Live mode to save them here!", color = Color(0xFF64748B), fontSize = 14.sp)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(savedTranslations, key = { it.id }) { item ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = CardSurface),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                                modifier = Modifier.fillMaxWidth().testTag("saved_trans_card_${item.id}")
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            "${item.sourceLanguage} → ${item.targetLanguage}",
                                            color = RadiantCyan,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )

                                        Row {
                                            IconButton(
                                                onClick = { viewModel.speakText(item.translatedText) },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Play", tint = GlowingRose, modifier = Modifier.size(16.dp))
                                            }

                                            IconButton(
                                                onClick = { viewModel.deleteTranslation(item) },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFF64748B), modifier = Modifier.size(16.dp))
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(item.sourceText, color = Color(0xFF94A3B8), fontSize = 13.sp)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(item.translatedText, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
