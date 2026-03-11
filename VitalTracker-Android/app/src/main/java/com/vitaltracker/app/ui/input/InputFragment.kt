package com.vitaltracker.app.ui.input

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.*
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
    private var _b: FragmentInputBinding? = null
    private val b get() = _b!!
    private val vm: VitalViewModel by activityViewModels()
    private var selectedDate = Calendar.getInstance()
    private val fmt = SimpleDateFormat("EE, dd. MMM yyyy", Locale.GERMAN)

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?) =
        FragmentInputBinding.inflate(i, c, false).also { _b = it }.root

    override fun onViewCreated(v: View, s: Bundle?) {
        updateDateBtn()
        b.btnDate.setOnClickListener { pickDate() }
        b.btnSave.setOnClickListener { save() }
        b.etWeight.setOnFocusChangeListener { _, f -> if (!f) updateBmi() }
    }

    private fun pickDate() {
        DatePickerDialog(requireContext(), { _, y, m, d ->
            selectedDate.set(y, m, d); updateDateBtn()
        }, selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        ).apply { datePicker.maxDate = System.currentTimeMillis() }.show()
    }

    private fun updateDateBtn() { b.btnDate.text = fmt.format(selectedDate.time) }

    private fun updateBmi() {
        val w = b.etWeight.text?.toString()?.toFloatOrNull() ?: return
        val h = vm.userHeightCm
        val bmi = NormalValues.bmi(w, h)
        b.tvBmi.text = getString(R.string.bmi_display, bmi, NormalValues.bmiCategory(bmi))
        b.tvBmi.visibility = View.VISIBLE
    }

    private fun save() {
        val w   = b.etWeight.text?.toString()?.toFloatOrNull()
        val sys = b.etSys.text?.toString()?.toIntOrNull()
        val dia = b.etDia.text?.toString()?.toIntOrNull()
        val p   = b.etPulse.text?.toString()?.toIntOrNull()
        val o2  = b.etO2.text?.toString()?.toFloatOrNull()
        if (w == null || sys == null || dia == null || p == null || o2 == null) {
            toast(getString(R.string.err_required)); return
        }
        if (w < 20 || w > 300)   { toast(getString(R.string.err_weight));  return }
        if (sys < 60||sys > 250) { toast(getString(R.string.err_bp));      return }
        if (dia < 40||dia > 150) { toast(getString(R.string.err_bp));      return }
        if (p < 20 || p > 250)   { toast(getString(R.string.err_pulse));   return }
        if (o2 < 50 || o2 > 100) { toast(getString(R.string.err_oxygen));  return }

        vm.insert(VitalEntry(
            date = selectedDate.timeInMillis,
            weightKg = w, bloodPressureSystolic = sys,
            bloodPressureDiastolic = dia, pulseBeatsPerMinute = p,
            oxygenSaturationPercent = o2,
            notes = b.etNotes.text?.toString() ?: ""
        ))
        toast(getString(R.string.saved))
        listOf(b.etWeight, b.etSys, b.etDia, b.etPulse, b.etO2, b.etNotes).forEach { it.text?.clear() }
        b.tvBmi.visibility = View.GONE
    }

    private fun toast(msg: String) = Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
