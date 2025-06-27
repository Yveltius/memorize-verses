package com.yveltius.memorize.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yveltius.versememorization.domain.verses.GetVersesUseCase
import com.yveltius.versememorization.entity.verses.Verse
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class VersesListViewModel: ViewModel() {
    private val getVersesUseCase: GetVersesUseCase by inject(GetVersesUseCase::class.java)

    private val _uiState: MutableState<UiState> = mutableStateOf(value = UiState())
    val uiState: State<UiState> = _uiState

    private var _verses: List<Verse> = listOf()
    private val verses: List<Verse>
        get() = _verses

    fun getVerses() {
        viewModelScope.launch {
            getVersesUseCase.getVerses()
                .onSuccess { verses ->
                    this@VersesListViewModel._verses = verses

                    updateUiState()
                }
                .onFailure {
                    // todo display fetch error
                }
        }
    }

    private fun updateUiState() {
        _uiState.value = UiState(
            verses = verses
        )
    }

    data class UiState(
        val verses: List<Verse> = listOf()
    )
}