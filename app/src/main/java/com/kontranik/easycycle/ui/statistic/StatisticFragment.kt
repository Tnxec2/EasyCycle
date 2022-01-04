package com.kontranik.easycycle.ui.statistic

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kontranik.easycycle.R
import com.kontranik.easycycle.constants.DefaultSettings
import com.kontranik.easycycle.database.DatabaseService
import com.kontranik.easycycle.database.sdfIso
import com.kontranik.easycycle.databinding.FragmentStatisticBinding
import com.kontranik.easycycle.models.LastCycle
import com.kontranik.easycycle.models.Settings
import com.kontranik.easycycle.models.StatisticItem
import com.kontranik.easycycle.storage.SettingsService
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader


class StatisticFragment : Fragment() {

    private var _binding: FragmentStatisticBinding? = null

    private val listOfYears: MutableList<StatisticItem> = mutableListOf()

    private lateinit var databaseService: DatabaseService

    private lateinit var recyclerView: RecyclerView
    private lateinit var llNoData: LinearLayout


    private var settings: Settings = DefaultSettings.settings

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)

        _binding = FragmentStatisticBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val tempSettings = SettingsService.loadSettings(requireContext())
        if ( tempSettings != null) settings = tempSettings

        databaseService = DatabaseService(requireContext())

        llNoData = binding.llStatisticNodata
        llNoData.visibility = View.GONE

        recyclerView = binding.rvStatisticList
        val adapter = StatisticListAdapter(context, listOfYears)
        val layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager

        val btnImportStatistic = binding.btnStatisticImportData
        btnImportStatistic.setOnClickListener { openImportInfoAlert() }

        loadArchiv()

        return root
    }

    fun loadArchiv() {
        Log.d("NIK", "loadArchiv")
        listOfYears.clear()
        recyclerView.adapter!!.notifyDataSetChanged()
        val items = databaseService.getArchivList(settings.yearsOnStatistic)
        if ( items.isEmpty() ) {
            llNoData.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            llNoData.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
        listOfYears.addAll(items)
        recyclerView.adapter!!.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(com.kontranik.easycycle.R.menu.statistic_toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_statistic_import -> {
                openImportInfoAlert()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openFilePicker() {
        var chooseFile = Intent(Intent.ACTION_GET_CONTENT)

        chooseFile.type = "text/comma-separated-values"
        chooseFile = Intent.createChooser(chooseFile, resources.getString(R.string.choose_csv_file))
        startActivityForResult(chooseFile, PICKFILE_RESULT_CODE)
    }

    private fun openImportInfoAlert() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle(resources.getString(R.string.import_statistic))
        alertDialogBuilder.setMessage(resources.getString(R.string.import_description))
        alertDialogBuilder.setPositiveButton(resources.getString(R.string.ok),
            DialogInterface.OnClickListener { _, _ -> openFilePicker() })
        alertDialogBuilder.setNegativeButton(resources.getString((R.string.cancel)),
            DialogInterface.OnClickListener { _, _ -> {  } })
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data != null) {
            if (requestCode == PICKFILE_RESULT_CODE && resultCode == -1) {
                val fileUri = data.data
                importStatisticFromFile(fileUri)
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    private fun importStatisticFromFile(fileUri: Uri?) {
        if ( fileUri != null) {
            try {
                val inputStream: InputStream? = requireContext().contentResolver.openInputStream(fileUri)
                val r = BufferedReader(InputStreamReader(inputStream))
                var countSaved: Int = 0
                var line: String? = ""
                while (true) {
                    try {
                        if ( (r.readLine().also { line = it }) == null) break
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    var delimiter = ""
                    if ( line!!.contains(";")) delimiter = ";"
                    else if (line!!.contains(",")) delimiter = ","
                    if ( delimiter != "") {
                        val ar = line!!.split(delimiter)
                        if (ar.size >= 2) {
                            try {
                                val cycleStart = sdfIso.parse(ar[0])
                                val length = ar[1].toInt()
                                if (cycleStart != null) {
                                    val cycleItem = LastCycle(
                                        cycleStart = cycleStart,
                                        lengthOfLastCycle = length
                                    )
                                    databaseService.add(cycleItem)
                                    countSaved += 1
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
                r.close()
                inputStream?.close()

                Log.d("EasyCycle", "Imported items $countSaved")
                if ( countSaved > 0) {
                    val lastCycle = SettingsService.loadLastCycleStart(requireContext())
                    if ( lastCycle == null) {
                        Log.d("EasyCycle", "set lastCycle...")
                        val lastOneCycle = databaseService.getLastOne()
                        if (lastOneCycle != null) {
                            SettingsService.saveLastCycleStart(
                                LastCycle(
                                    cycleStart = lastOneCycle.cycleStart,
                                    lengthOfLastCycle = lastOneCycle.lengthOfLastCycle)
                                , requireContext()
                            )
                        }
                    }
                    loadArchiv()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        const val PICKFILE_RESULT_CODE = 1
    }
}