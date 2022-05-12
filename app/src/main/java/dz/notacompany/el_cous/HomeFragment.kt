package dz.notacompany.el_cous

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val db = Firebase.firestore
    private lateinit var mainAct : MainActivity

    private lateinit var source: Source

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainAct = activity as MainActivity // Reference to MainActivity

        // Resetting the elements of the top bar
        mainAct.topBarLayout.visibility = View.GONE
        mainAct.topBarTextView.visibility = View.VISIBLE
        mainAct.adminButton.visibility = View.VISIBLE
        mainAct.deleteRouteButton.visibility = View.GONE

        // These lists store all the departures, destinations and their IDs from the database
        val departureList = arrayListOf<String>()
        val destinationList = arrayListOf<String>()
        val idList = arrayListOf<String>()

        // These lists store the elements the user sees
        // I use them to avoid re doing requests to the database at every selection
        val idListToUse = arrayListOf<String>()
        val destinationListToUse = arrayListOf<String>()
        val departureListToUse = arrayListOf<String>()

        var idToLoad = String()

        mainAct.createLoadingDialog()

        // If user doesn't have internet access, fetch data from cache
        source = if (mainAct.isOnline()) Source.DEFAULT else Source.CACHE

        // Fetching data from the Database
        db.collection("trajets").get(source)
            .addOnSuccessListener { trajets ->

                // If the collection is empty, it means user probably didn't have internet access on the first launch
                // Just show an error and ask user to try again
                if (trajets.isEmpty) {
                    mainAct.dismissLoadingDialog()

                    // Dialog restricts user from doing anything
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Oops :(")
                        .setMessage(getString(R.string.empty_database_error))
                        .setCancelable(false)
                        .create()
                        .show()

                } else { // Collection was accessed successfully

                    // fill in the lists
                    for (trajet in trajets) {
                        departureList.add(trajet.data["depart"].toString())

                        // If a departure is already in the departureList once, it won't be copied in departureListToUse
                        // (Duplicates are required in departureList to keep the counting right between departures, destinations and their IDs)
                        if (!departureListToUse.contains(trajet.data["depart"].toString()))
                            departureListToUse.add(trajet.data["depart"].toString())

                        destinationList.add(trajet.data["destination"].toString())
                        idList.add(trajet.id)
                    }

                    // Fills in the destinationListToUse depending on whats selected as departure
                    for ((i, _) in destinationList.withIndex()) {
                        if (departureList[i] == departureList[0]) {
                            destinationListToUse.add(destinationList[i])
                            idListToUse.add(idList[i])
                        }
                    }

                    departureSpinner.setItems(departureListToUse)
                    destinationSpinner.setItems(destinationListToUse)
                    idToLoad = idListToUse[0]

                    mainAct.dismissLoadingDialog()
                }
            }

        // Departure selected
        departureSpinner.setOnItemSelectedListener { view, position, id, item ->

            // These get cleared because they change depending on the departure
            destinationListToUse.clear()
            idListToUse.clear()

            // Fills in the destinationListToUse depending on whats selected as departure
            for ((i, _) in destinationList.withIndex()) {
                if (departureList[i] == item.toString()) {
                    destinationListToUse.add(destinationList[i])
                    idListToUse.add(idList[i])
                }
            }

            destinationSpinner.setItems(destinationListToUse)
            idToLoad = idListToUse[0]
        }

        // When a destination is selected, all is needed is to save the ID of the corresponding trip
        destinationSpinner.setOnItemSelectedListener { view, position, id, item ->
            idToLoad = idListToUse[position]
        }

        // Search clicked
        searchButton.setOnClickListener {
            mainAct.replaceCurrentFragment(DetailsFragment(idToLoad), false)

            // Clears the spinners to fix some retarded problem
            departureSpinner.setItems(String())
            destinationSpinner.setItems(String())
        }

    }
}