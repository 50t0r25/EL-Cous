package dz.notacompany.el_cous

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_admin_panel.*

class AdminPanelFragment : Fragment(R.layout.fragment_admin_panel) {

    private val db = Firebase.firestore
    private lateinit var mainAct : MainActivity

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainAct = activity as MainActivity // Reference to MainActivity

        // Resetting the elements of the top bar
        mainAct.topBarLayout.visibility = View.VISIBLE
        mainAct.topBarTextView2.text = getString(R.string.top_bar_admin)
        mainAct.topBarTextView.visibility = View.GONE
        mainAct.adminButton.visibility = View.GONE
        mainAct.githubButton.visibility = View.GONE

        // If user isn't admin, ask for admin key
        if (!mainAct.isAdmin) {
            adminLayout.visibility = View.GONE
            nonAdminLayout.visibility = View.VISIBLE
        }

        // Button to become admin
        confirmAdminKeyButton.setOnClickListener {

            mainAct.createLoadingDialog()

            // Fetch the admin key from the DB
            db.collection("app").document("data").get(Source.SERVER)
                .addOnSuccessListener { info ->

                    // If admin key is correct, mark user as admin and display the admin layout
                    // Else ask user to try again
                    if (adminKeyInput.text.toString() == info.data!!["admin_key"].toString()) {
                        mainAct.isAdmin = true

                        adminLayout.visibility = View.VISIBLE
                        nonAdminLayout.visibility = View.GONE
                    } else {
                        Toast.makeText(context,getString(R.string.wrong_key),Toast.LENGTH_SHORT).show()
                    }

                    mainAct.dismissLoadingDialog()

                }.addOnFailureListener {
                    mainAct.dismissLoadingDialog()

                    Toast.makeText(context, it.localizedMessage, Toast.LENGTH_SHORT).show()
                }

        }

        confirmSTU.setOnClickListener {
            addTrajetToDB(stuList)
        }

        confirmUTS.setOnClickListener {
            addTrajetToDB(utsList)
        }
    }

    // Function takes a schedule preset and makes a new Route in the DB
    private fun addTrajetToDB(list : MutableList<out HashMap<String, out Any>>) {
        mainAct.createLoadingDialog()

        val departure = departureInput.text.toString().trim()
        val destination = destinationInput.text.toString().trim()

        if (departure.isNotEmpty() && destination.isNotEmpty()) {

            val newTrajet = hashMapOf(
                "depart" to departure,
                "destination" to destination
            )

            // Reference to our new route in the DB
            val docRef = db.collection("trajets").document("${departure}_${destination}")

            // Batch write adds the route then it's scheduled times
            db.runBatch { batch ->

                batch.set(docRef, newTrajet)

                for (schedule in list) {
                    batch.set(docRef.collection("horaires").document(), schedule)
                }

            }.addOnCompleteListener {
                mainAct.dismissLoadingDialog()

                parentFragmentManager.popBackStack()

            }.addOnFailureListener {
                mainAct.dismissLoadingDialog()

                Toast.makeText(context, it.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Uni To Stop schedule preset
    private val utsList = mutableListOf(
        hashMapOf(
            "ordre" to 1,
            "depart" to "09:00",
            "arrive" to "09:30"
        ),
        hashMapOf(
            "ordre" to 2,
            "depart" to "10:00",
            "arrive" to "10:30"
        ),
        hashMapOf(
            "ordre" to 3,
            "depart" to "11:00",
            "arrive" to "11:30"
        ),
        hashMapOf(
            "ordre" to 4,
            "depart" to "12:00",
            "arrive" to "12:30"
        ),
        hashMapOf(
            "ordre" to 5,
            "depart" to "13:00",
            "arrive" to "13:30"
        ),
        hashMapOf(
            "ordre" to 6,
            "depart" to "14:00",
            "arrive" to "14:30"
        ),
        hashMapOf(
            "ordre" to 7,
            "depart" to "15:00",
            "arrive" to "15:30"
        ),
        hashMapOf(
            "ordre" to 8,
            "depart" to "16:00",
            "arrive" to "16:30"
        ),
        hashMapOf(
            "ordre" to 9,
            "depart" to "17:00",
            "arrive" to "17:30"
        )
    )

    // Stop To Uni schedule preset
    private val stuList = mutableListOf(
        hashMapOf(
            "ordre" to 1,
            "depart" to "07:30",
            "arrive" to "08:00"
        ),
        hashMapOf(
            "ordre" to 2,
            "depart" to "08:00",
            "arrive" to "08:30"
        ),
        hashMapOf(
            "ordre" to 3,
            "depart" to "08:30",
            "arrive" to "09:00"
        ),
        hashMapOf(
            "ordre" to 4,
            "depart" to "09:00",
            "arrive" to "09:30"
        ),
        hashMapOf(
            "ordre" to 5,
            "depart" to "10:00",
            "arrive" to "10:30"
        ),
        hashMapOf(
            "ordre" to 6,
            "depart" to "11:00",
            "arrive" to "11:30"
        ),
        hashMapOf(
            "ordre" to 7,
            "depart" to "12:00",
            "arrive" to "12:30"
        ),
        hashMapOf(
            "ordre" to 8,
            "depart" to "13:00",
            "arrive" to "13:30"
        )
    )


}