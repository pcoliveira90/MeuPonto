package com.pcoliveira.meuponto.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import kotlinx.coroutines.launch

class RegistroViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = RegistroDatabase.getDatabase(application).registroDao()

    fun registrarPonto(latitude: Double, longitude: Double) {
        val registro = Registro(timestamp = System.currentTimeMillis(), latitude = latitude, longitude = longitude)
        viewModelScope.launch { dao.inserir(registro) }
    }

    fun todosRegistros(): LiveData<List<Registro>> = dao.listar()
}