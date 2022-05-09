package dz.notacompany.el_cous

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dz.notacompany.el_cous.databinding.ItemScheduleBinding

class SchedulesAdapter(var schedulesList: List<ScheduleItem>) : RecyclerView.Adapter<SchedulesAdapter.SchedulesViewHolder>() {

    inner class SchedulesViewHolder(val binding: ItemScheduleBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SchedulesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemScheduleBinding.inflate(layoutInflater, parent, false)
        return SchedulesViewHolder(binding)
    }


    override fun onBindViewHolder(holder: SchedulesViewHolder, position: Int) {
        holder.binding.apply {

            cousNumberTextView.text = "Cous N°".plus(schedulesList[position].itemOrder)
            departureTimeTextView.text = schedulesList[position].scheduleDepartureTime
            arrivalTimeTextView.text = schedulesList[position].scheduleArrivalTime
        }
    }

    override fun getItemCount(): Int {
        return  schedulesList.size
    }

}