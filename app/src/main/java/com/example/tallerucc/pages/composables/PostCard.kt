package com.example.tallerucc.pages.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.tallerucc.R
import com.example.tallerucc.ui.theme.DarkBlue
import com.example.tallerucc.ui.theme.DarkGrey
import com.example.tallerucc.ui.theme.LightBlue
import com.example.tallerucc.ui.theme.LightGrey
import com.example.tallerucc.ui.theme.Typography
import com.example.tallerucc.ui.theme.White

@Composable
fun PostCard(
    post: Map<String, Any>,
    userId: String,
    isLiked: Boolean,
    isCommunityOfficial: Boolean, // Indicador adicional para comunidades oficiales
    createdAt: String, // Nuevo parámetro
    onLikeToggle: (String, Boolean) -> Unit
) {
    val postId = post["postId"] as? String ?: return
    var likesCount by remember { mutableStateOf((post["likesCount"] as? Long) ?: 0) }
    var liked by remember { mutableStateOf(isLiked) }
    val isPostOfficial = post["isOfficial"] as? Boolean ?: false // Oficial por parte del post


    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = White,
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header con logo de la comunidad y autor
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)
            ) {
                val communityLogo = post["communityLogo"] as? String
                if (communityLogo == "https://i.imgur.com/DCzUFzG.png") {
                    AsyncImage(
                        model = communityLogo,
                        contentDescription = "Logo de la comunidad",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Fit
                    )
                } else if (!communityLogo.isNullOrEmpty()) {
                    AsyncImage(
                        model = communityLogo,
                        contentDescription = "Logo de la comunidad",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Nombre de la comunidad con ícono de oficial (si aplica)
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = post["communityName"] as? String ?: "Sin Comunidad",
                            style = Typography.bodyMedium,
                            color = DarkBlue.copy(alpha = 0.8f)
                        )
                        if (isPostOfficial || isCommunityOfficial) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Image(
                                painter = painterResource(id = R.drawable.ic_verified),
                                contentDescription = "Oficial",
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(bottom = 0.dp),
                                colorFilter = ColorFilter.tint(LightBlue)
                            )
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically

                    ) {
                        Text(
                            text = "by ${post["authorId"] as String}",
                            style = Typography.bodySmall,
                            color = DarkGrey.copy(alpha = 0.6f)
                        )
//                        Spacer(modifier = Modifier.width(6.dp))
                        VerticalDivider(
                            thickness = 1.dp,
                            color = LightGrey.copy(alpha = 0.4f),
                            modifier = Modifier
                                .height(12.dp)
                                .padding(horizontal = 6.dp) // Establece la altura del divisor
                        )

                        Text(
                            text = createdAt,
                            style = Typography.bodySmall,
                            color = DarkGrey.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            // Título del post
            Text(
                text = post["title"] as String,
                style = Typography.titleSmall,
                color = DarkBlue,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
            )

            Text(
                text = post["textContent"] as String,
                style = Typography.bodyMedium,
                color = DarkGrey,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            )

            // Carrusel de imágenes con indicador de puntos
            val imageUrls = post["imageUrls"] as? List<String> ?: emptyList()
            if (imageUrls.isNotEmpty()) {
                val pagerState = rememberPagerState { imageUrls.size }
                val maxVisibleDots = 4 // Máximo de puntos visibles

                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)) {
                    // Carrusel de imágenes
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                    ) { page ->
                        AsyncImage(
                            model = imageUrls[page],
                            contentDescription = "Imagen del post",
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    // Mostrar indicador de puntos solo si hay más de una imagen
                    if (imageUrls.size > 1) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(16.dp)
                                .background(
                                    DarkGrey.copy(alpha = 0.8f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            val startIndex = maxOf(0, pagerState.currentPage - maxVisibleDots / 2)
                            val endIndex = minOf(imageUrls.size, startIndex + maxVisibleDots)

                            (startIndex until endIndex).forEach { index ->
                                val isActive = pagerState.currentPage == index
                                Box(
                                    modifier = Modifier
                                        .size(if (isActive) 8.dp else 6.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isActive) White.copy(alpha = 0.9f) else White.copy(
                                                alpha = 0.2f
                                            )
                                        )
                                )
                            }
                        }
                    }
                }
            }

            // Botón de "like"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 16.dp)
            ) {
                IconButton(
                    onClick = {
                        liked = !liked
                        likesCount = if (liked) likesCount + 1 else likesCount - 1
                        onLikeToggle(postId, liked)
                    },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(LightGrey.copy(alpha = 0.1f))
                        .height(40.dp)
                        .width(60.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ThumbUp,
                            contentDescription = "Like",
                            tint = if (liked) LightBlue else DarkGrey
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$likesCount",
                            style = Typography.bodySmall,
                            color = if (liked) LightBlue else DarkGrey
                        )
                    }
                }
            }
        }
    }
}

//