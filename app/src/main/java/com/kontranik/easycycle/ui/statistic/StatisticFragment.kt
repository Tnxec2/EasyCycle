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


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel

    private var yearsOnStatistic: Int = DefaultSettings.settings.yearsOnStatistic

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)

        _binding = FragmentStatisticBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        binding.llStatisticNodata.visibility = View.GONE

        val adapter = StatisticListAdapter(context, viewModel.getListOfYearsStatistic(), viewModel)
        val layoutManager = LinearLayoutManager(context)
        binding.rvStatisticList.adapter = adapter
        binding.rvStatisticList.layoutManager = layoutManager

        val btnImportStatistic = binding.btnStatisticImportData
        btnImportStatistic.setOnClickListener { openImportInfoAlert() }

        viewModel.settings.observe(viewLifecycleOwner, Observer {
            yearsOnStatistic = it.yearsOnStatistic
            loadArchiv()
        })
        viewModel.averageCycleLength.observe(viewLifecycleOwner, Observer {
            binding.tvStatisticAverageCycleLength.text = resources.getString(R.string.average_cycle_length_statistic, it)
        })
        return root
    }

    fun loadArchiv() {
        viewModel.loadListOfYearsStatistic(yearsOnStatistic)
        if ( viewModel.getListOfYearsStatistic().isEmpty() ) {
            binding.llStatisticNodata.visibility = View.VISIBLE
            binding.rvStatisticList.visibility = View.GONE
            binding.tvStatisticAverageCycleLength.visibility = View.GONE
        } else {
            binding.llStatisticNodata.visibility = View.GONE
            binding.rvStatisticList.visibility = View.VISIBLE
            binding.tvStatisticAverageCycleLength.visibility = View.VISIBLE
        }
        binding.rvStatisticList.adapter!!.notifyDataSetChanged()
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
            R.id.menu_statistic_help -> {
                openHelpDialog()
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

    private fun openHelpDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle(resources.getString(R.string.help))
        alertDialogBuilder.setMessage(resources.getString(R.string.help_statistic))
        alertDialogBuilder.setCancelable(false)
        alertDialogBuilder.setPositiveButton(resources.getString(R.string.ok),
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