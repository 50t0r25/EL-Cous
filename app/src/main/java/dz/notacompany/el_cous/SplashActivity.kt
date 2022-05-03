package dz.notacompany.el_cous

import android.content.SharedPreferences
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_splash)

        val sharedpref: SharedPreferences =
            getApplicationContext().getSharedPreferences(
                "com.example.android.your_application",
                MODE_PRIVATE
            )

        val token: String? = sharedpref.getString("token", null)
        if (token == "False" || token == null) {
            // rest of the FirstTime Logic here

            sharedpref.edit().putString("token", "true").apply()
        } else {
            // rest of the Not-FirstTime Logic here
        }
    }
}