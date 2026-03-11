package com.vitaltracker.app.ui.input

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.vitaltracker.app.R
import com.vitaltracker.app.data.db.VitalEntry
import com.vitaltracker.app.databinding.FragmentInputBinding
import com.vitaltracker.app.util.NormalValues
import com.vitaltracker.app.viewmodel.VitalViewModel
import java.text.SimpleDateFormat
import java.util.*

class InputFragment : Fragment() {

    private var _binding: FragmentInputBinding? = null
    private val binding get() = _binding!!
    private val viewModel: VitalViewModel by activityViewModels()

    private var selectedDate: Calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("EEEE, dd. MMMM yyyy", Locale.GERMAN)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInputBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateDateButton()
        updateBmiDisplay()

        binding.btnPickDate.setOnClickListener { showDatePicker() }

        binding.btnSave.setOnClickListener { saveEntry() }

        // Live BMI update when weight changes
        binding.etWeight.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) updateBmiDisplay()
        }
    }

    private fun showDatePicker() {
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                selectedDate.set(year, month, day)
                updateDateButton()
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.maxDate = System.currentTimeMillis()
        }.show()
    }

    private fun updateDateButton() {
        binding.btnPickDate.text = dateFormat.format(selectedDate.time)
        binding.tvDate.text = getString(R.string.label_messung_vom, dateFormat.format(selectedDate.time))
    }

    private fun updateBmiDisplay() {
        val weightStr = binding.etWeight.text?.toString()
        val weight = weightStr?.toFloatOrNull() ?: return
        val height = viewModel.userHeightCm
        if (height > 0) {
            val bmi = NormalValues.bmi(weight, height)
            val category = NormalValues.bmiCategory(bmi)
            binding.tvBmi.text = getString(R.string.label_bmi_value, bmi, category)
            binding.tvBmi.visibility = View.VISIBLE
        }
    }

    private fun saveEntry() {
        val weightStr = binding.etWeight.text?.toString()
        val systolicStr = binding.etSystolic.text?.toString()
        val diastolicStr = binding.etDiastolic.text?.toString()
        val pulseStr = binding.etPulse.text?.toString()
        val oxygenStr = binding.etOxygen.text?.toString()

        // Validation
        if (weightStr.isNullOrBlank() || systolicStr.isNullOrBlank() ||
            diastolicStr.isNullOrBlank() || pulseStr.isNullOrBlank() || oxygenStr.isNullOrBlank()
        ) {
            Toast.makeText(requireContext(), getString(R.string.error_pflichtfelder), Toast.LENGTH_SHORT).show()
            return
        }

        val weight = weightStr.toFloatOrNull()
        val systolic = systolicStr.toIntOrNull()
        val diastolic = diastolicStr.toIntOrNull()
        val pulse = pulseStr.toIntOrNull()
        val oxygen = oxygenStr.toFloatOrNull()

        if (weight == null || systolic == null || diastolic == null || pulse == null || oxygen == null) {
            Toast.makeText(requireContext(), getString(R.string.error_ungueltige_werte), Toast.LENGTH_SHORT).show()
            return
        }

        // Range checks
        if (weight < 20f || weight > 300f) {
            Toast.makeText(requireContext(), getString(R.string.error_gewicht_bereich), Toast.LENGTH_SHORT).show()
            return
        }
        if (systolic < 60 || systolic > 250) {
            Toast.makeText(requireContext(), getString(R.string.error_blutdruck_bereich), Toast.LENGTH_SHORT).show()
            return
        }
        if (diastolic < 40 || diastolic > 150) {
            Toast.makeText(requireContext(), getString(R.string.error_blutdruck_bereich), Toast.LENGTH_SHORT).show()
            return
        }
        if (pulse < 20 || pulse > 250) {
            Toast.makeText(requireContext(), getString(R.string.error_puls_bereich), Toast.LENGTH_SHORT).show()
            return
        }
        if (oxygen < 50f || oxygen > 100f) {
            Toast.makeText(requireContext(), getString(R.string.error_sauerstoff_bereich), Toast.LENGTH_SHORT).show()
            return
        }

        val entry = VitalEntry(
            date = selectedDate.timeInMillis,
            weightKg = weight,
            bloodPressureSystolic = systolic,
            bloodPressureDiastolic = diastolic,
            pulseBeatsPerMinute = pulse,
            oxygenSaturationPercent = oxygen,
            notes = binding.etNotes.text?.toString() ?: ""
        )

        viewModel.insert(entry)
        Toast.makeText(requireContext(), getString(R.string.msg_gespeichert), Toast.LENGTH_SHORT).show()
        clearFields()
    }

    private fun clearFields() {
        binding.etWeight.text?.clear()
        binding.etSystolic.text?.clear()
        binding.etDiastolic.text?.clear()
        binding.etPulse.text?.clear()
        binding.etOxygen.text?.clear()
        binding.etNotes.text?.clear()
        binding.tvBmi.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
