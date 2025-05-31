package com.pcoliveira.meuponto.ui

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.pcoliveira.meuponto.R
import com.pcoliveira.meuponto.data.AjustePonto
import com.pcoliveira.meuponto.data.Registro
import com.pcoliveira.meuponto.data.RegistroDatabase
import com.pcoliveira.meuponto.viewmodel.RegistroViewModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ExportarActivity : AppCompatActivity() {
    private lateinit var viewModel: RegistroViewModel
    private lateinit var spinnerTipo: Spinner
    private lateinit var textView: TextView
    private lateinit var buttonPdf: MaterialButton
    private lateinit var buttonCsv: MaterialButton

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exportar)

        viewModel = ViewModelProvider(this)[RegistroViewModel::class.java]

        spinnerTipo = Spinner(this)
        textView = TextView(this)
        buttonPdf = findViewById(R.id.btnExportPdf)
        buttonCsv = findViewById(R.id.btnExportCsv)

        val tipos = listOf("Relatório Completo", "Relatório Simples")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tipos)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipo.adapter = adapter

        val dao = RegistroDatabase.getDatabase(this)
        dao.registroDao().listar().observe(this) { registros ->
            dao.ajustePontoDao().listarTodos().observe(this) { ajustes ->

                buttonPdf.setOnClickListener {
                    gerarPdfProfissional(registros, ajustes)
                }

                buttonCsv.setOnClickListener {
                    gerarCsv(registros, ajustes)
                }
            }
        }
    }

    private fun gerarCsv(
        registros: List<Registro>,
        ajustes: List<AjustePonto>
    ) {
        val builder = StringBuilder()
        builder.append("id,data_hora,latitude,longitude,ajuste\n")
        registros.forEach { reg ->
            val temAjuste = ajustes.any { it.registroId == reg.id }
            builder.append("${reg.id},${dateFormat.format(Date(reg.timestamp))},${reg.latitude},${reg.longitude},${temAjuste}\n")
        }
        val file = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "relatorio_pontos.csv")
        file.writeText(builder.toString())
    }

    private fun gerarPdfProfissional(
        registros: List<Registro>,
        ajustes: List<AjustePonto>
    ) {
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
