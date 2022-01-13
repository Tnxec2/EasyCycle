package com.kontranik.easycycle.ui.statistic

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kontranik.easycycle.MainViewModel
import com.kontranik.easycycle.R
import com.kontranik.easycycle.constants.DefaultSettings
import com.kontranik.easycycle.databinding.FragmentStatisticBinding


class StatisticFragment : Fragment() {

    private var _binding: FragmentStatisticBinding? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var llNoData: LinearLayout

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewModel: StatisticFragmentViewModel
    private lateinit var shareModel: MainViewModel

    private var yearsOnStatistic: Int = DefaultSettings.settings.yearsOnStatistic

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)

        _binding = FragmentStatisticBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewModel = ViewModelProvider(requireActivity()).get(StatisticFragmentViewModel::class.java)
        shareModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        llNoData = binding.llStatisticNodata
        llNoData.visibility = View.GONE

        recyclerView = binding.rvStatisticList
        val adapter = StatisticListAdapter(context, viewModel.getListOfYears())
        val layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager

        val btnImportStatistic = binding.btnStatisticImportData
        btnImportStatistic.setOnClickListener { openImportInfoAlert() }

        shareModel.settings.observe(viewLifecycleOwner, Observer {
            yearsOnStatistic = it.yearsOnStatistic
            loadArchiv()
        })

        return root
    }

    fun loadArchiv() {
        viewModel.loadListOfYears(yearsOnStatistic)
        if ( viewModel.getListOfYears().isEmpty() ) {
            llNoData.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            llNoData.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
        recyclerView.adapter!!.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.statistic_toolbar_menu, menu)
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
                viewModel.importStatisticFromFile(fileUri)
                loadArchiv()
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    companion object {
        const val PICKFILE_RESULT_CODE = 1
    }
}