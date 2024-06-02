package ch.epfl.skysync.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ch.epfl.skysync.ui.theme.TOP_CORNER_ROUNDED

/**
 * displays a top banner
 *
 * @param topTitle the title of the banner
 * @param topBannerColor the color of the banner
 */
@Composable
fun TopBanner(topTitle: String, topBannerColor: Color, paddingValues: PaddingValues) {
  Text(
      text = topTitle,
      style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
      modifier =
          Modifier.background(color = topBannerColor, shape = TOP_CORNER_ROUNDED)
              .fillMaxWidth()
              .padding(
                  top = paddingValues.calculateTopPadding() + 16.dp,
                  start = 16.dp,
                  end = 16.dp,
                  bottom = 16.dp),
      color = Color.White,
      textAlign = TextAlign.Center)
}
