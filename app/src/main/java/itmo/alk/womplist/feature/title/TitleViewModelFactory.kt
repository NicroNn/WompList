package itmo.alk.womplist.feature.title

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import itmo.alk.womplist.data.repository.AnimeRepository

class TitleViewModelFactory(
    private val repository: AnimeRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TitleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TitleViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

