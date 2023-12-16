package ie.wit.golfcourseb.ui.map

import android.annotation.SuppressLint
import android.app.AlertDialog
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import ie.wit.golfcourseb.R
import ie.wit.golfcourseb.models.GolfcourseModel
import ie.wit.golfcourseb.ui.auth.LoggedInViewModel
import ie.wit.golfcourseb.ui.played.PlayedViewModel
import ie.wit.golfcourseb.utils.createLoader
import ie.wit.golfcourseb.utils.hideLoader
import ie.wit.golfcourseb.utils.showLoader

class MapsFragment : Fragment() {

    private val mapsViewModel: MapsViewModel by activityViewModels()
    private val playedViewModel: PlayedViewModel by activityViewModels()
    private val loggedInViewModel : LoggedInViewModel by activityViewModels()
    lateinit var loader : AlertDialog

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->
        mapsViewModel.map = googleMap
        mapsViewModel.map.isMyLocationEnabled = true
        mapsViewModel.currentLocation.observe(viewLifecycleOwner) {
            val loc = LatLng(
                mapsViewModel.currentLocation.value!!.latitude,
                mapsViewModel.currentLocation.value!!.longitude
            )

            mapsViewModel.map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 14f))
            mapsViewModel.map.uiSettings.isZoomControlsEnabled = true
            mapsViewModel.map.uiSettings.isMyLocationButtonEnabled = true

            playedViewModel.observableGolfcoursesList.observe(
                viewLifecycleOwner
            ) { golfcourses ->
                golfcourses?.let {
                    render(golfcourses as ArrayList<GolfcourseModel>)
                    hideLoader(loader)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        loader = createLoader(requireActivity())
        setupMenu()
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun render(golfcouresList: ArrayList<GolfcourseModel>) {
        var markerColour: Float
        if (golfcouresList.isNotEmpty()) {
            mapsViewModel.map.clear()
            golfcouresList.forEach {
                markerColour = if(it.email.equals(this.playedViewModel.liveFirebaseUser.value!!.email))
                    BitmapDescriptorFactory.HUE_AZURE + 5
                else
                    BitmapDescriptorFactory.HUE_RED

                mapsViewModel.map.addMarker(
                    MarkerOptions().position(LatLng(it.latitude, it.longitude))
                        .title("${it.typeOfCourse} €${it.amount}")
                        .snippet(it.message)
                        .icon(BitmapDescriptorFactory.defaultMarker(markerColour ))
                )
            }
        }
    }

    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onPrepareMenu(menu: Menu) {
                // Handle for example visibility of menu items
            }

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_played, menu)

                val item = menu.findItem(R.id.toggleGolfcourses) as MenuItem
                item.setActionView(R.layout.togglebutton_layout)
                val toggleGolfcourses: SwitchCompat = item.actionView!!.findViewById(R.id.toggleButton)
                toggleGolfcourses.isChecked = false

                toggleGolfcourses.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) playedViewModel.loadAll()
                    else playedViewModel.load()
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Validate and handle the selected menu item
                return NavigationUI.onNavDestinationSelected(menuItem,
                    requireView().findNavController())
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onResume() {
        super.onResume()
        showLoader(loader, "Downloading Golfcourses")
        loggedInViewModel.liveFirebaseUser.observe(viewLifecycleOwner) {
                firebaseUser -> if (firebaseUser != null) {
            playedViewModel.liveFirebaseUser.value = firebaseUser
            playedViewModel.load()
        }
        }
    }
}