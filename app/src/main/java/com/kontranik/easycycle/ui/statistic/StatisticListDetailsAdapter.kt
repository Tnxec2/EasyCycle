package com.kontranik.easycycle.ui.statistic

import android.content.Context
import android.content.DialogInterface
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView

import android.view.ViewGroup

import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import com.kontranik.easycycle.MainViewModel
import com.kontranik.easycycle.R
import com.kontranik.easycycle.database.DatabaseService
import com.kontranik.easycycle.models.LastCycle
import java.text.SimpleDateFormat
import java.util.*

val sdf = SimpleDateFormat("dd. MMM yyyy", Locale.getDefault())

class StatisticListDetailsAdapter internal constructor(
    val context: Context?,
    private val items: MutableList<LastCycle>,
    private val viewModel: MainViewModel
    ) :
    RecyclerView.Adapter<StatisticListDetailsAdapter.ViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = inflater.inflate(R.layout.statisticcard_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: LastCycle = items[position]
        holder.textView.text = sdf.format(item.cycleStart)
        holder.badgeView.text = item.lengthOfLastCycle.toString()
        holder.titleLayout.setOnClickListener {
            val canRemove = item.clickedToRemove()
            if (canRemove) {
                openDeleteItemAlert(item, position)
            }
        }
    }

    private fun openDeleteItemAlert(item: LastCycle, position: Int) {
        val alertDialogBuilder = AlertDialog.Builder(context!!);
        alertDialogBuilder.setTitle(context.getString(R.string.delete_statistic_item))
        alertDialogBuilder.setMessage(context.getString(R.string.are_you_sure_to_delete_statistic));
        alertDialogBuilder.setPositiveButton(context.getString(R.string.yes),
            DialogInterface.OnClickListener { _, _ -> removeStatisticItem(item, position) })
        alertDialogBuilder.setNegativeButton(context.getString(R.string.no),
            DialogInterface.OnClickListener { _, _ -> {  } })
        val alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private fun removeStatisticItem(item: LastCycle, position: Int) {
        if (item.id != null) {
            if ( viewModel.deleteStatisticItemById(item.id!!) > 0) {
                items.removeAt(position)
                notifyItemRemoved(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        val titleLayout: LinearLayout = view.findViewById(R.id.ll_statisticcard_item)
        val textView: TextView = view.findViewById(R.id.tv_statisticcard_item_text)
        val badgeView: TextView = view.findViewById(R.id.tv_statisticcard_item_badge)
    }

}