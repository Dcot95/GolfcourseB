package ie.wit.golfcourseb.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import ie.wit.golfcourseb.R
import ie.wit.golfcourseb.adapters.GolfcourseAdapter
import ie.wit.golfcourseb.databinding.ActivityCourseBinding
import ie.wit.golfcourseb.databinding.ActivityPlayedBinding
import ie.wit.golfcourseb.main.GolfcourseBApp

class Played : AppCompatActivity() {

    lateinit var app: GolfcourseBApp
    lateinit var playedLayout : ActivityPlayedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playedLayout = ActivityPlayedBinding.inflate(layoutInflater)
        setContentView(playedLayout.root)

        app = this.application as GolfcourseBApp
        playedLayout.recyclerView.layoutManager = LinearLayoutManager(this)
        playedLayout.recyclerView.adapter = GolfcourseAdapter(app.golfcoursesStore.findAll())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_played, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_course -> { startActivity(
                Intent(this,
                    Course::class.java)
            )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}