package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.MedicalDisclaimerCard
import com.example.ui.theme.ClinicalBackground
import com.example.ui.theme.ClinicalBorder
import com.example.ui.theme.ClinicalSurface
import com.example.ui.theme.ClinicalTealContainer
import com.example.ui.theme.ClinicalTealPrimary
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryText
import com.example.ui.viewmodel.ChatMessage
import com.example.ui.viewmodel.NutritionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiAssistantScreen(
    viewModel: NutritionViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    val chatMessages by viewModel.chatMessages.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()
    val mealResult by viewModel.aiMealRecommendationResult.collectAsState()
    val isMealLoading by viewModel.isMealAiLoading.collectAsState()

    val dailySummary by viewModel.dailySummary.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    // Meal Recommender form states
    var targetCalStr by remember { mutableStateOf("600") }
    var targetPStr by remember { mutableStateOf("40") }
    var targetCStr by remember { mutableStateOf("70") }
    var targetFStr by remember { mutableStateOf("15") }
    var mealPreference by remember { mutableStateOf("High Protein Balanced") }

    // Conversational input state
    var userPromptText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ClinicalBackground)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Screen Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Clinical AI Intelligence",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryText
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = ClinicalTealContainer,
                        shape = RoundedCornerShape(4.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ClinicalBorder)
                    ) {
                        Text(
                            text = "GEMINI-BACKED",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.5.sp,
                            color = ClinicalTealPrimary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = "Evidence-grounded meal synthesis & nutritional consultation",
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryText
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Segmented Tabs
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = ClinicalSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, ClinicalBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                Surface(
                    onClick = { selectedTab = 0 },
                    color = if (selectedTab == 0) ClinicalTealPrimary else ClinicalSurface,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("tab_what_to_eat")
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestaurantMenu,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = if (selectedTab == 0) Color.White else SecondaryText
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Dietary Formulation",
                            fontSize = 13.sp,
                            fontWeight = if (selectedTab == 0) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (selectedTab == 0) Color.White else SecondaryText
                        )
                    }
                }

                Surface(
                    onClick = { selectedTab = 1 },
                    color = if (selectedTab == 1) ClinicalTealPrimary else ClinicalSurface,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("tab_ai_chat")
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = if (selectedTab == 1) Color.White else SecondaryText
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Clinical Consultation",
                            fontSize = 13.sp,
                            fontWeight = if (selectedTab == 1) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (selectedTab == 1) Color.White else SecondaryText
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (selectedTab == 0) {
            // TAB 1: DIETARY FORMULATION (Meal Combination Engine)
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = ClinicalSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ClinicalBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "TARGET NUTRITIONAL PRESCRIPTION",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 0.8.sp,
                                    color = SecondaryText
                                )

                                Surface(
                                    onClick = {
                                        val remCal = (userProfile.targetCalories - dailySummary.totalCalories).coerceAtLeast(300.0)
                                        val remP = (userProfile.targetProtein - dailySummary.totalProtein).coerceAtLeast(20.0)
                                        val remC = (userProfile.targetCarbs - dailySummary.totalCarbs).coerceAtLeast(30.0)
                                        val remF = (userProfile.targetFat - dailySummary.totalFat).coerceAtLeast(10.0)
                                        targetCalStr = remCal.toInt().toString()
                                        targetPStr = remP.toInt().toString()
                                        targetCStr = remC.toInt().toString()
                                        targetFStr = remF.toInt().toString()
                                    },
                                    shape = RoundedCornerShape(6.dp),
                                    color = ClinicalTealContainer,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, ClinicalBorder),
                                    modifier = Modifier.testTag("fill_remaining_macros_button")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ElectricBolt,
                                            contentDescription = null,
                                            modifier = Modifier.size(12.dp),
                                            tint = ClinicalTealPrimary
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Fill Remaining Balance",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = ClinicalTealPrimary
                                        )
                                    }
                                }
                            }

                            // Inputs Row: Energy & Protein
                            Row(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = targetCalStr,
                                    onValueChange = { targetCalStr = it },
                                    label = { Text("Energy (kcal)", color = SecondaryText) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("meal_target_calories"),
                                    singleLine = true
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                OutlinedTextField(
                                    value = targetPStr,
                                    onValueChange = { targetPStr = it },
                                    label = { Text("Protein (g)", color = SecondaryText) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("meal_target_protein"),
                                    singleLine = true
                                )
                            }

                            // Inputs Row: Carbs & Fat
                            Row(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = targetCStr,
                                    onValueChange = { targetCStr = it },
                                    label = { Text("Carbohydrates (g)", color = SecondaryText) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("meal_target_carbs"),
                                    singleLine = true
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                OutlinedTextField(
                                    value = targetFStr,
                                    onValueChange = { targetFStr = it },
                                    label = { Text("Total Fat (g)", color = SecondaryText) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("meal_target_fat"),
                                    singleLine = true
                                )
                            }

                            // Meal Type / Preference Presets
                            Text(
                                text = "DIETARY PROFILE / PROTOCOL",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.8.sp,
                                color = SecondaryText
                            )

                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(listOf("High Protein Balanced", "Vegetarian / Plant-Dense", "Low Glycemic Index", "Post-Training Recovery", "Portion-Controlled Snack")) { pref ->
                                    val isSelected = mealPreference == pref
                                    Surface(
                                        onClick = { mealPreference = pref },
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isSelected) ClinicalTealPrimary else ClinicalBackground,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) ClinicalTealPrimary else ClinicalBorder)
                                    ) {
                                        Text(
                                            text = pref,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                            color = if (isSelected) Color.White else PrimaryText,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Submit Button
                            Button(
                                onClick = {
                                    val cal = targetCalStr.toDoubleOrNull() ?: 600.0
                                    val p = targetPStr.toDoubleOrNull() ?: 40.0
                                    val c = targetCStr.toDoubleOrNull() ?: 70.0
                                    val f = targetFStr.toDoubleOrNull() ?: 15.0
                                    viewModel.requestAiMealPlan(cal, p, c, f, mealPreference)
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ClinicalTealPrimary,
                                    contentColor = Color.White
                                ),
                                enabled = !isMealLoading,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("synthesize_meals_button")
                            ) {
                                if (isMealLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Synthesizing Food Combination...", fontSize = 13.sp)
                                } else {
                                    Text(
                                        text = "Generate Evidence-Based Meal Combination",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }

                // AI Meal Recommendation Result Output
                mealResult?.let { resultText ->
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = ClinicalSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ClinicalBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("ai_meal_recommendation_card")
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "SYNTHESIZED MEAL PROTOCOL",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 0.8.sp,
                                    color = ClinicalTealPrimary
                                )

                                Spacer(modifier = Modifier.height(10.dp))
                                HorizontalDivider(color = ClinicalBorder)
                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = resultText,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = PrimaryText,
                                    lineHeight = 22.sp
                                )
                            }
                        }
                    }
                }

                item {
                    MedicalDisclaimerCard()
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        } else {
            // TAB 2: CONVERSATIONAL NUTRITION CONSULTATION
            Column(modifier = Modifier.fillMaxSize()) {
                // Chat Message List
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(chatMessages, key = { it.id }) { msg ->
                        ChatBubble(message = msg)
                    }

                    if (isAiLoading) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Surface(
                                    color = ClinicalSurface,
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, ClinicalBorder)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(14.dp),
                                            strokeWidth = 2.dp,
                                            color = ClinicalTealPrimary
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Consulting nutritional database...",
                                            fontSize = 12.sp,
                                            color = SecondaryText
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Quick Prompt Chips
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val promptChips = listOf(
                        "How much protein did I log today?",
                        "Compare White Rice vs Whole Wheat Roti",
                        "Recommend 500 kcal high-protein meal",
                        "How much chicken breast for 40g protein?",
                        "High protein vegetarian breakfast options"
                    )
                    items(promptChips) { chipText ->
                        Surface(
                            onClick = {
                                viewModel.sendAiChatMessage(chipText)
                            },
                            shape = RoundedCornerShape(6.dp),
                            color = ClinicalSurface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, ClinicalBorder)
                        ) {
                            Text(
                                text = chipText,
                                fontSize = 11.sp,
                                color = PrimaryText,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Input Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = userPromptText,
                        onValueChange = { userPromptText = it },
                        placeholder = { Text("Ask clinical query regarding foods or macro goals...", color = SecondaryText, fontSize = 13.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("ai_chat_text_input"),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = false,
                        maxLines = 3,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                if (userPromptText.isNotBlank()) {
                                    viewModel.sendAiChatMessage(userPromptText)
                                    userPromptText = ""
                                }
                            }
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (userPromptText.isNotBlank()) {
                                viewModel.sendAiChatMessage(userPromptText)
                                userPromptText = ""
                            }
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .background(ClinicalTealPrimary, RoundedCornerShape(8.dp))
                            .testTag("send_ai_message_button"),
                        enabled = userPromptText.isNotBlank() && !isAiLoading
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.sender.equals("user", ignoreCase = true)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Surface(
                color = ClinicalTealContainer,
                shape = CircleShape,
                border = androidx.compose.foundation.BorderStroke(1.dp, ClinicalBorder),
                modifier = Modifier
                    .size(28.dp)
                    .padding(top = 2.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = "AI",
                        tint = ClinicalTealPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Surface(
            color = if (isUser) ClinicalTealPrimary else ClinicalSurface,
            shape = RoundedCornerShape(8.dp),
            border = if (isUser) null else androidx.compose.foundation.BorderStroke(1.dp, ClinicalBorder),
            modifier = Modifier.fillMaxWidth(if (isUser) 0.85f else 0.9f)
        ) {
            Text(
                text = message.message,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isUser) Color.White else PrimaryText,
                modifier = Modifier.padding(12.dp),
                lineHeight = 20.sp
            )
        }
    }
}
