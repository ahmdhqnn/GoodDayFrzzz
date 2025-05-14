package org.ahmad.project1app.ui.screen

import androidx.lifecycle.ViewModel
import org.ahmad.project1app.database.BreatheasyDao
import org.ahmad.project1app.model.Module

class ReadingViewModel(private  val dao: BreatheasyDao): ViewModel() {
    suspend fun getModule(id: Long):        Module? {
        return dao.getModuleById(id)
    }
}