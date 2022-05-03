package dz.notacompany.el_cous

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        replaceCurrentFragment(HomeFragment())

        topBarTextView.setOnClickListener {
            replaceCurrentFragment(HomeFragment())
        }

    }

    // Replaces fragment and adding it to backstack
    fun replaceCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            setCustomAnimations(
                R.anim.slide_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out
            )
            replace(R.id.flFragment, fragment)
            addToBackStack(null)
            commit()
        }
}