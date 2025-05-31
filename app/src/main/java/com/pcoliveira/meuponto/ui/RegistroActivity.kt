package com.pcoliveira.meuponto.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.pcoliveira.meuponto.R
import com.pcoliveira.meuponto.viewmodel.RegistroViewModel
import java.text.SimpleDateFormat
import java.util.*

class RegistroActivity : AppCompatActivity() {
    private lateinit var viewModel: RegistroViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var textHoraAtual: TextView
    private lateinit var textLatitude: TextView
    private lateinit var textLongitude: TextView
    private lateinit var listaRegistrosLayout: LinearLayout
    private val horaFormatada = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        viewModel = ViewModelProvider(this)[RegistroViewModel::class.java]
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val button = findViewById<Button>(R.id.button_registrar)
        textHoraAtual = findViewById(R.id.text_hora_atual)
        textLatitude = findViewById(R.id.text_latitude)
        textLongitude = findViewById(R.id.text_longitude)
        listaRegistrosLayout = findViewById(R.id.lista_registros_layout) // ← XML precisa ter esse ID

        button.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                return@setOnClickListener
            }

            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val hora = horaFormatada.format(Date())
                    textHoraAtual.text = "Hora atual: $hora"
                    textLatitude.text = "Latitude: ${it.latitude}"
                    textLongitude.text = "Longitude: ${it.longitude}"
                    viewModel.registrarPonto(it.latitude, it.longitude)
                    Toast.makeText(this, "Ponto registrado", Toast.LENGTH_SHORT).show()
                } ?: Toast.makeText(this, "Localização não disponível", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.todosRegistros().observe(this) { registros ->
            listaRegistrosLayout.removeAllViews()
            registros.forEach { registro ->
                val texto = TextView(this).apply {
                    text = "ID ${registro.id}: ${Date(registro.timestamp)}"
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
        if (isGranted) recreate()
        else Toast.makeText(this, "Permissão negada", Toast.LENGTH_SHORT).show()
    }
}
