package dz.notacompany.el_cous

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_details.*
import java.util.*
import kotlin.collections.ArrayList

class DetailsFragment(private val documentID : String) : Fragment(R.layout.fragment_details) {

    private val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth
    private lateinit var mainAct : MainActivity

    private lateinit var source: Source

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        mainAct = activity as MainActivity // Reference to MainActivity

        mainAct.currentDocument = documentID // Used to delete Routes if admin is user

        // Resetting the elements of the top bar
        mainAct.topBarLayout.visibility = View.VISIBLE
        mainAct.topBarTextView2.text = getString(R.string.top_bar_schedules)
        mainAct.topBarTextView.visibility = View.GONE
        mainAct.adminButton.visibility = View.GONE
        if (mainAct.isAdmin) mainAct.deleteRouteButton.visibility = View.VISIBLE

        val schedulesList = mutableListOf<ScheduleItem>()

        // Function will be passed to the adapter to run stuff that can't be run inside it otherwise
        // Runs when the schedule items are clicked
        fun onScheduleClick(position : Int, textView : TextView) {
            // Depending on if the user has already reported a delay on this schedule
            // They will either add a report, or remove their report if done previously

            // Initialize stuff depending on the above
            var thisTitle = getString(R.string.confirm_report)
            var dialogMessage = getString(R.string.add_report)
            var isRemoving = false
            if (schedulesList[position].userHasReported) {
                thisTitle = getString(R.string.confirm_remove_report)
                dialogMessage = getString(R.string.remove_report)
                isRemoving = true
            }

            // Ask for confirmation
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(thisTitle)
                .setMessage(dialogMessage)
                .setNeutralButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(getString(R.string.confirm)) { dialog, _ ->
                    // User confirms

                    dialog.dismiss()

                    mainAct.createLoadingDialog()

                    val scheduleRef = db.collection("trajets").document(documentID).collection("horaires").document(schedulesList[position].itemID)
                    var reportsSize = 0

                    if (isRemoving) { // User is removing his report

                        db.runTransaction { transaction ->
                            val thisSchedule = transaction.get(scheduleRef)
                            var newReports : ArrayList<String>? = arrayListOf()

                            val reports = thisSchedule.data?.get("retards") as ArrayList<String>?

                            reports?.let { newReports!!.addAll(it) }
                            newReports!!.remove(auth.uid.toString())

                            reportsSize = newReports.size

                            val cal = Calendar.getInstance()
                            var newDate : String? = "${cal.get(Calendar.MONTH)}/${cal.get(Calendar.DAY_OF_MONTH)}"

                            if (newReports.size == 0) {
                                newReports = null
                                newDate = null
                            }

                            val newData = hashMapOf(
                                "retards" to newReports,
                                "lastReport" to newDate
                            )

                            transaction.set(scheduleRef, newData, SetOptions.merge())

                            null
                        }.addOnSuccessListener {
                            mainAct.dismissLoadingDialog()

                            textView.visibility = View.VISIBLE
                            textView.setTextColor(Color.parseColor("#92000000"))
                            schedulesList[position].userHasReported = false
                            textView.text = "${getString(R.string.reported_delays0)} $reportsSize ${getString(R.string.reported_delays1)}"
                            if (reportsSize == 0) textView.visibility = View.GONE

                        }.addOnFailureListener {
                            mainAct.dismissLoadingDialog()

                            Toast.makeText(context, it.localizedMessage, Toast.LENGTH_SHORT).show()
                        }

                    } else { // User is adding a report

                        db.runTransaction { transaction ->
                            val thisSchedule = transaction.get(scheduleRef)
                            val newReports : ArrayList<String> = arrayListOf()

                            val reports = thisSchedule.data?.get("retards") as ArrayList<String>?

                            reports?.let { newReports.addAll(it) }
                            newReports.add(auth.uid.toString())

                            reportsSize = newReports.size

                            val cal = Calendar.getInstance()

                            val newData = hashMapOf(
                                "retards" to newReports,
                                "lastReport" to "${cal.get(Calendar.MONTH)}/${cal.get(Calendar.DAY_OF_MONTH)}"
                            )

                            transaction.set(scheduleRef, newData, SetOptions.merge())

                            null
                        }.addOnSuccessListener {
                            mainAct.dismissLoadingDialog()

                            textView.visibility = View.VISIBLE
                            textView.setTextColor(Color.parseColor("#EF5F00"))
                            schedulesList[position].userHasReported = true
                            textView.text = "${getString(R.string.reported_delays0)} $reportsSize ${getString(R.string.reported_delays1)}"

                        }.addOnFailureListener {
                            mainAct.dismissLoadingDialog()

                            Toast.makeText(context, it.localizedMessage, Toast.LENGTH_SHORT).show()
                        }

                    }
                }
                .show()
        }

        mainAct.createLoadingDialog()

        // If user doesn't have internet access, fetch data from cache
        source = if (mainAct.isOnline()) Source.DEFAULT else Source.CACHE

        // Fetches this route's data from DB
        db.collection("trajets").document(documentID).get(source)
            .addOnSuccessListener { trajet ->

                currentDepartureTextView.text = trajet.data!!["depart"].toString()
                currentDestinationTextView.text = trajet.data!!["destination"].toString()

                // Fetches all scheduled routes times from DB
                db.collection("trajets").document(documentID).collection("horaires").get(source)
                    .addOnSuccessListener { horaires ->

                        if (horaires.isEmpty) { // Didn't get any results

                            // Display error message
                            schedulesRecyclerView.visibility = View.GONE
                            errorTextView.visibility = View.VISIBLE

                            mainAct.dismissLoadingDialog()

                        } else { // Got results

                            // Adds each schedule object from the DB to our list
                            for (horaire in horaires) {

                                // Get the current device's day & month
                                val cal = Calendar.getInstance()
                                val currentDate = "${cal.get(Calendar.MONTH)}/${cal.get(Calendar.DAY_OF_MONTH)}"

                                // If there is a last reported day & month and it doesn't match the current day
                                // Clear all the reports, then add the schedule to our list
                                // (Basically will make it look like the reports clear every 24h, but it's done client-side)
                                if (horaire.data["lastReport"] != null && horaire.data["lastReport"].toString() != currentDate) {
                                    db.runBatch { batch ->

                                        val horaireRef = db.collection("trajets").document(documentID).collection("horaires").document(horaire.id)
                                        val newData = hashMapOf(
                                            "lastReport" to null,
                                            "retards" to null
                                        )
                                        batch.set(horaireRef, newData, SetOptions.merge())

                                        addScheduleToList(horaire,schedulesList,true)
                                    }
                                } else { // Just add schedule to list normally
                                    addScheduleToList(horaire,schedulesList,false)
                                }


                            }
                            schedulesList.sortBy { it.itemOrder } // Sorts the list by the itemOrder variable

                            // Initializes the RecyclerView with the adapter
                            schedulesRecyclerView.adapter = SchedulesAdapter(requireContext(), schedulesList, { position, textView -> onScheduleClick(position, textView)})
                            schedulesRecyclerView.layoutManager = LinearLayoutManager(context)

                            mainAct.dismissLoadingDialog()

                        }
                    }
            }

    }

    // Function adds a schedule from the received object from the DB to a list of ScheduleItem
    private fun addScheduleToList(horaire: QueryDocumentSnapshot, list: MutableList<ScheduleItem>, gotCleared : Boolean) {
        // gotCleared used to display schedules as empty when the date doesn't match and they get cleared
        // Since the clearing isn't instant, gotta manually make them empty client-side

        val id = horaire.id
        val order = horaire.data["ordre"].toString().toInt()
        val departure = horaire.data["depart"].toString()
        val arrival = horaire.data["arrive"].toString()

        // retards = delays :)
        val retards = horaire.data["retards"] as ArrayList<String>?
        var delays = 0
        var userHasReported = false
        if (retards != null && !gotCleared) {
            delays = retards.size
            if (retards.contains(auth.uid.toString())) userHasReported = true
        }

        list.add(ScheduleItem(id,order,departure,arrival,delays,userHasReported))
    }
}