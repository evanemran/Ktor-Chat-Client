package com.evanemran.buzz.data.remote
import com.evanemran.buzz.domain.model.Message
import com.evanemran.buzz.util.Resource
import kotlinx.coroutines.flow.Flow

interface ChatSocketService {
    suspend fun initSession(
        username: String
    ): Resource<Unit>

    suspend fun sendMessage(
        message: String
    )

    fun observeMessages(): Flow<Message>

    suspend fun closeSession()

    companion object {
        const val BASE_URL = "ws://10.0.2.2:8082"
    }

    sealed class Endpoints(val url: String) {
        object ChatSocket: Endpoints("$BASE_URL/chat-socket")
    }
}