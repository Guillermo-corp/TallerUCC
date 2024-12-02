package com.example.tallerucc.pages.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.tallerucc.model.Community
import com.example.tallerucc.ui.theme.DarkBlue
import com.example.tallerucc.ui.theme.DarkGrey
import com.example.tallerucc.ui.theme.LightBlue
import com.example.tallerucc.ui.theme.Typography

@Composable
fun CommunityHeader(community: Community) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Banner
        if (!community.bannerUrl.isNullOrEmpty()) {
            AsyncImage(
                model = community.bannerUrl,
                contentDescription = "Banner de la comunidad",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentScale = ContentScale.Crop
            )
        }

        // Logo, nombre y descripción
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.Start)
        ) {
            if (!community.iconUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = community.iconUrl,
                    contentDescription = "Ícono de la comunidad",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color.Gray),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = community.name,
                    style = Typography.titleMedium,
                    color = DarkBlue
                )
                Text(
                    text = "${community.membersCount} ${if (community.membersCount == 1) "miembro" else "miembros"}",
                    style = Typography.bodyMedium,
                    color = LightBlue
                )
            }
        }

        Text(
            text = community.description,
            style = Typography.bodySmall,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .align(Alignment.Start),
            color = DarkGrey
        )

        HorizontalDivider(
            color = DarkBlue.copy(alpha = 0.5f),
            thickness = 1.dp,
            modifier = Modifier
                .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 8.dp)
        )
    }
}
