package itmo.alk.womplist.core.ui.utils

import android.os.Build
import android.text.Html
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.core.text.toHtml

@Composable
fun HtmlText(html: String, modifier: Modifier = Modifier) {
    val spanned = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
    } else {
        @Suppress("DEPRECATION")
        Html.fromHtml(html)
    }
    val annotatedString = AnnotatedString.fromHtml(spanned.toHtml())
    SelectionContainer {
        Text(text = annotatedString, modifier = modifier)
    }
}