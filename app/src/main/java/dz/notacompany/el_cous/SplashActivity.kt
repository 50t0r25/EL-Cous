package dz.notacompany.el_cous

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_splash.*

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_ELCous_SplashActivity) // Sets theme to override splash screen theme
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Define the shared preferences
        val sharedpref: SharedPreferences =
            applicationContext.getSharedPreferences(
                "dz.notacompany.el_cous",
                MODE_PRIVATE
            )

        letsGoButton.setOnClickListener {

            // Set token to true, defining the the first launch has occurred
            sharedpref.edit().putString("token", "true").apply()

            // Start MainActivity
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
    }
}