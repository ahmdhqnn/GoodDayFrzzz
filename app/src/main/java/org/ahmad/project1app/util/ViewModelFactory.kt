package org.ahmad.project1app.util

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.ahmad.project1app.database.BreatheasyDb
import org.ahmad.project1app.ui.screen.GlossaryViewModel
import org.ahmad.project1app.ui.screen.ModuleViewModel

class ViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory{
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val dao = BreatheasyDb.getInstance(context).dao
        if (modelClass.isAssignableFrom(ModuleViewModel::class.java)){
            return ModuleViewModel(dao) as T
        }
        else if (modelClass.isAssignableFrom(GlossaryViewModel::class.java)){
            return GlossaryViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}