package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PrimaryViolet
import com.example.ui.theme.SecondaryCyan
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

/**
 * High-performance search bar component with debounced query handling,
 * instant local input state, clear button, loading indicators,
 * search history, and trending suggestions.
 */
@Composable
fun CatalogSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onSearchSubmitted: (String) -> Unit = {},
    onClear: () -> Unit = {},
    isSearching: Boolean = false,
    searchHistory: List<String> = emptyList(),
    onHistoryItemClick: (String) -> Unit = {},
    onRemoveHistoryItem: (String) -> Unit = {},
    onClearHistory: () -> Unit = {},
    suggestions: List<String> = listOf("Stranger Things", "Dune 2", "Arcane", "Netflix", "HBO Max", "4K HDR"),
    onSuggestionClick: (String) -> Unit = {},
    debounceMillis: Long = 300L,
    placeholderText: String = "חיפוש סרטים, סדרות, שחקנים, במאים...",
    resultCount: Int? = null,
    showSuggestions: Boolean = true
) {
    var localText by remember(query) { mutableStateOf(query) }
    var isFocused by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // Debounce query changes from local typing to the parent/ViewModel
    LaunchedEffect(localText) {
        if (localText == query) return@LaunchedEffect
        delay(debounceMillis)
        onQueryChange(localText)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("catalog_search_bar")
    ) {
        // Search Input Box
        OutlinedTextField(
            value = localText,
            onValueChange = { localText = it },
            placeholder = {
                Text(
                    text = placeholderText,
                    color = TextMuted,
                    fontSize = 13.sp,
                    maxLines = 1
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "חיפוש / Search",
                    tint = if (localText.isNotBlank() || isFocused) SecondaryCyan else TextMuted
                )
            },
            trailingIcon = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    if (isSearching) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(18.dp)
                                .testTag("search_loading_indicator"),
                            strokeWidth = 2.dp,
                            color = SecondaryCyan
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    if (resultCount != null && resultCount > 0 && localText.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(PrimaryViolet.copy(alpha = 0.25f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "$resultCount",
                                color = SecondaryCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                    }

                    if (localText.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                localText = ""
                                onClear()
                                onQueryChange("")
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("search_clear_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "נקה חיפוש / Clear search",
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    if (localText.isNotBlank()) {
                        onSearchSubmitted(localText)
                    }
                }
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = SurfaceVariantDark,
                unfocusedContainerColor = SurfaceVariantDark,
                disabledContainerColor = SurfaceVariantDark,
                focusedIndicatorColor = PrimaryViolet,
                unfocusedIndicatorColor = if (localText.isNotBlank()) SecondaryCyan.copy(alpha = 0.4f) else Color.Transparent,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                cursorColor = SecondaryCyan
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 2.dp)
                .onFocusChanged { isFocused = it.isFocused }
                .testTag("search_text_field")
        )

        // Suggestions & Recent Searches strip (shown when search box is empty or focused)
        if (showSuggestions && localText.isBlank() && (searchHistory.isNotEmpty() || suggestions.isNotEmpty())) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Recent searches
                searchHistory.forEach { item ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceVariantDark)
                            .border(1.dp, PrimaryViolet.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .clickable {
                                localText = item
                                keyboardController?.hide()
                                focusManager.clearFocus()
                                onHistoryItemClick(item)
                                onSearchSubmitted(item)
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("search_history_chip_$item")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = SecondaryCyan,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = item,
                                color = TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "הסר מההיסטוריה",
                                tint = TextMuted,
                                modifier = Modifier
                                    .size(12.dp)
                                    .clickable { onRemoveHistoryItem(item) }
                            )
                        }
                    }
                }

                // Curated trending quick search tags
                suggestions.forEach { tag ->
                    if (!searchHistory.contains(tag)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(SurfaceVariantDark.copy(alpha = 0.7f))
                                .clickable {
                                    localText = tag
                                    keyboardController?.hide()
                                    focusManager.clearFocus()
                                    onSuggestionClick(tag)
                                    onSearchSubmitted(tag)
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("search_suggestion_chip_$tag")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TrendingUp,
                                    contentDescription = null,
                                    tint = PrimaryViolet,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = tag,
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                // Clear history button if history exists
                if (searchHistory.isNotEmpty()) {
                    Text(
                        text = "נקה היסטוריה",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clickable { onClearHistory() }
                            .padding(horizontal = 4.dp, vertical = 4.dp)
                            .testTag("clear_search_history_button")
                    )
                }
            }
        }
    }
}
