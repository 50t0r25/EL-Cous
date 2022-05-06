package dz.notacompany.el_cous

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val db = Firebase.firestore

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var departureList = arrayListOf<String>()
        var destinationList = arrayListOf<String>()

        db.collection("trajets").get()
            .addOnSuccessListener { trajets ->
                for ((i, trajet) in trajets.withIndex()) {
                    departureList.add(trajet.data["depart"].toString())
                    destinationList.add(trajet.data["destination"].toString())
                }
                departureSpinner.setItems(departureList)
            }


        destinationSpinner.setItems(destinationList)
        destinationSpinner.isClickable = false
        departureSpinnerLayout.setOnClickListener {
            if (!destinationSpinner.isClickable) {
                Toast.makeText(context, "Choissisez un depart!", Toast.LENGTH_LONG).show()
            }
        }

        departureSpinner.setOnItemSelectedListener { view, position, id, item ->
            destinationSpinner.isClickable = true
            Toast.makeText(context, departureList[position], Toast.LENGTH_LONG).show()
        }

        destinationSpinner.setOnItemSelectedListener { view, position, id, item ->
            Toast.makeText(context, destinationList[position], Toast.LENGTH_LONG).show()
        }

    }
}