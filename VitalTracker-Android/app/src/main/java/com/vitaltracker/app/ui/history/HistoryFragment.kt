package com.vitaltracker.app.ui.history

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.vitaltracker.app.databinding.FragmentHistoryBinding

class HistoryFragment : Fragment() {
    private var _b: FragmentHistoryBinding? = null
    private val b get() = _b!!

    private val tabs = listOf(
        "Blutdruck sys." to VitalType.SYSTOLIC,
        "Blutdruck dia." to VitalType.DIASTOLIC,
        "Puls"           to VitalType.PULSE,
        "Sauerstoff"     to VitalType.OXYGEN,
        "Gewicht"        to VitalType.WEIGHT
    )

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?) =
        FragmentHistoryBinding.inflate(i, c, false).also { _b = it }.root

    override fun onViewCreated(v: View, s: Bundle?) {
        b.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = tabs.size
            override fun createFragment(pos: Int) = ChartPageFragment.newInstance(tabs[pos].second)
        }
        TabLayoutMediator(b.tabLayout, b.viewPager) { tab, pos -> tab.text = tabs[pos].first }.attach()
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
