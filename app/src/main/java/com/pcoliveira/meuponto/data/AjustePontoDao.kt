package com.pcoliveira.meuponto.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AjustePontoDao {
    @Insert
    suspend fun inserir(ajuste: AjustePonto)

    @Query("SELECT * FROM AjustePonto WHERE registroId = :registroId")
    fun buscarPorRegistro(registroId: Int): LiveData<List<AjustePonto>>

    @Query("SELECT * FROM AjustePonto")
    fun listarTodos(): LiveData<List<AjustePonto>>
}
