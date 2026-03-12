package com.nguyennhatminh614.alarmappcompose.ui.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.nguyennhatminh614.alarmappcompose.ui.components.NoNetwork

@Composable
fun DetailsScreenRoute() {
    val viewModel = hiltViewModel<DetailsViewModel>()
    val uiState by viewModel.uiState.collectAsState()

    DetailsScreen(
        uiState = uiState
    )
}

@Composable
fun DetailsScreen(
    modifier: Modifier = Modifier,
    uiState: DetailsUiState
) {
    if (uiState.offline) {
        NoNetwork(modifier = modifier)
    } else {
        Row(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            AsyncImage(
                modifier = Modifier.size(100.dp),
                model = uiState.detail.avatar,
                contentDescription = null
            )
            Column {
                Text(
                    modifier = Modifier.padding(start = 16.dp),
                    text = uiState.detail.name.orEmpty(),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                    text = uiState.formattedUserSince,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                    text = uiState.detail.location.orEmpty(),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}