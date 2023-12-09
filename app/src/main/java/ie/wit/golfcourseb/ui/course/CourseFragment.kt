package ie.wit.golfcourseb.ui.course

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import ie.wit.golfcourseb.R
import ie.wit.golfcourseb.databinding.FragmentCourseBinding
import ie.wit.golfcourseb.models.GolfcourseModel
import ie.wit.golfcourseb.ui.played.PlayedViewModel

class CourseFragment : Fragment() {

    //lateinit var app: GolfcourseBApp
    var totalCoursed = 0
    private var _fragBinding: FragmentCourseBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val fragBinding get() = _fragBinding!!
    //lateinit var navController: NavController
    private lateinit var courseViewModel: CourseViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _fragBinding = FragmentCourseBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        activity?.title = getString(R.string.action_course)
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
        setButtonListener(fragBinding)
        return root
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

    private fun render(status: Boolean) {
        when (status) {
            true -> {
                view?.let {
                    //Uncomment this if you want to immediately return to Played
                    //findNavController().popBackStack()
                }
            }
            false -> Toast.makeText(context,getString(R.string.golfcourseError), Toast.LENGTH_LONG).show()
        }
    }

    fun setButtonListener(layout: FragmentCourseBinding) {
        layout.courseButton.setOnClickListener {
            val amount = if (layout.paymentAmount.text.isNotEmpty())
                layout.paymentAmount.text.toString().toInt() else layout.amountPicker.value
            if(totalCoursed >= layout.progressBar.max)
                Toast.makeText(context,"Course Amount Exceeded!", Toast.LENGTH_LONG).show()
            else {
                val paymentmethod = if(layout.paymentMethod.checkedRadioButtonId == R.id.Direct) "Direct" else "Paypal"
                totalCoursed += amount
                layout.totalSoFar.text = getString(R.string.totalSoFar,totalCoursed)
                layout.progressBar.progress = totalCoursed
                courseViewModel.addGolfcourse(GolfcourseModel(paymentmethod = paymentmethod,amount = amount))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }

    override fun onResume() {
        super.onResume()
        val playedViewModel = ViewModelProvider(this).get(PlayedViewModel::class.java)
        playedViewModel.observableGolfcoursesList.observe(viewLifecycleOwner, Observer {
            totalCoursed  = playedViewModel.observableGolfcoursesList.value!!.sumOf { it.amount }
        })
        fragBinding.progressBar.progress = totalCoursed
        fragBinding.totalSoFar.text = getString(R.string.totalSoFar,totalCoursed )
    }
}