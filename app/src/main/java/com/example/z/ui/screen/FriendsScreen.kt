package com.example.z.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.z.ui.Friend
import com.example.z.ui.FriendRequest
import com.example.z.utils.TokenManager
import com.example.z.viewmodel.FriendsViewModel
import kotlinx.coroutines.delay

@Composable
fun FriendsScreen(
    onBackClick: () -> Unit,
    onFriendClick: (Friend) -> Unit,
    viewModel: FriendsViewModel = viewModel()
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val token by remember { mutableStateOf(tokenManager.getToken()) }

    // Состояние для поиска
    var searchText by remember { mutableStateOf("") }

    // Загрузка данных при первом открытии
    LaunchedEffect(Unit) {
        token?.let { viewModel.loadFriends(it) }
    }

    Scaffold(
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Поиск друзей
            TextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("Введите логин друга") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    token?.let {
                        if (searchText.isNotBlank()) {
                            viewModel.sendFriendRequest(it, searchText)
                            searchText = "" // Очищаем поле после отправки
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Добавить в друзья")
            }

            // Отображение ошибок
            viewModel.errorMessage?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                LaunchedEffect(error) {
                    delay(3000)
                    viewModel.clearError()
                }
            }

            // Запросы в друзья
            if (viewModel.pendingRequests.isNotEmpty()) {
                Text(
                    "Запросы в друзья (${viewModel.pendingRequests.size})",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                    items(viewModel.pendingRequests) { request ->
                        FriendRequestItem(
                            request = request,
                            onAccept = {
                                token?.let { viewModel.respondToRequest(it, request.id, true) }
                            },
                            onReject = {
                                token?.let { viewModel.respondToRequest(it, request.id, false) }
                            }
                        )
                    }
                }
            }

            // Список друзей
            Text(
                "Ваши друзья (${viewModel.friends.size})",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            LazyColumn {
                items(viewModel.friends) { friend ->
                    FriendItem(
                        friend = friend,
                        onClick = { onFriendClick(friend) }
                    )
                }
            }
        }
    }
}

@Composable
fun FriendRequestItem(
    request: FriendRequest,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "${request.senderLogin} хочет добавить вас в друзья",
                modifier = Modifier.weight(1f)
            )
            Row {
                Button(
                    onClick = onAccept,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text("Принять")
                }
                Button(
                    onClick = onReject,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Отклонить")
                }
            }
        }
    }
}

@Composable
fun FriendItem(
    friend: Friend,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Друг",
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "${friend.firstName} ${friend.lastName}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    "@${friend.login}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}