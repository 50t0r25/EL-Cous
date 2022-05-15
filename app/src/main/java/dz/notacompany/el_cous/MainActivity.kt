package dz.notacompany.el_cous

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketAddress
import java.util.concurrent.LinkedBlockingQueue

class MainActivity : AppCompatActivity() {

    private val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth

    private lateinit var loading: AlertDialog
    var isAdmin = false

    lateinit var currentDocument: String // Used to get the Route ID from DetailsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_ELCous) // Sets theme to override splash screen theme
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        // Define the shared preferences
        val sharedpref: SharedPreferences =
            applicationContext.getSharedPreferences(
                "dz.notacompany.el_cous",
                MODE_PRIVATE
            )

        // If the token says false or is null, it means it's the first time launch
        val token: String? = sharedpref.getString("token", null)
        if (token == "False" || token == null) {
            // First time launch

            // Launch the SplashActivity
            val intent = Intent(this, SplashActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)

        } else {
            // Not the first time launch

            // Display MainActivity's layout
            setContentView(R.layout.activity_main)
        }

        replaceCurrentFragment(HomeFragment(),true)

        topBarLayout.setOnClickListener {
            supportFragmentManager.popBackStack()
        }

        adminButton.setOnClickListener {
            replaceCurrentFragment(AdminPanelFragment(),false)
        }

        githubButton.setOnClickListener {
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse("https://github.com/50t0r25/EL-Cous")
            startActivity(openURL)
        }

        // Button to delete Route (Only available for admins)
        deleteRouteButton.setOnClickListener {

            // Dialog to ask confirmations
            MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.caution))
                .setMessage(getString(R.string.delete_route))
                .setNeutralButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(getString(R.string.confirm)) { dialog, _ ->
                    // User confirms delete

                    dialog.dismiss()

                    createLoadingDialog()

                    val routeRef = db.collection("trajets").document(currentDocument)

                    // Fetch all the scheduled times of current displayed Route
                    // Used because Transactions can't fetch a whole collection for some reasons ?????????????
                    // PS: i hate google for this
                    routeRef.collection("horaires").get(Source.SERVER)
                        .addOnSuccessListener { horaires ->

                            db.runBatch { batch ->

                                // Delete all the documents inside
                                for (horaire in horaires) {
                                    batch.delete(routeRef.collection("horaires").document(horaire.id))
                                }

                                batch.delete(routeRef) // Delete Route document

                            }.addOnCompleteListener {
                                dismissLoadingDialog()

                                supportFragmentManager.popBackStack()
                            }
                        }
                }
                .show()
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and sign them up if they aren't.
        if (auth.currentUser == null) {
            auth.signInAnonymously()
        }
    }

    // Replaces fragment while either clearing or adding to BackStack
    fun replaceCurrentFragment(fragment: Fragment, clearBackStack : Boolean) =
        supportFragmentManager.beginTransaction().apply {

            // Sliding from bottom animation
            setCustomAnimations(
                R.anim.slide_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out
            )
            replace(R.id.flFragment, fragment)

            // If clearBackStack is true, clear the backstack and open the new fragment
            // Else don't clear it and add the new fragment to it
            if (clearBackStack) {
                supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            } else {
                addToBackStack(null)
            }
            commit()
        }

    // ------------------- LOADING DIALOG START ------------------------
    // Loading dialog that restricts user from pressing anything while it's displayed

    fun createLoadingDialog() {
        loading = MaterialAlertDialogBuilder(this)
            .setView(this.layoutInflater.inflate(R.layout.dialog_loading, null))
            .setCancelable(false)
            .create()
        loading.show()
    }

    fun dismissLoadingDialog() {
        loading.dismiss()
    }
    // ------------------- LOADING DIALOG END ---------------------------


    // Function checks if user has internet access
    fun isOnline(): Boolean {
        val queue = LinkedBlockingQueue<Boolean>()

        // Start a thread to run check on a non-UI thread
        // prevents freezing on networks with no internet
        Thread {
            try {
                val timeoutMs = 1500
                val sock = Socket()
                val sockaddr: SocketAddress = InetSocketAddress("8.8.8.8", 53)
                sock.connect(sockaddr, timeoutMs)
                sock.close()
                queue.add(true)
            } catch (e: IOException) {
                queue.add(false)
            }
        }.start()
        return queue.take()
    }
}