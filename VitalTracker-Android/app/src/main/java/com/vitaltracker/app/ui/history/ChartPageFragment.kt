package com.vitaltracker.app.ui.history

import android.graphics.Color
import android.os.Bundle
import android.view.*
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

enum class VitalType { WEIGHT, SYSTOLIC, DIASTOLIC, PULSE, OXYGEN }

class ChartPageFragment : Fragment() {
    companion object {
        fun newInstance(t: VitalType) = ChartPageFragment().apply {
            arguments = Bundle().also { it.putSerializable("type", t) }
        }
    }

    private var _b: FragmentChartPageBinding? = null
    private val b get() = _b!!
    private val vm: VitalViewModel by activityViewModels()
    private val type: VitalType by lazy {
        @Suppress("DEPRECATION")
        arguments?.getSerializable("type") as VitalType
    }
    private val dateFmt = SimpleDateFormat("dd.MM", Locale.GERMAN)

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?) =
        FragmentChartPageBinding.inflate(i, c, false).also { _b = it }.root

    override fun onViewCreated(v: View, s: Bundle?) {
        setupChart()
        setupHeader()
        vm.allAsc.observe(viewLifecycleOwner) { entries ->
            if (entries.isEmpty()) {
                b.noData.visibility = View.VISIBLE
                b.lineChart.visibility = View.GONE
            } else {
                b.noData.visibility = View.GONE
                b.lineChart.visibility = View.VISIBLE
                renderChart(entries)
                updateStat(entries.last())
            }
        }
    }

    private fun setupHeader() {
        val (title, colorRes, iconRes) = when (type) {
            VitalType.WEIGHT    -> Triple(getString(R.string.label_gewicht),    R.color.weight_color, R.drawable.ic_weight)
            VitalType.SYSTOLIC  -> Triple(getString(R.string.label_sys),        R.color.bp_color,     R.drawable.ic_heart)
            VitalType.DIASTOLIC -> Triple(getString(R.string.label_dia),        R.color.bp_color,     R.drawable.ic_heart)
            VitalType.PULSE     -> Triple(getString(R.string.label_puls),       R.color.pulse_color,  R.drawable.ic_pulse)
            VitalType.OXYGEN    -> Triple(getString(R.string.label_sauerstoff), R.color.o2_color,     R.drawable.ic_oxygen)
        }
        b.tvTitle.text = title
        b.ivIcon.setImageResource(iconRes)
        b.ivIcon.setColorFilter(ContextCompat.getColor(requireContext(), colorRes))
        val (lo, hi) = getLimits()
        b.tvNormalRange.text = getString(R.string.normal_range, lo, hi)
    }

    private fun setupChart() {
        b.lineChart.apply {
            description.isEnabled = false
            legend.isEnabled = false
            setTouchEnabled(true); isDragEnabled = true; setScaleEnabled(true)
            setPinchZoom(true); setDrawGridBackground(false)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.labelRotationAngle = -30f
            xAxis.textSize = 10f
            axisLeft.textSize = 10f
            axisRight.isEnabled = false
            animateX(600)
        }
    }

    private fun renderChart(entries: List<VitalEntry>) {
        val pts = entries.mapIndexed { i, e -> Entry(i.toFloat(), getValue(e)) }
        val (lo, hi) = getLimits()
        val colorRes = when (type) {
            VitalType.WEIGHT    -> R.color.weight_color
            VitalType.SYSTOLIC, VitalType.DIASTOLIC -> R.color.bp_color
            VitalType.PULSE     -> R.color.pulse_color
            VitalType.OXYGEN    -> R.color.o2_color
        }
        val color = ContextCompat.getColor(requireContext(), colorRes)
        val ds = LineDataSet(pts, "").apply {
            this.color = color; lineWidth = 2.5f
            circleRadius = 5f; setCircleColor(color); circleHoleRadius = 2.5f
            setDrawValues(true); valueTextSize = 9f
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawFilled(true); fillAlpha = 30; fillColor = color
        }
        val labels = entries.map { dateFmt.format(Date(it.date)) }
        b.lineChart.xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(v: Float) = labels.getOrElse(v.toInt()) { "" }
        }
        b.lineChart.axisLeft.removeAllLimitLines()
        val red = ContextCompat.getColor(requireContext(), R.color.limit_color)
        fun addLimit(value: Float, lbl: String) = b.lineChart.axisLeft.addLimitLine(
            LimitLine(value, lbl).apply {
                lineWidth = 2f; lineColor = red; textColor = red; textSize = 10f
                enableDashedLine(15f, 8f, 0f)
                labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP
            }
        )
        addLimit(hi, getString(R.string.limit_max))
        addLimit(lo, getString(R.string.limit_min))
        b.lineChart.data = LineData(ds)
        b.lineChart.invalidate()
    }

    private fun updateStat(entry: VitalEntry) {
        val v = getValue(entry)
        val (lo, hi) = getLimits()
        val unit = when (type) {
            VitalType.WEIGHT -> "kg"; VitalType.OXYGEN -> "%"; else -> if (type == VitalType.PULSE) "bpm" else "mmHg"
        }
        b.tvLatest.text = if (type == VitalType.WEIGHT || type == VitalType.OXYGEN) "%.1f %s".format(v, unit) else "${v.toInt()} $unit"
        val (txt, bg) = when {
            v < lo  -> getString(R.string.status_low)  to R.color.status_low
            v > hi  -> getString(R.string.status_high) to R.color.status_high
            else    -> getString(R.string.status_ok)   to R.color.status_ok
        }
        b.tvStatus.text = txt
        b.tvStatus.setBackgroundColor(ContextCompat.getColor(requireContext(), bg))
    }

    private fun getValue(e: VitalEntry) = when (type) {
        VitalType.WEIGHT    -> e.weightKg
        VitalType.SYSTOLIC  -> e.bloodPressureSystolic.toFloat()
        VitalType.DIASTOLIC -> e.bloodPressureDiastolic.toFloat()
        VitalType.PULSE     -> e.pulseBeatsPerMinute.toFloat()
        VitalType.OXYGEN    -> e.oxygenSaturationPercent
    }

    private fun getLimits(): Pair<Float, Float> {
        val age = vm.userAge; val h = vm.userHeightCm; val ath = vm.isAthlete
        return when (type) {
            VitalType.WEIGHT    -> NormalValues.weight(h)
            VitalType.SYSTOLIC  -> NormalValues.bloodPressure(age).let { it.sysMin.toFloat() to it.sysMax.toFloat() }
            VitalType.DIASTOLIC -> NormalValues.bloodPressure(age).let { it.diaMin.toFloat() to it.diaMax.toFloat() }
            VitalType.PULSE     -> NormalValues.pulse(ath).let { it.first.toFloat() to it.second.toFloat() }
            VitalType.OXYGEN    -> NormalValues.oxygen()
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
