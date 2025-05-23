package com.example.z.ui.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.z.R
import com.example.z.model.FriendModel
import com.example.z.model.requests.FriendRequest
import com.example.z.utils.TokenManager
import com.example.z.viewmodel.FriendsViewModel
import kotlin.collections.isNotEmpty

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsScreen(
    onBackClick: () -> Unit,
    onFriendClick: (FriendModel) -> Unit,
    viewModel: FriendsViewModel = viewModel()
) {
    val context = LocalContext.current
    val token = remember { TokenManager(context).getToken() ?: "" }
    var emailInput by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        if (token.isNotBlank()) {
            viewModel.loadFriends(token)
            viewModel.loadPendingRequests(token)
        }
    }

    Scaffold(
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(62.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.arrow),
                        contentDescription = "Назад",
                    )
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Добавить друга", fontWeight = FontWeight.Medium, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text("По email") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            if (emailInput.isNotBlank()) {
                                viewModel.sendFriendRequestByEmail(token, emailInput)
                                emailInput = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Отправить запрос")
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outline
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    if (viewModel.pendingRequests.isEmpty()) {
                        Text("Нет запросов в друзья!", modifier = Modifier.align(Alignment.Center))
                    } else {
                        LazyColumn {
                            items(viewModel.pendingRequests) { request ->
                                FriendRequestCard(
                                    request = request,
                                    onAccept = { viewModel.respondToRequest(token, request.senderLogin, true) },
                                    onReject = { viewModel.respondToRequest(token, request.senderLogin, false) }
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outline
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    if (viewModel.friends.isEmpty()) {
                        Text("Список друзей пуст!", modifier = Modifier.align(Alignment.Center))
                    } else {
                        LazyColumn {
                            items(viewModel.friends) { friend ->
                                FriendCard(
                                    friend = friend,
                                    onClick = { onFriendClick(friend) },
                                    onDelete = { viewModel.deleteFriend(token, friend.login) }
                                )
                            }
                        }
                    }
                }
            }

        }
    }
}

@Composable
fun FriendRequestCard(
    request: FriendRequest,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Друг",
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "${request.senderLogin} хочет добавить вас в друзья!",
                modifier = Modifier.weight(1f)
            )
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                TextButton(
                    onClick = onAccept,
                    modifier = Modifier.width(100.dp)
                ) {
                    Text("Принять")
                }
                TextButton(
                    onClick = onReject,
                    modifier = Modifier.width(100.dp),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Отклонить")
                }
            }
        }
    }
}

@Composable
fun FriendCard(
    friend: FriendModel,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Друг",
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Имя: ${friend.firstName}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Фамилия: ${friend.lastName}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Логин: ${friend.login}",
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Удалить",
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}