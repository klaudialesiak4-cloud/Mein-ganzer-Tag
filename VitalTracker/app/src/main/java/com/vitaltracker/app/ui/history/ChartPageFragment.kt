package com.vitaltracker.app.ui.history

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.vitaltracker.app.R
import com.vitaltracker.app.data.db.VitalEntry
import com.vitaltracker.app.databinding.FragmentChartPageBinding
import com.vitaltracker.app.util.NormalValues
import com.vitaltracker.app.viewmodel.VitalViewModel
import java.text.SimpleDateFormat
import java.util.*

enum class VitalType {
    WEIGHT, SYSTOLIC, DIASTOLIC, PULSE, OXYGEN
}

class ChartPageFragment : Fragment() {

    companion object {
        private const val ARG_TYPE = "vital_type"

        fun newInstance(type: VitalType): ChartPageFragment {
            return ChartPageFragment().apply {
                arguments = Bundle().putSerializable(ARG_TYPE, type).let { Bundle().also { b ->
                    b.putSerializable(ARG_TYPE, type)
                }}
            }
        }
    }

    private var _binding: FragmentChartPageBinding? = null
    private val binding get() = _binding!!
    private val viewModel: VitalViewModel by activityViewModels()

    private val vitalType: VitalType by lazy {
        @Suppress("DEPRECATION")
        arguments?.getSerializable(ARG_TYPE) as? VitalType ?: VitalType.SYSTOLIC
    }

    private val dateFormat = SimpleDateFormat("dd.MM", Locale.GERMAN)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChartPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupChartAppearance()

        viewModel.allEntriesAsc.observe(viewLifecycleOwner) { entries ->
            if (entries.isEmpty()) {
                binding.tvNoData.visibility = View.VISIBLE
                binding.lineChart.visibility = View.GONE
            } else {
                binding.tvNoData.visibility = View.GONE
                binding.lineChart.visibility = View.VISIBLE
                updateChart(entries)
                updateLatestValue(entries.last())
            }
        }

        setupHeader()
    }

    private fun setupHeader() {
        val (title, icon, colorRes, unit) = when (vitalType) {
            VitalType.WEIGHT    -> Quadruple(getString(R.string.label_gewicht),     R.drawable.ic_weight,          R.color.color_weight,          "kg")
            VitalType.SYSTOLIC  -> Quadruple(getString(R.string.label_systolisch),  R.drawable.ic_blood_pressure,  R.color.color_blood_pressure,  "mmHg")
            VitalType.DIASTOLIC -> Quadruple(getString(R.string.label_diastolisch), R.drawable.ic_blood_pressure,  R.color.color_blood_pressure,  "mmHg")
            VitalType.PULSE     -> Quadruple(getString(R.string.label_puls),        R.drawable.ic_pulse,           R.color.color_pulse,           "bpm")
            VitalType.OXYGEN    -> Quadruple(getString(R.string.label_sauerstoff),  R.drawable.ic_oxygen,          R.color.color_oxygen,          "%")
        }
        binding.tvVitalTitle.text = title
        binding.ivVitalIcon.setImageResource(icon)
        binding.ivVitalIcon.setColorFilter(ContextCompat.getColor(requireContext(), colorRes))
        binding.tvChartTitle.text = getString(R.string.chart_verlauf_title, title, unit)

        val normalText = getNormalRangeText()
        binding.tvNormalRange.text = normalText
    }

    private fun getNormalRangeText(): String {
        val age = viewModel.userAge
        val gender = viewModel.userGender
        val height = viewModel.userHeightCm
        val athlete = viewModel.isAthlete
        return when (vitalType) {
            VitalType.WEIGHT -> {
                val range = NormalValues.weightRange(height)
                getString(R.string.normal_range_weight, range.minKg, range.maxKg)
            }
            VitalType.SYSTOLIC -> {
                val bp = NormalValues.bloodPressureRange(age, gender)
                getString(R.string.normal_range_generic, bp.systolicMin, bp.systolicMax)
            }
            VitalType.DIASTOLIC -> {
                val bp = NormalValues.bloodPressureRange(age, gender)
                getString(R.string.normal_range_generic, bp.diastolicMin, bp.diastolicMax)
            }
            VitalType.PULSE -> {
                val (lo, hi) = NormalValues.pulseRange(age, athlete)
                getString(R.string.normal_range_generic, lo, hi)
            }
            VitalType.OXYGEN -> {
                val (lo, hi) = NormalValues.oxygenSaturationRange()
                getString(R.string.normal_range_oxygen, lo, hi)
            }
        }
    }

    private fun setupChartAppearance() {
        binding.lineChart.apply {
            description.isEnabled = false
            legend.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)
            setDrawGridBackground(false)
            setBackgroundColor(Color.WHITE)
            animateX(800)

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(true)
                granularity = 1f
                labelRotationAngle = -30f
                textSize = 10f
            }
            axisLeft.apply {
                setDrawGridLines(true)
                textSize = 10f
            }
            axisRight.isEnabled = false
        }
    }

    private fun updateChart(entries: List<VitalEntry>) {
        val dataPoints = entries.mapIndexed { index, entry ->
            val value = getValueForType(entry)
            Entry(index.toFloat(), value)
        }

        val colorRes = when (vitalType) {
            VitalType.WEIGHT    -> R.color.color_weight
            VitalType.SYSTOLIC, VitalType.DIASTOLIC -> R.color.color_blood_pressure
            VitalType.PULSE     -> R.color.color_pulse
            VitalType.OXYGEN    -> R.color.color_oxygen
        }
        val lineColor = ContextCompat.getColor(requireContext(), colorRes)

        val dataSet = LineDataSet(dataPoints, "").apply {
            color = lineColor
            valueTextColor = Color.DKGRAY
            lineWidth = 2.5f
            circleRadius = 5f
            setCircleColor(lineColor)
            setDrawCircleHole(true)
            circleHoleRadius = 2.5f
            setDrawValues(true)
            valueTextSize = 9f
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawFilled(true)
            fillAlpha = 30
            fillColor = lineColor
        }

        // X-Achsen Labels (Datum)
        val labels = entries.map { dateFormat.format(Date(it.date)) }
        binding.lineChart.xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val idx = value.toInt()
                return if (idx in labels.indices) labels[idx] else ""
            }
        }

        // Normalwert-Linien (rot)
        binding.lineChart.axisLeft.removeAllLimitLines()
        val (lowerLimit, upperLimit) = getNormalLimits()
        val normalColor = ContextCompat.getColor(requireContext(), R.color.color_normal_limit)

        addLimitLine(upperLimit, getString(R.string.limit_max), normalColor)
        addLimitLine(lowerLimit, getString(R.string.limit_min), normalColor)

        binding.lineChart.data = LineData(dataSet)
        binding.lineChart.invalidate()
    }

    private fun addLimitLine(value: Float, label: String, color: Int) {
        val ll = LimitLine(value, label).apply {
            lineWidth = 2f
            lineColor = color
            enableDashedLine(15f, 8f, 0f)
            textColor = color
            textSize = 10f
            labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP
        }
        binding.lineChart.axisLeft.addLimitLine(ll)
    }

    private fun getValueForType(entry: VitalEntry): Float = when (vitalType) {
        VitalType.WEIGHT    -> entry.weightKg
        VitalType.SYSTOLIC  -> entry.bloodPressureSystolic.toFloat()
        VitalType.DIASTOLIC -> entry.bloodPressureDiastolic.toFloat()
        VitalType.PULSE     -> entry.pulseBeatsPerMinute.toFloat()
        VitalType.OXYGEN    -> entry.oxygenSaturationPercent
    }

    private fun getNormalLimits(): Pair<Float, Float> {
        val age = viewModel.userAge
        val gender = viewModel.userGender
        val height = viewModel.userHeightCm
        val athlete = viewModel.isAthlete
        return when (vitalType) {
            VitalType.WEIGHT -> {
                val r = NormalValues.weightRange(height)
                Pair(r.minKg, r.maxKg)
            }
            VitalType.SYSTOLIC -> {
                val bp = NormalValues.bloodPressureRange(age, gender)
                Pair(bp.systolicMin.toFloat(), bp.systolicMax.toFloat())
            }
            VitalType.DIASTOLIC -> {
                val bp = NormalValues.bloodPressureRange(age, gender)
                Pair(bp.diastolicMin.toFloat(), bp.diastolicMax.toFloat())
            }
            VitalType.PULSE -> {
                val (lo, hi) = NormalValues.pulseRange(age, athlete)
                Pair(lo.toFloat(), hi.toFloat())
            }
            VitalType.OXYGEN -> {
                val (lo, hi) = NormalValues.oxygenSaturationRange()
                Pair(lo, hi)
            }
        }
    }

    private fun updateLatestValue(entry: VitalEntry) {
        val value = getValueForType(entry)
        val (lo, hi) = getNormalLimits()

        val (displayValue, unit) = when (vitalType) {
            VitalType.WEIGHT    -> Pair("%.1f kg".format(value), "kg")
            VitalType.SYSTOLIC  -> Pair("${value.toInt()} mmHg", "mmHg")
            VitalType.DIASTOLIC -> Pair("${value.toInt()} mmHg", "mmHg")
            VitalType.PULSE     -> Pair("${value.toInt()} bpm", "bpm")
            VitalType.OXYGEN    -> Pair("%.1f %%".format(value), "%")
        }
        binding.tvLatestValue.text = displayValue

        val (statusText, statusColor) = when {
            value < lo  -> Pair(getString(R.string.status_zu_niedrig), R.color.color_status_low)
            value > hi  -> Pair(getString(R.string.status_zu_hoch),    R.color.color_status_high)
            else        -> Pair(getString(R.string.status_normal),     R.color.color_status_normal)
        }
        binding.tvStatus.text = statusText
        binding.tvStatus.setBackgroundColor(ContextCompat.getColor(requireContext(), statusColor))
        binding.tvStatus.setTextColor(Color.WHITE)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
}
