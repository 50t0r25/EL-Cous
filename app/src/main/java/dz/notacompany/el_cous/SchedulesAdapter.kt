package dz.notacompany.el_cous

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dz.notacompany.el_cous.databinding.ItemScheduleBinding

class SchedulesAdapter(private val context : Context, private var schedulesList: List<ScheduleItem>, private val onScheduleClick : (position : Int, textView : TextView) -> Unit) : RecyclerView.Adapter<SchedulesAdapter.SchedulesViewHolder>() {

    inner class SchedulesViewHolder(val binding: ItemScheduleBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SchedulesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemScheduleBinding.inflate(layoutInflater, parent, false)
        return SchedulesViewHolder(binding)
    }


    override fun onBindViewHolder(holder: SchedulesViewHolder, position: Int) {
        holder.binding.apply {

            itemTripCard.setOnClickListener {
                onScheduleClick(position, delaysTextView)
            }

            if (schedulesList[position].delays != 0) {
                delaysTextView.visibility = View.VISIBLE
                delaysTextView.text = "${context.getString(R.string.reported_delays0)} ${schedulesList[position].delays} ${context.getString(R.string.reported_delays1)}"
                if (schedulesList[position].userHasReported) delaysTextView.setTextColor(Color.parseColor("#EF5F00"))
            }
            cousNumberTextView.text = "Cous N°".plus(schedulesList[position].itemOrder)
            departureTimeTextView.text = schedulesList[position].scheduleDepartureTime
            arrivalTimeTextView.text = schedulesList[position].scheduleArrivalTime
        }
    }

    override fun getItemCount(): Int {
        return  schedulesList.size
    }

}