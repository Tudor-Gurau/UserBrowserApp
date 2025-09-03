package com.example.userbrowserapp.presentation.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.userbrowserapp.domain.model.UserModel
import com.example.userbrowserapp.util.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onUserClick: (UserModel) -> Unit,
    refreshKey: Int = 0,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val usersState by viewModel.usersState.collectAsState()
    val bookmarkedUsers by viewModel.bookmarkedUsers.collectAsState()
    val isLoadingMore by viewModel.isLoadingMore.collectAsState()
    val listState = rememberLazyListState()
    var showBookmarkedUsers by remember { mutableStateOf(false) }

    // Pull to refresh
    val pullRefreshState = rememberPullToRefreshState()
    var isRefreshing by remember { mutableStateOf(false) }

    // Refresh data when returning from detailed screen
    LaunchedEffect(refreshKey) {
        if (refreshKey > 0) {
            viewModel.refreshAllData()
        }
    }

    LaunchedEffect(isRefreshing, showBookmarkedUsers) {
        if (isRefreshing) {
            if (!showBookmarkedUsers) {
                viewModel.refreshUsers()
            }
            isRefreshing = false
        }
    }

    // Load more users when reaching the end
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo }
            .collect { layoutInfo ->
                val totalItems = layoutInfo.totalItemsCount
                val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

                if (lastVisibleItem >= totalItems - 3 && !isLoadingMore && !showBookmarkedUsers) {
                    viewModel.loadMoreUsers()
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Browser") },
                actions = {
                    IconButton(
                        onClick = { showBookmarkedUsers = !showBookmarkedUsers }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Toggle Bookmarked Users",
                            tint = if (showBookmarkedUsers) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when (usersState) {
            is Resource.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is Resource.Success -> {
                val users = if (showBookmarkedUsers) bookmarkedUsers else usersState.data ?: emptyList()

                if (users.isEmpty() && showBookmarkedUsers) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No bookmarked users yet")
                    }
                } else {
                    PullToRefreshBox(
                        isRefreshing = isRefreshing && !showBookmarkedUsers,
                        onRefresh = { if (!showBookmarkedUsers) { isRefreshing = true } },
                        state = pullRefreshState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize(),
                            state = listState,
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(users) { user ->
                                UserCard(
                                    user = user,
                                    onUserClick = onUserClick,
                                    onBookmarkClick = { viewModel.toggleBookmark(user) }
                                )
                            }

                            // Show loading indicator for pagination only when not showing bookmarked users
                            if (!showBookmarkedUsers && users.isNotEmpty() && isLoadingMore) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            is Resource.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = usersState.message ?: "An error occurred",
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(onClick = { viewModel.refreshUsers() }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserCard(
    user: UserModel,
    onUserClick: (UserModel) -> Unit,
    onBookmarkClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onUserClick(user) },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // User Image
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(user.picture)
                    .crossfade(true)
                    .build(),
                contentDescription = "User profile picture",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            
            // User Info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = user.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = user.phone,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = user.email,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Bookmark Button
            IconButton(onClick = onBookmarkClick) {
                Icon(
                    imageVector = if (user.isBookmarked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (user.isBookmarked) "Remove from bookmarks" else "Add to bookmarks",
                    tint = if (user.isBookmarked) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
