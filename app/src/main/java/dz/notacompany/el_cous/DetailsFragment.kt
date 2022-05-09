package dz.notacompany.el_cous

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_details.*

class DetailsFragment(private val documentID : String) : Fragment(R.layout.fragment_details) {

    private val db = Firebase.firestore
    private lateinit var mainAct : MainActivity

    private lateinit var source: Source

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainAct = activity as MainActivity // Reference to MainActivity

        // Resetting the elements of the top bar
        mainAct.topBarLayout.visibility = View.VISIBLE
        mainAct.topBarTextView.visibility = View.GONE

        mainAct.createLoadingDialog()

        // If user doesn't have internet access, fetch data from cache
        source = if (mainAct.isOnline()) Source.DEFAULT else Source.CACHE

        db.collection("trajets").document(documentID).get(source)
            .addOnSuccessListener { trajet ->

                currentDepartureTextView.text = trajet.data!!["depart"].toString()
                currentDestinationTextView.text = trajet.data!!["destination"].toString()

                mainAct.dismissLoadingDialog()
            }

    }
}