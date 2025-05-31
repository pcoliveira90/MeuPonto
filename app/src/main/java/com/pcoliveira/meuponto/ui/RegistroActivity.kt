package com.pcoliveira.meuponto.ui

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.button.MaterialButton
import com.pcoliveira.meuponto.R
import com.pcoliveira.meuponto.adapter.RegistroAdapter
import com.pcoliveira.meuponto.viewmodel.RegistroViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RegistroActivity : AppCompatActivity() {
    private lateinit var viewModel: RegistroViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var textHoraAtual: TextView
    private lateinit var textLatitude: TextView
    private lateinit var textLongitude: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerViewRegistros: androidx.recyclerview.widget.RecyclerView
    private lateinit var registroAdapter: com.pcoliveira.meuponto.adapter.RegistroAdapter
    private val horaFormatada = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        viewModel = ViewModelProvider(this)[RegistroViewModel::class.java]
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val button = findViewById<MaterialButton>(R.id.button_registrar)
        textHoraAtual = findViewById(R.id.text_hora_atual)
        textLatitude = findViewById(R.id.text_latitude)
        textLongitude = findViewById(R.id.text_longitude)
        progressBar = findViewById(R.id.pbRegistrando)

  registroAdapter = RegistroAdapter(emptyList()) { /* onAjustarClick não implementado */ }
        recyclerViewRegistros = findViewById(R.id.lista_registros_layout)
        recyclerViewRegistros.adapter = registroAdapter
        recyclerViewRegistros.layoutManager = LinearLayoutManager(this)

        button.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE
            button.isEnabled = false

            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                try {
                    location?.let {
                        val hora = horaFormatada.format(Date())
                        textHoraAtual.text = "Hora atual: $hora"
                        textLatitude.text = "Latitude: ${'$'}{it.latitude}" // Escapado para a string da subtarefa
                        textLongitude.text = "Longitude: ${'$'}{it.longitude}" // Escapado para a string da subtarefa
                        viewModel.registrarPonto(it.latitude, it.longitude)
                        Toast.makeText(this, "Ponto registrado", Toast.LENGTH_SHORT).show()
                    } ?: Toast.makeText(this, "Localização não disponível", Toast.LENGTH_SHORT).show()
                } finally {
                    progressBar.visibility = View.GONE
                    button.isEnabled = true
                }
            }.addOnFailureListener {
                // Adicionado para garantir que o botão e o progress bar sejam resetados em caso de falha ao obter localização
                Toast.makeText(this, "Falha ao obter localização", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
                button.isEnabled = true
            }
        }

        viewModel.todosRegistros().observe(this) { registros ->
            registroAdapter.updateList(registros ?: emptyList())
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) recreate()
        else Toast.makeText(this, "Permissão negada", Toast.LENGTH_SHORT).show()
    }
}
