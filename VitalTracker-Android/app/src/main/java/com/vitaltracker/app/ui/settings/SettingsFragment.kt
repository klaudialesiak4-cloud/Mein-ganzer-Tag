package com.vitaltracker.app.ui.settings

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.vitaltracker.app.R
import com.vitaltracker.app.databinding.FragmentSettingsBinding
import com.vitaltracker.app.util.NormalValues
import com.vitaltracker.app.viewmodel.VitalViewModel

class SettingsFragment : Fragment() {
    private var _b: FragmentSettingsBinding? = null
    private val b get() = _b!!
    private val vm: VitalViewModel by activityViewModels()
    private var gender = "m"

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?) =
        FragmentSettingsBinding.inflate(i, c, false).also { _b = it }.root

    override fun onViewCreated(v: View, s: Bundle?) {
        loadUi()
        b.toggleGender.addOnButtonCheckedListener { _, _, _ -> refreshNormals() }
        b.etAge.setOnFocusChangeListener    { _, f -> if (!f) refreshNormals() }
        b.etHeight.setOnFocusChangeListener { _, f -> if (!f) refreshNormals() }
        b.switchAthlete.setOnCheckedChangeListener { _, _ -> refreshNormals() }
        b.btnSave.setOnClickListener { saveSettings() }
    }

    private fun loadUi() {
        b.etName.setText(vm.userName)
        b.etAge.setText(vm.userAge.toString())
        b.etHeight.setText(vm.userHeightCm.toInt().toString())
        b.switchAthlete.isChecked = vm.isAthlete
        gender = vm.userGender
        when (gender) {
            "m" -> b.toggleGender.check(R.id.btnMale)
            "w" -> b.toggleGender.check(R.id.btnFemale)
            else -> b.toggleGender.check(R.id.btnDiverse)
        }
        refreshNormals()
    }

    private fun refreshNormals() {
        val age    = b.etAge.text?.toString()?.toIntOrNull()    ?: vm.userAge
        val height = b.etHeight.text?.toString()?.toFloatOrNull() ?: vm.userHeightCm
        val ath    = b.switchAthlete.isChecked
        val bp     = NormalValues.bloodPressure(age)
        val (pL,pH) = NormalValues.pulse(ath)
        val (wL,wH) = NormalValues.weight(height)
        val (o2L,o2H) = NormalValues.oxygen()
        b.tvNormals.text = buildString {
            appendLine(getString(R.string.norm_bp_sys,  bp.sysMin, bp.sysMax))
            appendLine(getString(R.string.norm_bp_dia,  bp.diaMin, bp.diaMax))
            appendLine(getString(R.string.norm_pulse,   pL, pH))
            appendLine(getString(R.string.norm_oxygen,  o2L, o2H))
            append(getString(R.string.norm_weight, wL, wH))
        }
    }

    private fun saveSettings() {
        val age    = b.etAge.text?.toString()?.toIntOrNull()
        val height = b.etHeight.text?.toString()?.toFloatOrNull()
        if (age == null || age < 1 || age > 120)      { toast(getString(R.string.err_age));    return }
        if (height == null || height < 50||height>250) { toast(getString(R.string.err_height)); return }
        gender = when (b.toggleGender.checkedButtonId) {
            R.id.btnMale -> "m"; R.id.btnFemale -> "w"; else -> "d"
        }
        vm.userName     = b.etName.text?.toString()?.trim() ?: ""
        vm.userAge      = age
        vm.userHeightCm = height
        vm.userGender   = gender
        vm.isAthlete    = b.switchAthlete.isChecked
        toast(getString(R.string.settings_saved))
        refreshNormals()
    }

    private fun toast(msg: String) = Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
