package com.kontranik.cycleora.ui.phases.editphase

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.kontranik.cycleora.databinding.FragmentEditPhaseBinding
import com.kontranik.cycleora.models.Phase
import com.kontranik.cycleora.storage.SettingsService
import com.skydoves.colorpickerview.ActionMode
import com.skydoves.colorpickerview.ColorPickerView
import android.content.DialogInterface
import com.kontranik.cycleora.MainActivity
import com.kontranik.cycleora.R


import com.skydoves.colorpickerview.ColorEnvelope

import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener

import com.skydoves.colorpickerview.ColorPickerDialog









// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PHASE = "phase"


class EditPhaseFragment : Fragment() {
    private var phase: Phase? = null

    private var _binding: FragmentEditPhaseBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var editFrom: EditText
    private lateinit var editTo: EditText
    private lateinit var editDescription: EditText
    private var color: String? = null
    private var colorP: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            phase = it.getSerializable(ARG_PHASE) as Phase?
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        (activity as MainActivity?)?.supportActionBar?.title = resources.getString(R.string.edit_phase)

        _binding = FragmentEditPhaseBinding.inflate(inflater, container, false)
        val root: View = binding.root

        editFrom = binding.edittextEditphaseFrom
        editFrom.setText(phase?.from.toString())

        editTo = binding.editTextEditphaseTo
        if (phase!!.to != null) editTo.setText(phase!!.to.toString())

        editDescription = binding.editTextEditphaseDescription
        if (phase!!.desc != null) editDescription.setText(phase!!.desc.toString())

        val colorTextView = binding.tvEditphaseColorButton
        if ( phase?.color != null)
            colorTextView.setBackgroundColor(Color.parseColor(phase?.color))
        color = phase?.color
        colorTextView.setOnClickListener {
            openColorPicker(color, ColorEnvelopeListener { envelope, fromUser ->
                color = "#" + envelope.hexCode
                colorTextView.setBackgroundColor(envelope.color)
             })
        }
        val deleteColorButton = binding.ibEditphaseRemoveColor
        deleteColorButton.setOnClickListener {
            color = null
            colorTextView.setBackgroundColor(Color.TRANSPARENT)
        }
        val colorPTextView = binding.tvEditphaseColorPButton
        if ( phase?.colorP != null)
            colorPTextView.setBackgroundColor(Color.parseColor(phase?.colorP))
        colorP = phase?.colorP
        colorPTextView.setOnClickListener {
            openColorPicker(colorP, ColorEnvelopeListener { envelope, fromUser ->
                colorP = "#" + envelope.hexCode
                colorPTextView.setBackgroundColor(envelope.color)
            })
        }
        val deleteColorPButton = binding.ibEditphaseRemoveColorP
        deleteColorPButton.setOnClickListener {
            colorP = null
            colorPTextView.setBackgroundColor(Color.TRANSPARENT)
        }
        val cancelButton = binding.btnEditphaseCancel
        cancelButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val saveButton = binding.btnEditphaseSave
        saveButton.setOnClickListener {
            savePhase()
            parentFragmentManager.popBackStack()
        }

        return root
    }



    private fun openColorPicker(hexColorString: String?, listener: ColorEnvelopeListener) {
        val picker = ColorPickerDialog.Builder(requireContext())
            .setTitle("ColorPicker Dialog")
            .setPreferenceName("MyColorPickerDialog")
            .setPositiveButton(getString(R.string.ok), listener)
            .setNegativeButton(
                getString(R.string.cancel)
            ) { dialogInterface, i -> dialogInterface.dismiss() }
            .attachAlphaSlideBar(false) // the default value is true.
            .attachBrightnessSlideBar(true) // the default value is true.
            .setBottomSpace(12) // set a bottom space between the last slidebar and buttons.

        val initColor = if ( hexColorString != null ) Color.parseColor(hexColorString) else Color.WHITE
        picker.colorPickerView.setInitialColor(initColor)
        picker.show()
    }

    private fun savePhase() {
        SettingsService.saveCustomPhase( requireContext(),
            Phase(
                key = phase!!.key,
                from = editFrom.text.toString().toLong(),
                to = editTo.text.toString().toLong(),
                desc = editDescription.text.toString(),
                color = color,
                colorP = colorP,
                markwholephase = phase!!.markwholephase
        ))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param phase Phase to edit.
         * @return A new instance of fragment EditPhaseFragment.
         */

        @JvmStatic
        fun newInstance(phase: Phase) =
            EditPhaseFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PHASE, phase)
                }
            }
    }
}