package com.pcoliveira.meuponto.data


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RegistroDao {
    @Insert
    suspend fun inserir(registro: Registro)

    @Query("SELECT * FROM Registro ORDER BY timestamp DESC")
    fun listar(): LiveData<List<Registro>>
}
