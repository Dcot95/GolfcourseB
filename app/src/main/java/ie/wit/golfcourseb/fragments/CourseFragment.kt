package ie.wit.golfcourseb.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import ie.wit.golfcourseb.R
import ie.wit.golfcourseb.databinding.FragmentCourseBinding
import ie.wit.golfcourseb.main.GolfcourseBApp
import ie.wit.golfcourseb.models.GolfcourseModel

class CourseFragment : Fragment() {

    lateinit var app: GolfcourseBApp
    var totalCoursed  = 0
    private var _fragBinding: FragmentCourseBinding? = null
    private val fragBinding get() = _fragBinding!!
    //lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as GolfcourseBApp
        setHasOptionsMenu(true)
        //navController = Navigation.findNavController(activity!!, R.id.nav_host_fragment)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _fragBinding = FragmentCourseBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        activity?.title = getString(R.string.action_course)

        fragBinding.progressBar.max = 10000
        fragBinding.amountPicker.minValue = 1
        fragBinding.amountPicker.maxValue = 1000

        fragBinding.amountPicker.setOnValueChangedListener { _, _, newVal ->
            //Display the newly selected number to paymentAmount
            fragBinding.paymentAmount.setText("$newVal")
        }
        setButtonListener(fragBinding)
        return root;
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            CourseFragment().apply {
                arguments = Bundle().apply {}
            }
    }

    fun setButtonListener(layout: FragmentCourseBinding) {
        layout.courseButton.setOnClickListener {
            val amount = if (layout.paymentAmount.text.isNotEmpty())
                layout.paymentAmount.text.toString().toInt() else layout.amountPicker.value
            if(totalCoursed  >= layout.progressBar.max)
                Toast.makeText(context,"Course Amount Exceeded!",Toast.LENGTH_LONG).show()
            else {
                val paymentmethod = if(layout.paymentMethod.checkedRadioButtonId == R.id.Direct) "Direct" else "Paypal"
                totalCoursed  += amount
                layout.totalSoFar.text = getString(R.string.totalSoFar,totalCoursed )
                layout.progressBar.progress = totalCoursed
                app.golfcoursesStore.create(GolfcourseModel(paymentmethod = paymentmethod,amount = amount))
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_course, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item,
            requireView().findNavController()) || super.onOptionsItemSelected(item)
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }

    override fun onResume() {
        super.onResume()
        totalCoursed  = app.golfcoursesStore.findAll().sumOf { it.amount }
        fragBinding.progressBar.progress = totalCoursed
        fragBinding.totalSoFar.text = getString(R.string.totalSoFar,totalCoursed )
    }
}