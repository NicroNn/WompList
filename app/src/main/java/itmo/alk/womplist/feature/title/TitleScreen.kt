package itmo.alk.womplist.feature.title

import android.app.Activity
import android.view.ContextThemeWrapper
import android.widget.TextView
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.viewpager2.widget.ViewPager2
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import itmo.alk.womplist.R
import itmo.alk.womplist.data.LocalAnimeRepository
import itmo.alk.womplist.data.repository.AnimeRepository
import itmo.alk.womplist.data.repository.AnimeStatus
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.luminance

fun android.content.Context.findActivity(): Activity? {
    var ctx = this
    while (ctx is android.content.ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}

@Composable
fun TitleScreen(navController: NavController, titleId: Long) {
    val repository: AnimeRepository = LocalAnimeRepository.current
    val context = LocalContext.current
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f

    AndroidView(
        factory = { originalContext ->
            val themeRes = if (isDark) R.style.Theme_WompList_Dark
            else R.style.Theme_WompList_Light
            ContextThemeWrapper(originalContext, themeRes)
                .let { LayoutInflater.from(it).inflate(R.layout.title_screen, null, false) }
        },
        update = { view ->
            val anime = repository.getAnimeById(titleId) ?: return@AndroidView

            val poster = view.findViewById<ImageView>(R.id.posterImage)
            val titleText = view.findViewById<TextView>(R.id.titleText)
            val ratingText = view.findViewById<TextView>(R.id.ratingText)
            val yearText = view.findViewById<TextView>(R.id.yearText)
            val watchButton = view.findViewById<MaterialButton>(R.id.watchButton)
            val statusButton = view.findViewById<MaterialButton>(R.id.addToListButton)

            poster.setImageResource(anime.posterResId)
            titleText.text = anime.title
            ratingText.text = "${anime.rating}/10"
            yearText.text = anime.year.toString()

            var currentStatus = repository.getStatusForAnime(titleId)

            fun updateStatusButton() {
                when (currentStatus) {
                    AnimeStatus.WATCHING -> {
                        statusButton.setIconResource(android.R.drawable.ic_menu_view)
                        statusButton.text = view.context.getString(R.string.in_watching)
                    }
                    AnimeStatus.PLANNED -> {
                        statusButton.setIconResource(android.R.drawable.ic_menu_recent_history)
                        statusButton.text = view.context.getString(R.string.in_planned)
                    }
                    AnimeStatus.COMPLETED -> {
                        statusButton.setIconResource(android.R.drawable.checkbox_on_background)
                        statusButton.text = view.context.getString(R.string.in_completed)
                    }
                    else -> {
                        statusButton.setIconResource(android.R.drawable.ic_input_add)
                        statusButton.text = view.context.getString(R.string.add_to_list)
                    }
                }
            }

            updateStatusButton()

            // Status button click
            statusButton.setOnClickListener {
                val activity = view.context.findActivity() ?: return@setOnClickListener

                val options = mutableListOf(
                    activity.getString(R.string.watching),
                    activity.getString(R.string.planned),
                    activity.getString(R.string.completed)
                )
                if (currentStatus != null) {
                    options.add(activity.getString(R.string.remove_from_list))
                }

                MaterialAlertDialogBuilder(activity)
                    .setTitle(R.string.select_status)
                    .setItems(options.toTypedArray()) { _, which ->
                        when (which) {
                            0 -> repository.addToList(anime, AnimeStatus.WATCHING)
                            1 -> repository.addToList(anime, AnimeStatus.PLANNED)
                            2 -> repository.addToList(anime, AnimeStatus.COMPLETED)
                            3 -> repository.removeFromList(anime.id, currentStatus!!)
                        }
                        currentStatus = repository.getStatusForAnime(titleId)
                        updateStatusButton()
                    }
                    .setNegativeButton(R.string.cancel, null)
                    .show()
            }

            val tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)
            val viewPager = view.findViewById<ViewPager2>(R.id.viewPager)

            if (tabLayout.tabCount == 0) {
                tabLayout.addTab(tabLayout.newTab().setText(R.string.about))
                tabLayout.addTab(tabLayout.newTab().setText(R.string.episodes))
                tabLayout.addTab(tabLayout.newTab().setText(R.string.recommendations))
            }

            viewPager.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                override fun getItemCount() = 3

                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): RecyclerView.ViewHolder {
                    val tv = TextView(parent.context)
                    tv.setPadding(32, 32, 32, 32)
                    tv.layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    return object : RecyclerView.ViewHolder(tv) {}
                }

                override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                    val tv = holder.itemView as TextView
                    when (position) {
                        0 -> tv.text = anime.description
                        1 -> tv.text = anime.episodesList.joinToString("\n")
                        2 -> {
                            val recs = repository.allAnime.value.take(3)
                            tv.text = recs.joinToString("\n") { it.title }
                            tv.setOnClickListener {
                                navController.navigate("title/${recs.first().id}")
                            }
                        }
                    }
                }
            }

            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    viewPager.currentItem = tab.position
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })
        }
    )
}
