package com.evanemran.buzz.presentation.chat

import com.evanemran.buzz.domain.model.Message

data class ChatState(
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = false
)
