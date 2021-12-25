package com.kontranik.easycycle.ui.statistic

import android.content.Context
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView

import android.view.ViewGroup

import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.kontranik.easycycle.R
import com.kontranik.easycycle.models.StatisticItem


class StatisticListAdapter internal constructor(val context: Context?, private val items: List<StatisticItem>) :
    RecyclerView.Adapter<StatisticListAdapter.ViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = inflater.inflate(R.layout.statisticcard, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: StatisticItem = items[position]
        holder.titleView.text = item.year
        holder.badgeView.text = item.averageCycleLength.toString()

        holder.titleLayout.setOnClickListener {
            holder.itemsView.visibility =  if ( holder.itemsView.visibility == View.VISIBLE ) View.GONE else View.VISIBLE
        }

        holder.itemsView.visibility = View.GONE

        val adapter = StatisticListDetailsAdapter(context, item.items)
        val layoutManager = LinearLayoutManager(context)
        holder.itemsView.adapter = adapter
        holder.itemsView.layoutManager = layoutManager
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        val titleLayout: LinearLayout = view.findViewById(R.id.ll_statisticcard_title)
        val titleView: TextView = view.findViewById(R.id.tv_statisticcard_title_text)
        val badgeView: TextView = view.findViewById(R.id.tv_statisticcard_title_badge)
        val itemsView: RecyclerView = view.findViewById(R.id.rv_statisticcard_items)
    }

}