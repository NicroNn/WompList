package itmo.alk.womplist.core.sdui

import kotlinx.coroutines.delay

enum class ScrollDirection {
    Up,
    Down
}

class HomeSduiServer {

    val initialJson: String = """
    {
      "type": "column",
      "children": [
        {
          "type": "header",
          "props": {
            "title": "Discover Anime",
            "showSecret": true
          }
        },
        {
          "type": "search",
          "props": {
            "hint": "Search anime"
          }
        },
        {
          "type": "anime_list"
        }
      ]
    }
    """.trimIndent()

    private val footerSearchJson: String = """
    {
      "type": "column",
      "children": [
        {
          "type": "header",
          "props": {
            "title": "Discover Anime",
            "showSecret": true
          }
        },
        {
          "type": "anime_list"
        },
        {
          "type": "search",
          "weight": 1,
          "props": {
            "hint": "Search anime"
          }
        }
      ]
    }
    """.trimIndent()

    suspend fun requestHomeJson(direction: ScrollDirection): String {
        delay(120)
        return when (direction) {
            ScrollDirection.Down -> initialJson
            ScrollDirection.Up -> footerSearchJson
        }
    }
}

