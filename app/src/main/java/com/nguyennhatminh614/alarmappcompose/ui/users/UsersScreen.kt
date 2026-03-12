package com.nguyennhatminh614.alarmappcompose.ui.users

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.nguyennhatminh614.alarmappcompose.domain.User
import com.nguyennhatminh614.alarmappcompose.ui.components.NoNetwork

@Composable
fun UsersScreenRoute(
    onUserClick: (String) -> Unit
) {
    val viewModel = hiltViewModel<UsersViewModel>()
    val uiState by viewModel.uiState.collectAsState()

    UserScreen(
        isOffline = uiState.offline,
        items = uiState.list,
        onUserClick = onUserClick
    )
}

@Composable
fun UserScreen(
    modifier: Modifier = Modifier,
    isOffline: Boolean,
    items: List<User>,
    onUserClick: (String) -> Unit
) {
    if (isOffline) {
        NoNetwork(modifier = modifier)
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            items(
                items = items,
                key = { it.id }
            ) { item ->
                UserItem(item = item, onUserClick = onUserClick)
            }
        }
    }
}

@Composable
fun UserItem(
    modifier: Modifier = Modifier,
    item: User, 
    onUserClick: (String) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onUserClick(item.username) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            modifier = Modifier
                .size(40.dp),
            model = item.avatar,
            contentDescription = null
        )
        Text(
            modifier = Modifier.padding(start = 16.dp),
            text = item.username,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Preview
@Composable
fun UsersScreenRoutePreview() {
    UserScreen(
        isOffline = false,
        items = listOf(
            User(1, "https://avatars.githubusercontent.com/u/1?v=4", "mojombo"),
            User(2, "https://avatars.githubusercontent.com/u/2?v=4", "defunkt")
        ),
        onUserClick = {}
    )
}