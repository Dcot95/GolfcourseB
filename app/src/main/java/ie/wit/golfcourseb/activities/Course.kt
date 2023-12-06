package ie.wit.golfcourseb.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import ie.wit.golfcourseb.R
import ie.wit.golfcourseb.databinding.ActivityCourseBinding
import ie.wit.golfcourseb.main.GolfcourseBApp
import ie.wit.golfcourseb.models.GolfcourseModel
import timber.log.Timber

class Course : AppCompatActivity() {

    private lateinit var courseLayout : ActivityCourseBinding
    lateinit var app: GolfcourseBApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        courseLayout = ActivityCourseBinding.inflate(layoutInflater)
        setContentView(courseLayout.root)
        app = this.application as GolfcourseBApp

        courseLayout.progressBar.max = 10000

        courseLayout.amountPicker.minValue = 1
        courseLayout.amountPicker.maxValue = 1000

        courseLayout.amountPicker.setOnValueChangedListener { _, _, newVal ->
            //Display the newly selected number to paymentAmount
            courseLayout.paymentAmount.setText("$newVal")
        }
//need to remove the totalCoursed in future but leaving it for now until i have rest of app working then will remove
        var totalCoursed = 0

        courseLayout.courseButton.setOnClickListener {
            val amount = if (courseLayout.paymentAmount.text.isNotEmpty())
                courseLayout.paymentAmount.text.toString().toInt() else courseLayout.amountPicker.value
            if(totalCoursed >= courseLayout.progressBar.max)
                Toast.makeText(applicationContext,"Course Amount Exceeded!", Toast.LENGTH_LONG).show()
            else {
                val paymentmethod = if(courseLayout.paymentMethod.checkedRadioButtonId == R.id.Direct)
                    "Direct" else "Paypal"
                totalCoursed += amount
                courseLayout.totalSoFar.text = getString(R.string.totalSoFar,totalCoursed)
                courseLayout.progressBar.progress = totalCoursed
                app.golfcoursesStore.create(GolfcourseModel(paymentmethod = paymentmethod,amount = amount))
                Timber.i("Total Coursed so far $totalCoursed")
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_course, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_played -> {
                startActivity(Intent(this, Played::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}