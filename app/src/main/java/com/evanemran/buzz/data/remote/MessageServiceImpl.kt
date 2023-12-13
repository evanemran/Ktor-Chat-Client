package com.evanemran.buzz.data.remote

import com.evanemran.buzz.data.remote.dto.MessageDto
import com.evanemran.buzz.domain.model.Message
import io.ktor.client.HttpClient
import io.ktor.client.request.get

class MessageServiceImpl(
    private val client: HttpClient
): MessageService {
    override suspend fun getAllMessages(): List<Message> {
        return try {
            client.get<List<MessageDto>>(MessageService.Endpoints.GetAllMessages.url)
                .map {
                    it.toMessage()
                }
        }
        catch (e: Exception) {
            emptyList()
        }
    }
}