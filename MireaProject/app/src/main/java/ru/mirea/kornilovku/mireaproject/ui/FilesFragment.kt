package ru.mirea.kornilovku.mireaproject.ui

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ru.mirea.kornilovku.mireaproject.R
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.Base64

class FilesFragment : Fragment(R.layout.fragment_files) {

    private lateinit var textViewNotes: TextView
    private lateinit var fabAddNote: FloatingActionButton
    private lateinit var buttonClearNotes: Button

    private val fileName = "encrypted_notes.txt"
    private val cipherKey = "kku13bsbo0923"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textViewNotes = view.findViewById(R.id.textViewNotes)
        fabAddNote = view.findViewById(R.id.fabAddNote)
        buttonClearNotes = view.findViewById(R.id.buttonClearNotes)

        loadNotesFromFile()

        fabAddNote.setOnClickListener {
            showAddNoteDialog()
        }

        buttonClearNotes.setOnClickListener {
            showClearConfirmation()
        }
    }

    private fun showAddNoteDialog() {
        val editText = EditText(requireContext())
        editText.hint = "Введите новую заметку"

        AlertDialog.Builder(requireContext())
            .setTitle("Новая запись")
            .setView(editText)
            .setPositiveButton("Сохранить") { _, _ ->
                val noteText = editText.text.toString().trim()

                if (noteText.isNotEmpty()) {
                    saveNoteToFile(noteText)
                    loadNotesFromFile()
                } else {
                    Toast.makeText(requireContext(), "Пустую запись сохранять нельзя", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun saveNoteToFile(note: String) {
        try {
            val encryptedNote = xorCipher(note, cipherKey)
            val base64Note = Base64.getEncoder().encodeToString(encryptedNote.toByteArray())
            val outputStream: FileOutputStream =
                requireActivity().openFileOutput(fileName, Context.MODE_APPEND)

            outputStream.write((base64Note + "\n").toByteArray())
            outputStream.close()

            Toast.makeText(requireContext(), "Запись сохранена (зашифрована)", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Ошибка записи файла", Toast.LENGTH_SHORT).show()
        }
    }

    private fun xorCipher(input: String, key: String): String {
        return input.indices.map { i ->
            (input[i].code xor key[i % key.length].code).toChar()
        }.joinToString("")
    }

    private fun loadNotesFromFile() {
        var fileInputStream: FileInputStream? = null

        try {
            fileInputStream = requireActivity().openFileInput(fileName)
            val bytes = ByteArray(fileInputStream.available())
            fileInputStream.read(bytes)
            val fileContent = String(bytes)

            val decryptedLines = fileContent
                .lines()
                .filter { it.isNotBlank() }
                .map { base64Line ->
                    try {
                        val decodedBytes = Base64.getDecoder().decode(base64Line)
                        xorCipher(String(decodedBytes), cipherKey)
                    } catch (e: IllegalArgumentException) {
                        "Ошибка: файл повреждён, очистите записи"
                    }
                }

            textViewNotes.text = if (decryptedLines.isEmpty()) {
                "Пока нет сохранённых записей"
            } else {
                decryptedLines.joinToString("\n")
            }
        } catch (e: IOException) {
            textViewNotes.text = "Пока нет сохранённых записей"
        } finally {
            try {
                fileInputStream?.close()
            } catch (_: IOException) {
            }
        }
    }

    private fun showClearConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle("Очистить записи")
            .setMessage("Все сохранённые записи будут удалены. Продолжить?")
            .setPositiveButton("Удалить") { _, _ ->
                requireActivity().deleteFile(fileName)
                textViewNotes.text = "Пока нет сохранённых записей"
                Toast.makeText(requireContext(), "Записи удалены", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        requireActivity().findViewById<FloatingActionButton>(R.id.fab)?.hide()
    }

    override fun onPause() {
        super.onPause()
        requireActivity().findViewById<FloatingActionButton>(R.id.fab)?.show()
    }
}
