package com.vitaltracker.app.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.vitaltracker.app.R
import com.vitaltracker.app.databinding.FragmentSettingsBinding
import com.vitaltracker.app.util.NormalValues
import com.vitaltracker.app.viewmodel.VitalViewModel

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: VitalViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadSettings()

        binding.toggleGender.addOnButtonCheckedListener { _, _, _ ->
            updateNormalValuesDisplay()
        }
        binding.etAge.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) updateNormalValuesDisplay()
        }
        binding.etHeight.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) updateNormalValuesDisplay()
        }
        binding.switchAthlete.setOnCheckedChangeListener { _, _ ->
            updateNormalValuesDisplay()
        }

        binding.btnSaveSettings.setOnClickListener { saveSettings() }
        updateNormalValuesDisplay()
    }

    private fun loadSettings() {
        binding.etName.setText(viewModel.userName)
        binding.etAge.setText(viewModel.userAge.toString())
        binding.etHeight.setText(viewModel.userHeightCm.toInt().toString())
        binding.switchAthlete.isChecked = viewModel.isAthlete

        when (viewModel.userGender) {
            "m" -> binding.toggleGender.check(R.id.btnMale)
            "w" -> binding.toggleGender.check(R.id.btnFemale)
            "d" -> binding.toggleGender.check(R.id.btnDiverse)
            else -> binding.toggleGender.check(R.id.btnMale)
        }
    }

    private fun saveSettings() {
        val name = binding.etName.text?.toString() ?: ""
        val ageStr = binding.etAge.text?.toString()
        val heightStr = binding.etHeight.text?.toString()

        val age = ageStr?.toIntOrNull()
        val height = heightStr?.toFloatOrNull()

        if (age == null || age < 1 || age > 120) {
            Toast.makeText(requireContext(), getString(R.string.error_alter_ungueltig), Toast.LENGTH_SHORT).show()
            return
        }
        if (height == null || height < 50f || height > 250f) {
            Toast.makeText(requireContext(), getString(R.string.error_groesse_ungueltig), Toast.LENGTH_SHORT).show()
            return
        }

        val gender = when (binding.toggleGender.checkedButtonId) {
            R.id.btnMale   -> "m"
            R.id.btnFemale -> "w"
            R.id.btnDiverse -> "d"
            else -> "m"
        }

        viewModel.userName = name
        viewModel.userAge = age
        viewModel.userHeightCm = height
        viewModel.userGender = gender
        viewModel.isAthlete = binding.switchAthlete.isChecked

        Toast.makeText(requireContext(), getString(R.string.msg_einstellungen_gespeichert), Toast.LENGTH_SHORT).show()
        updateNormalValuesDisplay()
    }

    private fun updateNormalValuesDisplay() {
        val ageStr = binding.etAge.text?.toString()
        val heightStr = binding.etHeight.text?.toString()
        val age = ageStr?.toIntOrNull() ?: viewModel.userAge
        val height = heightStr?.toFloatOrNull() ?: viewModel.userHeightCm
        val athlete = binding.switchAthlete.isChecked

        val gender = when (binding.toggleGender.checkedButtonId) {
            R.id.btnMale    -> "m"
            R.id.btnFemale  -> "w"
            R.id.btnDiverse -> "d"
            else -> "m"
        }

        val bp = NormalValues.bloodPressureRange(age, gender)
        val (pulseMin, pulseMax) = NormalValues.pulseRange(age, athlete)
        val (o2Min, o2Max) = NormalValues.oxygenSaturationRange()
        val weightRange = NormalValues.weightRange(height)

        binding.tvNormalValues.text = buildString {
            appendLine(getString(R.string.normal_bp_sys,    bp.systolicMin,  bp.systolicMax))
            appendLine(getString(R.string.normal_bp_dia,    bp.diastolicMin, bp.diastolicMax))
            appendLine(getString(R.string.normal_pulse,     pulseMin,        pulseMax))
            appendLine(getString(R.string.normal_oxygen,    o2Min,           o2Max))
            append(getString(R.string.normal_weight_range,  weightRange.minKg, weightRange.maxKg))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
