package com.pcoliveira.meuponto.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pcoliveira.meuponto.data.RegistroDatabase
import com.pcoliveira.meuponto.model.AjustePonto
import kotlinx.coroutines.launch

class AjusteViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = RegistroDatabase.getDatabase(application).ajustePontoDao()

    fun solicitarAjuste(registroId: Int, timestamp: Long, latitude: Double, longitude: Double, motivo: String) {
        val ajuste = AjustePonto(
            registroId = registroId,
            timestamp = timestamp,
            latitude = latitude,
            longitude = longitude,
            motivo = motivo
        )
        viewModelScope.launch {
            dao.inserir(ajuste)
        }
    }
}