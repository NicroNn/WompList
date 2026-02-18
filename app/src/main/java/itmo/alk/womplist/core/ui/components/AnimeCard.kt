package itmo.alk.womplist.core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import itmo.alk.womplist.R

enum class AnimeCardType {
    HORIZONTAL, VERTICAL
}

@Composable
fun AnimeCard(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    type: AnimeCardType = AnimeCardType.HORIZONTAL
) {
    Card(
        modifier = modifier
            .padding(8.dp)
            .then(
                if (type == AnimeCardType.VERTICAL) {
                    Modifier.fillMaxWidth().aspectRatio(0.8f)
                } else {
                    Modifier.fillMaxWidth()
                }
            ),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(4.dp),
        onClick = onClick
    ) {
        if (type == AnimeCardType.HORIZONTAL) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.womp),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(MaterialTheme.shapes.small)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }
        } else if (type == AnimeCardType.VERTICAL) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.womp),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(8.dp)
                        .clip(MaterialTheme.shapes.small)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(8.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview
@Composable
fun AnimeCardHorizontalPreview() {
    AnimeCard(title = "Attack on Titan", onClick = {}, type = AnimeCardType.HORIZONTAL)
}

@Preview
@Composable
fun AnimeCardVerticalPreview() {
    AnimeCard(title = "Attack on Titan", onClick = {}, type = AnimeCardType.VERTICAL)
}