package com.kontranik.easycycle.ui.home

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.kontranik.easycycle.databinding.FragmentHomeBinding

import androidx.recyclerview.widget.RecyclerView
import com.kontranik.easycycle.MainActivity
import com.kontranik.easycycle.R
import com.kontranik.easycycle.constants.DefaultSettings
import com.kontranik.easycycle.helper.PhasesHelper
import com.kontranik.easycycle.models.CDay
import com.kontranik.easycycle.models.Settings
import com.kontranik.easycycle.storage.SettingsService

import com.kontranik.easycycle.ui.settings.SettingsFragment


class HomeFragment : Fragment(), SettingsFragment.SettingsListener {

    private var _binding: FragmentHomeBinding? = null

    private val cDays: MutableList<CDay> = mutableListOf()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var settings: Settings = DefaultSettings.settings

    var recyclerView: RecyclerView? = null
    lateinit var noDataTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        setHasOptionsMenu(true)

        (activity as MainActivity?)?.supportActionBar?.title = resources.getString(R.string.title_home)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val tempSettings = SettingsService.loadSettings(requireContext())
        if ( tempSettings != null) settings = tempSettings

        recyclerView = binding.rvHomeInfoList
        val adapter = HomeListAdapter(context, cDays)
        recyclerView!!.adapter = adapter

        noDataTextView = binding.tvHomeNodate
        noDataTextView.visibility = View.GONE

        loadInfo()

        return root
    }

    private fun loadInfo() {
        val lastCycle = SettingsService.loadLastCycleStart(requireContext())
        if ( lastCycle != null) {
            val result = PhasesHelper.getDaysInfo(requireContext(), settings.daysOnHome, lastCycle)
            cDays.clear()
            recyclerView!!.adapter!!.notifyDataSetChanged()
            if (result.isEmpty()) {
                noDataTextView.visibility = View.VISIBLE
            } else {
                noDataTextView.visibility = View.GONE
                cDays.addAll(result)
                recyclerView!!.adapter!!.notifyDataSetChanged()
            }
        } else {
            noDataTextView.visibility = View.VISIBLE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_settings -> {
                openSettingsFragment()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun openSettingsFragment() {
        val fragment2 = SettingsFragment()
        val fragmentManager: FragmentManager = parentFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.nav_host_fragment_activity_main, fragment2, "Home")
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSettingsChanged(settings: Settings) {
        this.settings = settings
        loadInfo()
    }
}