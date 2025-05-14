package org.ahmad.project1app.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import org.ahmad.project1app.database.BreatheasyDao
import org.ahmad.project1app.model.Glossary

class GlossaryViewModel(dao: BreatheasyDao) : ViewModel() {
    val data: StateFlow<List<Glossary>> = dao.getGlossary().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyList()
    )
}