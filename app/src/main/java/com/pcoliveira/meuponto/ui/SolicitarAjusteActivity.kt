package com.pcoliveira.meuponto.ui


import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.LocationServices
import com.pcoliveira.meuponto.R
import com.pcoliveira.meuponto.viewmodel.AjusteViewModel


class SolicitarAjusteActivity : AppCompatActivity() {

    private lateinit var viewModel: AjusteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_solicitar_ajuste)

        viewModel = ViewModelProvider(this)[AjusteViewModel::class.java]
        val registroId = intent.getIntExtra("registroId", -1)

        val motivoEditText = findViewById<EditText>(R.id.etMotivo)
        val btnSalvar = findViewById<Button>(R.id.btnEnviar)

        btnSalvar.setOnClickListener {
            val motivo = motivoEditText.text.toString()
            if (motivo.isBlank()) {
                Toast.makeText(this, "Informe o motivo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        viewModel.solicitarAjuste(
                            registroId = registroId,
                            timestamp = System.currentTimeMillis(),
                            latitude = location.latitude,
                            longitude = location.longitude,
                            motivo = motivo
                        )
                        Toast.makeText(this, "Solicitação registrada", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, "Localização não disponível", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Permissão de localização não concedida", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
