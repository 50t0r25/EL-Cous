package dz.notacompany.el_cous

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        // -------- TEST ------------
        topBarTextView.setOnClickListener {
            replaceCurrentFragment(HomeFragment(),false)
        }
        // ---------------------------

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

            // If clearbackstack is true, clear the backstack and open the new fragment
            // Else don't clear it and add the new fragment to it
            if (clearBackStack) {
                supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            } else {
                addToBackStack(null)
            }
            commit()
        }
}