package com.kontranik.easycycle.ui.phases


import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.kontranik.easycycle.R
import com.kontranik.easycycle.models.Phase
import com.kontranik.easycycle.ui.phases.editphase.EditPhaseFragment
import java.text.SimpleDateFormat
import java.util.*

val sdf = SimpleDateFormat("dd. MMM yyyy", Locale.getDefault())

class PhasesListAdapter internal constructor(
    private val listener: PhasesListAdapterListener,
    private val context: Context?,
    private val fragmentManager: FragmentManager,
    private val phases: MutableList<Phase>) :
    RecyclerView.Adapter<PhasesListAdapter.ViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    interface PhasesListAdapterListener {
        fun onRemoveItem(phase: Phase)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = inflater.inflate(R.layout.card_phase, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val phase: Phase = phases[position]
        if ( phase.to == null )
            holder.fromtoView.text = context!!.resources.getString(R.string.phase_from, phase.from)
        else
            holder.fromtoView.text = context!!.resources.getString(R.string.phase_from, phase.from) + " " + context!!.resources.getString(R.string.phase_to, phase.to)
        holder.descriptionView.text = phase.desc

        if ( phase.color != null) {
            holder.colorView.setBackgroundColor(Color.parseColor(phase.color))
        } else {
            holder.colorView.setBackgroundColor(Color.TRANSPARENT)
        }
        if ( phase.colorP != null) {
            holder.colorPView.setBackgroundColor(Color.parseColor(phase.colorP))
        }else {
            holder.colorPView.setBackgroundColor(Color.TRANSPARENT)
        }
        if (phase.color != null || phase.colorP != null) {
            if (phase.markwholephase == true)  {
                holder.markWholePhaseView.text = context.getString(R.string.mark_whole_phase)
            } else {
                holder.markWholePhaseView.text = context.getString(R.string.mark_only_phase_start)
            }
        } else {
            holder.markWholePhaseView.visibility = View.GONE
        }

        holder.deleteImageButton.setOnClickListener {
            listener.onRemoveItem(phase)
        }
        
        holder.editImageButton.setOnClickListener { 
            openEditPhaseFragment(phase)
        }
    }

    private fun openEditPhaseFragment(item: Phase) {
        val editPhaseFragment = EditPhaseFragment.newInstance(item)
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.nav_host_fragment_activity_main, editPhaseFragment, "PhasesList")
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    override fun getItemCount(): Int {
        return phases.size
    }

    class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        val fromtoView: TextView = view.findViewById(R.id.tv_phase_card_from_to)
        val descriptionView: TextView = view.findViewById(R.id.tv_phase_card_description)
        val colorView: TextView = view.findViewById(R.id.tv_phase_card_color)
        val colorPView: TextView = view.findViewById(R.id.tv_phase_card_colorP)
        val markWholePhaseView: TextView = view.findViewById(R.id.tv_phase_card_markwholephase)
        val editImageButton: ImageButton = view.findViewById(R.id.ib_phase_card_edit)
        val deleteImageButton: ImageButton = view.findViewById(R.id.ib_phase_card_delete)
    }

}