package com.kontranik.easycycle.ui.phases

import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.kontranik.easycycle.MainActivity
import com.kontranik.easycycle.MainViewModel
import com.kontranik.easycycle.R
import com.kontranik.easycycle.databinding.FragmentPhasesBinding
import com.kontranik.easycycle.models.Phase
import com.kontranik.easycycle.ui.phases.editphase.EditPhaseDialogFragment
import java.util.*

class PhasesFragment : Fragment(), PhasesListAdapter.PhasesListAdapterListener {

    private var _binding: FragmentPhasesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var shareModel: MainViewModel

    private var recyclerView: RecyclerView? = null
    private lateinit var noDataTextView: TextView
    private lateinit var btnLoadPredefindePhases: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        setHasOptionsMenu(true)
        (activity as MainActivity?)?.supportActionBar?.title =
            resources.getString(R.string.title_phases)

        _binding = FragmentPhasesBinding.inflate(inflater, container, false)
        val root: View = binding.root
        shareModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        recyclerView = binding.rvPhasesList

        shareModel.phases.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val adapter = PhasesListAdapter(
                listener = this,
                context = requireContext(),
                parentFragmentManager,
                it
            )
            recyclerView!!.adapter = adapter
            if (it.isEmpty()) {
                noDataTextView.visibility = View.VISIBLE
                btnLoadPredefindePhases.visibility = View.VISIBLE
            } else {
                noDataTextView.visibility = View.GONE
                btnLoadPredefindePhases.visibility = View.GONE
            }
        })

        noDataTextView = binding.tvPhasesNodate
        noDataTextView.visibility = View.GONE
        btnLoadPredefindePhases = binding.btnPhasesImportCustom
        btnLoadPredefindePhases.visibility = View.GONE
        btnLoadPredefindePhases.setOnClickListener {
            shareModel.loadPredefindePhases()
        }

        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.phases_toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_phases_add -> {
                openEditPhaseFragment()
                true
            }
            R.id.menu_phases_load_predefined -> {
                showLoadPredefinedPhasesAlert()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openEditPhaseFragment() {
        val editPhaseFragment = EditPhaseDialogFragment.newInstance(
            Phase(
                key = Date().time,
                from = 1
            )
        )
        val fragmentTransaction: FragmentTransaction = requireFragmentManager().beginTransaction()
        fragmentTransaction.replace(
            R.id.nav_host_fragment_activity_main,
            editPhaseFragment,
            "PhasesList"
        )
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }


    private fun showLoadPredefinedPhasesAlert() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext());
        alertDialogBuilder.setTitle(getString(R.string.load_predefined_phases))
        alertDialogBuilder.setMessage(getString(R.string.are_you_sure_to_load_predefined_phases));
        alertDialogBuilder.setPositiveButton(getString(R.string.yes),
            DialogInterface.OnClickListener { _, _ -> shareModel.loadPredefindePhases() })
        alertDialogBuilder.setNegativeButton(getString(R.string.no),
            DialogInterface.OnClickListener { _, _ -> { } })
        val alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onRemoveItem(phase: Phase) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext());
        alertDialogBuilder.setTitle(getString(R.string.delete_phase_item))
        alertDialogBuilder.setMessage(getString(R.string.are_you_sure_to_delete_phase));
        alertDialogBuilder.setPositiveButton(getString(R.string.yes),
            DialogInterface.OnClickListener { _, _ -> removeItem(phase) })
        alertDialogBuilder.setNegativeButton(getString(R.string.no),
            DialogInterface.OnClickListener { _, _ -> { } })
        val alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private fun removeItem(phase: Phase) {
        shareModel.removePhase(phase)
    }

}