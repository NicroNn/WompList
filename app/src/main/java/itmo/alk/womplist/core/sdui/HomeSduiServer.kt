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
      "style": {
        "density": "regular"
      },
      "children": [
        {
          "type": "header",
          "style": {
            "variant": "hero"
          },
          "props": {
            "title": "Discover Anime",
            "showSecret": true
          }
        },
        {
          "type": "search",
          "style": {
            "variant": "outlined"
          },
          "props": {
            "hint": "Search anime up"
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
      "style": {
        "density": "regular"
      },
      "children": [
        {
          "type": "header",
          "style": {
            "variant": "hero"
          },
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
          "style": {
            "variant": "filled"
          },
          "props": {
            "hint": "Search anime down"
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

