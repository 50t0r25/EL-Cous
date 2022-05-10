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

        if (!mainAct.isAdmin) {
            adminLayout.visibility = View.GONE
            nonAdminLayout.visibility = View.VISIBLE
        }

        confirmAdminKeyButton.setOnClickListener {

            mainAct.createLoadingDialog()

            db.collection("app").document("data").get(Source.SERVER)
                .addOnSuccessListener { info ->

                    if (adminKeyInput.text.toString() == info.data!!["admin_key"].toString()) {
                        mainAct.isAdmin = true

                        adminLayout.visibility = View.VISIBLE
                        nonAdminLayout.visibility = View.GONE
                    } else {
                        Toast.makeText(context,getString(R.string.wrong_key),Toast.LENGTH_SHORT).show()
                    }

                    mainAct.dismissLoadingDialog()
                }

        }

        confirmSTU.setOnClickListener {



        }

        confirmUTS.setOnClickListener {



        }
    }
}