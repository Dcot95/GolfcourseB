package ie.wit.golfcourseb.ui.course

import android.os.Bundle
import android.view.*
import android.widget.RatingBar
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import ie.wit.golfcourseb.R
import ie.wit.golfcourseb.databinding.FragmentCourseBinding
import ie.wit.golfcourseb.models.GolfcourseModel
import ie.wit.golfcourseb.ui.auth.LoggedInViewModel
import ie.wit.golfcourseb.ui.map.MapsViewModel
import ie.wit.golfcourseb.ui.played.PlayedViewModel
import timber.log.Timber

class CourseFragment : Fragment() {

    var totalCoursed = 0
    private var _fragBinding: FragmentCourseBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val fragBinding get() = _fragBinding!!
    private lateinit var courseViewModel: CourseViewModel
    private val playedViewModel: PlayedViewModel by activityViewModels()
    private val loggedInViewModel : LoggedInViewModel by activityViewModels()
    private val mapsViewModel: MapsViewModel by activityViewModels()
    private var currentRating: Float = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _fragBinding = FragmentCourseBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        setupMenu()
        courseViewModel = ViewModelProvider(this).get(CourseViewModel::class.java)
        courseViewModel.observableStatus.observe(viewLifecycleOwner, Observer {
                status -> status?.let { render(status) }
        })

        fragBinding.progressBar.max = 10000
        fragBinding.amountPicker.minValue = 1
        fragBinding.amountPicker.maxValue = 1000

        fragBinding.amountPicker.setOnValueChangedListener { _, _, newVal ->
            //Display the newly selected number to paymentAmount
            fragBinding.paymentAmount.setText("$newVal")
        }

        fragBinding.ratingBar.onRatingBarChangeListener = RatingBar.OnRatingBarChangeListener { _, rating, _ ->
            // Handle the rating value. You can store it in a variable or directly use it when needed
            currentRating = rating
        }
        setButtonListener(fragBinding)

        return root;
    }

    private fun render(status: Boolean) {
        when (status) {
            true -> {
                view?.let {
                    //Uncomment this if you want to immediately return to Played
                    //findNavController().popBackStack()
                }
            }
            false -> Toast.makeText(context,getString(R.string.golfcourseError),Toast.LENGTH_LONG).show()
        }
    }

    private fun setButtonListener(layout: FragmentCourseBinding) {
        layout.courseButton.setOnClickListener {
            val amount = if (layout.paymentAmount.text.isNotEmpty())
                layout.paymentAmount.text.toString().toInt() else layout.amountPicker.value
            if(totalCoursed >= layout.progressBar.max)
                Toast.makeText(context,"Course Amount Exceeded!", Toast.LENGTH_LONG).show()
            else {
                val typeOfCourse = when (layout.typeOfCourse.checkedRadioButtonId) {
                    R.id.Parkland -> "Parkland"
                    R.id.Links -> "Links"
                    R.id.Sandbelt -> "Sandbelt"
                    else -> "Stadium"
                }
                val rating = layout.ratingBar.rating
                totalCoursed += amount
                layout.totalSoFar.text = String.format(getString(R.string.totalSoFar),totalCoursed)
                layout.progressBar.progress = totalCoursed
                courseViewModel.addGolfcourse(loggedInViewModel.liveFirebaseUser,
                    GolfcourseModel(typeOfCourse = typeOfCourse, amount = amount,
                        rating =currentRating,
                        email = loggedInViewModel.liveFirebaseUser.value?.email!!,
                        latitude = mapsViewModel.currentLocation.value!!.latitude,
                        longitude = mapsViewModel.currentLocation.value!!.longitude))
            }
        }
    }

    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onPrepareMenu(menu: Menu) {
                // Handle for example visibility of menu items
            }

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_course, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Validate and handle the selected menu item
                return NavigationUI.onNavDestinationSelected(menuItem,
                    requireView().findNavController())
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }

    override fun onResume() {
        super.onResume()
        totalCoursed = playedViewModel.observableGolfcoursesList.value!!.sumOf { it.amount }
        fragBinding.progressBar.progress = totalCoursed
        fragBinding.totalSoFar.text = String.format(getString(R.string.totalSoFar),totalCoursed)
    }
}