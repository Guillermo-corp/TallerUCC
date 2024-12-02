package com.example.tallerucc.pages.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.tallerucc.R
import com.example.tallerucc.model.Community
import com.example.tallerucc.ui.theme.DarkBlue
import com.example.tallerucc.ui.theme.LightBlue
import com.example.tallerucc.ui.theme.LightGrey
import com.example.tallerucc.ui.theme.Typography
import com.example.tallerucc.viewModel.CommunityViewModel

@Composable
fun CommunityCard(
    community: Community,
    isFollowed: Boolean,
    onFollowClick: (String) -> Unit,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Imagen del banner
            if (!community.bannerUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = community.bannerUrl,
                    contentDescription = "Banner de la comunidad",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
            ) {
                // Ícono de la comunidad
                if (!community.iconUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = community.iconUrl,
                        contentDescription = "Ícono de la comunidad",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Título de la comunidad con icono oficial
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = community.name,
                        style = Typography.titleSmall,
                        color = DarkBlue
                    )
                    if (community.isOfficial) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Image(
                            painter = painterResource(id = R.drawable.ic_verified),
                            contentDescription = "Comunidad Oficial",
                            modifier = Modifier
                                .size(22.dp)
                                .padding(bottom = 2.dp),
                            colorFilter = ColorFilter.tint(LightBlue)
                        )
                    }
                }

                IconButton(
                    onClick = { onFollowClick(community.name) },
                    modifier = Modifier.padding(start = 16.dp, bottom = 4.dp, top = 4.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = if (isFollowed) LightBlue.copy(alpha = 0.1f)
                        else LightGrey.copy(alpha = 0.1f),
                        contentColor = DarkBlue,
                    ),
                ) {
                    Icon(
                        imageVector = if (isFollowed) Icons.Filled.Favorite
                        else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Seguir comunidad",
                        modifier = Modifier.size(24.dp),
                    )
                }
            }
        }
    }
}

