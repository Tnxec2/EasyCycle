package com.kontranik.easycycle.ui.phases.editphase


import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.kontranik.easycycle.MainActivity
import com.kontranik.easycycle.R
import com.kontranik.easycycle.databinding.FragmentEditPhaseBinding
import com.kontranik.easycycle.models.Phase
import com.kontranik.easycycle.storage.SettingsService
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener


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
    private lateinit var checkBoxMarkWholePhase: CheckBox
    private lateinit var checkBoxDifferentColorPrediction: CheckBox
    private lateinit var llColorPrediction: LinearLayout
    private var color: String? = null
    private var colorP: String? = null

    private var differentColorForPrediction = false

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
        if ( phase?.color != null) {
            changeColorOnView(colorTextView, Color.parseColor(phase?.color))
        } else {
            changeColorOnView(colorTextView)
        }
        color = phase?.color
        colorTextView.setOnClickListener {
            openColorPicker(color, ColorEnvelopeListener { envelope, fromUser ->
                color = "#" + envelope.hexCode
                changeColorOnView(colorTextView, envelope.color)
             })
        }
        val deleteColorButton = binding.ibEditphaseRemoveColor
        deleteColorButton.setOnClickListener {
            color = null
            changeColorOnView(colorTextView)
        }

        llColorPrediction = binding.llEditphaseColorP

        differentColorForPrediction = phase?.colorP != phase?.color

        checkBoxDifferentColorPrediction = binding.checkBoxEditphaseDifferentcolorprognose
        checkBoxDifferentColorPrediction.isChecked = differentColorForPrediction
        checkBoxDifferentColorPrediction.setOnCheckedChangeListener { compoundButton, b ->
            differentColorForPrediction = compoundButton.isChecked
            if (!differentColorForPrediction) llColorPrediction.visibility = View.GONE
            else llColorPrediction.visibility = View.VISIBLE
        }

        val colorPTextView = binding.tvEditphaseColorPButton
        if ( phase?.colorP != null) {
            changeColorOnView(colorPTextView, Color.parseColor(phase?.colorP))
        } else {
            changeColorOnView(colorPTextView)
        }
        colorP = phase?.colorP
        colorPTextView.setOnClickListener {
            openColorPicker(colorP, ColorEnvelopeListener { envelope, fromUser ->
                colorP = "#" + envelope.hexCode
                changeColorOnView(colorPTextView, envelope.color)
            })
        }
        val deleteColorPButton = binding.ibEditphaseRemoveColorP
        deleteColorPButton.setOnClickListener {
            colorP = null
            changeColorOnView(colorPTextView)
        }

        if (!differentColorForPrediction) llColorPrediction.visibility = View.GONE

        checkBoxMarkWholePhase = binding.checkBoxEditphaseMarkwholephase
        checkBoxMarkWholePhase.isChecked = phase?.markwholephase == true

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

    private fun changeColorOnView(cTextView: TextView, color: Int? = null) {
        if (color != null) {
            cTextView.setBackgroundColor(color)
        } else {
            cTextView.setBackgroundColor(Color.TRANSPARENT)
            cTextView.setBackgroundResource(R.drawable.border_for_colorbox)
        }
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
                colorP = if (checkBoxDifferentColorPrediction.isChecked) colorP else color,
                markwholephase = checkBoxMarkWholePhase.isChecked
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