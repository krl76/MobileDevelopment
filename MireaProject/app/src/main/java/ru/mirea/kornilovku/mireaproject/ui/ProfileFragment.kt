package ru.mirea.kornilovku.mireaproject.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import ru.mirea.kornilovku.mireaproject.R

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var editTextFullName: EditText
    private lateinit var editTextGroup: EditText
    private lateinit var editTextNumber: EditText
    private lateinit var editTextFavoriteFilm: EditText
    private lateinit var buttonSaveProfile: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editTextFullName = view.findViewById(R.id.editTextFullName)
        editTextGroup = view.findViewById(R.id.editTextGroup)
        editTextNumber = view.findViewById(R.id.editTextNumber)
        editTextFavoriteFilm = view.findViewById(R.id.editTextFavoriteFilm)
        buttonSaveProfile = view.findViewById(R.id.buttonSaveProfile)

        loadProfile()

        buttonSaveProfile.setOnClickListener {
            saveProfile()
        }
    }

    private fun saveProfile() {
        val preferences = requireActivity().getSharedPreferences(
            "profile_settings",
            Context.MODE_PRIVATE
        )

        preferences.edit()
            .putString("FULL_NAME", editTextFullName.text.toString())
            .putString("GROUP", editTextGroup.text.toString())
            .putString("NUMBER", editTextNumber.text.toString())
            .putString("FAVORITE_FILM", editTextFavoriteFilm.text.toString())
            .apply()

        Toast.makeText(requireContext(), "Профиль сохранён", Toast.LENGTH_SHORT).show()
    }

    private fun loadProfile() {
        val preferences = requireActivity().getSharedPreferences(
            "profile_settings",
            Context.MODE_PRIVATE
        )

        editTextFullName.setText(preferences.getString("FULL_NAME", ""))
        editTextGroup.setText(preferences.getString("GROUP", ""))
        editTextNumber.setText(preferences.getString("NUMBER", ""))
        editTextFavoriteFilm.setText(preferences.getString("FAVORITE_FILM", ""))
    }
}