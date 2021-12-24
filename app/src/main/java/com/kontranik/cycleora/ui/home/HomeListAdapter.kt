package com.kontranik.cycleora.ui.home


import android.content.Context
import android.graphics.Color

import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView

import android.view.ViewGroup

import android.view.LayoutInflater
import android.view.View
import com.kontranik.cycleora.R
import com.kontranik.cycleora.models.CDay
import java.text.SimpleDateFormat
import java.util.*

val sdf = SimpleDateFormat("dd. MMM yyyy", Locale.getDefault())

class HomeListAdapter internal constructor(context: Context?, private val cDays: List<CDay>) :
    RecyclerView.Adapter<HomeListAdapter.ViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = inflater.inflate(R.layout.infocard, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cDay: CDay = cDays[position]
        holder.badgeView.text = cDay.cyclesDay.toString()
        holder.titleView.text = com.kontranik.cycleora.ui.phases.sdf.format(cDay.date)
        val desc = StringBuffer()
        cDay.phases.forEach {
            if ( it.desc != null) desc.append(it.desc + "\n")
        }
        holder.descriptionView.text = desc
        if (cDay.color != null) {
            holder.badgeView.setBackgroundColor(Color.parseColor(cDay.color))
        } else {
            holder.badgeView.setBackgroundColor(Color.TRANSPARENT)
        }
    }

    override fun getItemCount(): Int {
        return cDays.size
    }

    class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        val titleView: TextView = view.findViewById(R.id.tv_infocard_title_text)
        val descriptionView: TextView = view.findViewById(R.id.tv_infocard_title_description)
        val badgeView: TextView = view.findViewById(R.id.tv_infocard_title_badge)
    }

}