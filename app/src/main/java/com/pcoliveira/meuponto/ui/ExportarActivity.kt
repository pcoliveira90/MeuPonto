package com.pcoliveira.meuponto.ui

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.smartponto.R
import com.example.smartponto.data.RegistroDatabase
import com.example.smartponto.viewmodel.RegistroViewModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ExportarActivity : AppCompatActivity() {
    private lateinit var viewModel: RegistroViewModel
    private lateinit var spinnerTipo: Spinner
    private lateinit var textView: TextView
    private lateinit var buttonPdf: Button
    private lateinit var buttonCsv: Button

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exportar)

        viewModel = ViewModelProvider(this)[RegistroViewModel::class.java]

        spinnerTipo = findViewById(R.id.spinner_tipo)
        textView = findViewById(R.id.text_registros)
        buttonPdf = findViewById(R.id.button_exportar)
        buttonCsv = findViewById(R.id.button_csv)

        val tipos = listOf("Relatório Completo", "Relatório Simples")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tipos)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipo.adapter = adapter

        val dao = RegistroDatabase.getDatabase(this)
        dao.registroDao().listar().observe(this) { registros ->
            dao.ajustePontoDao().listarTodos().observe(this) { ajustes ->

                val texto = StringBuilder()

                registros.forEach { registro ->
                    val ajustesDoRegistro = ajustes.filter { it.registroId == registro.id }

                    val baseText = "• ${dateFormat.format(Date(registro.timestamp))}"
                    val linha = if (ajustesDoRegistro.isNotEmpty()) "$baseText *" else baseText
                    texto.appendLine(linha)

                    if (spinnerTipo.selectedItem == "Relatório Simples") {
                        ajustesDoRegistro.forEach {
                            val linhaAjuste = "   → Solicitação: ${dateFormat.format(Date(it.timestamp))} - Motivo: ${it.motivo}"
                            texto.appendLine(linhaAjuste)
                        }
                    } else if (spinnerTipo.selectedItem == "Relatório Completo") {
                        texto.appendLine("   Lat: ${registro.latitude}, Long: ${registro.longitude}")
                    }

                    texto.appendLine()
                }

                val relatorioTexto = texto.toString()
                textView.text = relatorioTexto

                buttonPdf.setOnClickListener {
                    gerarPdfProfissional(registros, ajustes)
                }

                buttonCsv.setOnClickListener {
                    gerarCsv(registros, ajustes)
                }
            }
        }
    }

    private fun gerarCsv(registros: List<com.example.smartponto.data.Registro>, ajustes: List<com.example.smartponto.data.AjustePonto>) {
        val builder = StringBuilder()
        builder.append("id,data_hora,latitude,longitude,ajuste\n")
        registros.forEach { reg ->
            val temAjuste = ajustes.any { it.registroId == reg.id }
            builder.append("${reg.id},${dateFormat.format(Date(reg.timestamp))},${reg.latitude},${reg.longitude},${temAjuste}\n")
        }
        val file = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "relatorio_pontos.csv")
        file.writeText(builder.toString())
    }

    private fun gerarPdfProfissional(registros: List<com.example.smartponto.data.Registro>, ajustes: List<com.example.smartponto.data.AjustePonto>) {
        val document = PdfDocument()
        val paint = Paint().apply {
            color = Color.BLACK
            textSize = 12f
        }

        val titlePaint = Paint().apply {
            color = Color.BLUE
            textSize = 16f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        canvas.drawText("SmartPonto - Relatório de Pontos", 40f, 50f, titlePaint)
        canvas.drawText("Gerado em: ${dateFormat.format(Date())}", 40f, 70f, paint)

        var y = 100f
        registros.forEach { reg ->
            val hasAjuste = ajustes.any { it.registroId == reg.id }
            val text = "• ${dateFormat.format(Date(reg.timestamp))}${if (hasAjuste) " *" else ""}"
            canvas.drawText(text, 40f, y, paint)
            y += 20f

            if (spinnerTipo.selectedItem == "Relatório Completo") {
                canvas.drawText("   Lat: ${reg.latitude}, Long: ${reg.longitude}", 60f, y, paint)
                y += 20f
            }

            if (spinnerTipo.selectedItem == "Relatório Simples") {
                ajustes.filter { it.registroId == reg.id }.forEach {
                    val ajusteText = "   → ${dateFormat.format(Date(it.timestamp))} - ${it.motivo}"
                    canvas.drawText(ajusteText, 60f, y, paint)
                    y += 20f
                }
            }

            y += 10f
        }

        document.finishPage(page)

        val file = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "relatorio_pontos.pdf")
        document.writeTo(FileOutputStream(file))
        document.close()
    }
}
