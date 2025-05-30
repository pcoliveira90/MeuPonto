package com.pcoliveira.meuponto.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider

class RegistroActivity : AppCompatActivity() {
    private lateinit var viewModel: RegistroViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var listaRegistrosLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        viewModel = ViewModelProvider(this)[RegistroViewModel::class.java]
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val button = findViewById<Button>(R.id.button_registrar)
        listaRegistrosLayout = findViewById(R.id.lista_registros)

        button.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                return@setOnClickListener
            }

            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    viewModel.registrarPonto(it.latitude, it.longitude)
                    Toast.makeText(this, "Ponto registrado", Toast.LENGTH_SHORT).show()
                } ?: Toast.makeText(this, "Localização não disponível", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.todosRegistros().observe(this) { registros ->
            listaRegistrosLayout.removeAllViews()
            registros.forEach { registro ->
                val texto = TextView(this).apply {
                    text = "ID ${registro.id}: ${java.util.Date(registro.timestamp)}"
                    textSize = 16f
                }
                val botao = Button(this).apply {
                    text = "Solicitar Ajuste"
                    setOnClickListener {
                        val intent = Intent(this@RegistroActivity, SolicitarAjusteActivity::class.java)
                        intent.putExtra("registroId", registro.id)
                        startActivity(intent)
                    }
                }
                listaRegistrosLayout.addView(texto)
                listaRegistrosLayout.addView(botao)
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) recreate() else Toast.makeText(this, "Permissão negada", Toast.LENGTH_SHORT).show()
    }
}
