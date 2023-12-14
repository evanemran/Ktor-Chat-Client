package com.evanemran.buzz.presentation.chat

import android.graphics.drawable.Icon
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextFieldDefaults.indicatorLine
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.evanemran.buzz.ui.theme.backgroundColor
import com.evanemran.buzz.ui.theme.messageContainerColor
import com.evanemran.buzz.ui.theme.otherMessageColor
import com.evanemran.buzz.ui.theme.ownMessageColor
import com.evanemran.buzz.ui.theme.tabBarColor
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    username: String?,
    viewModel: ChatViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        viewModel.toastEvent.collectLatest { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                viewModel.connectToChat()
            } else if (event == Lifecycle.Event.ON_STOP) {
                viewModel.disconnect()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val state = viewModel.state.value

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    tabBarColor
                ),
                title = {Text(text = "Custom Room", fontWeight = FontWeight.Bold, color = Color.White)}
            )
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
                    .fillMaxWidth(),
                reverseLayout = true
            ) {
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
                items(state.messages) { message ->
                    val isOwnMessage = message.username == username
                    Box(
                        contentAlignment = if (isOwnMessage) Alignment.CenterEnd else Alignment.CenterStart,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .defaultMinSize(minWidth = 200.dp)
                                .drawBehind {
                                    val cornerRadius = 10.dp.toPx()
                                    val triangleHeight = 12.dp.toPx()
                                    val triangleWidth = 14.dp.toPx()
                                    val trianglePath = Path().apply {
                                        if (isOwnMessage) {
                                            moveTo(size.width, size.height - cornerRadius)
                                            lineTo(size.width, size.height + triangleHeight)
                                            lineTo(
                                                size.width - triangleWidth,
                                                size.height - cornerRadius
                                            )
                                            close()
                                        } else {
                                            moveTo(0f, size.height - cornerRadius)
                                            lineTo(0f, size.height + triangleHeight)
                                            lineTo(triangleWidth, size.height - cornerRadius)
                                            close()
                                        }
                                    }
                                    drawPath(
                                        path = trianglePath,
                                        color = if (isOwnMessage) ownMessageColor else otherMessageColor
                                    )
                                }
                                .background(
                                    color = if (isOwnMessage) ownMessageColor else otherMessageColor,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .padding(8.dp)
                        ) {
                            Text(
                                text = message.username,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                            )
                            Text(
                                text = message.text,
                                color = Color.White,
                                fontWeight = FontWeight.Normal,
                            )
                            Text(
                                text = message.formattedTime,
                                color = Color.White,
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                TextField(
                        value = viewModel.messageText.value,
                    onValueChange = viewModel::onMessageChange,
                    placeholder = {
                        Text(text = "Enter a message")
                    },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        cursorColor = Color.DarkGray,
                        focusedContainerColor = messageContainerColor,
                        unfocusedContainerColor = messageContainerColor,
                        disabledContainerColor = messageContainerColor,
                        focusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .weight(1f),
                    shape = RoundedCornerShape(16.dp)
                )
                IconButton(
                    onClick = viewModel::sendMessage) {
                    Icon(
                        imageVector = Icons.Rounded.Send,
                        contentDescription = "Send",
                        modifier = Modifier
                            .size(32.dp),
                        tint = ownMessageColor
                    )
                }
            }
        }
    }
}