package com.stavro_xhardha.pockettreasure.ui.compass

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.stavro_xhardha.pockettreasure.R
import com.stavro_xhardha.pockettreasure.brain.viewModel
import com.stavro_xhardha.pockettreasure.ui.BaseFragment
import edu.arbelkilani.compass.CompassListener
import kotlinx.android.synthetic.main.fragment_compass.*

class CompassFragment : BaseFragment(), CompassListener {

    private val compassViewModel by viewModel {
        applicationComponent.compassViewModelFactory.create(it)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_compass, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    view.findNavController().popBackStack(R.id.homeFragment, false)
                }
            })
    }

    override fun initializeComponents() {
        qibla_compass.setListener(this)
    }

    override fun observeTheLiveData() {
        compassViewModel.rotateAnimation.observe(viewLifecycleOwner, Observer {
            qibla_compass.startAnimation(it)
        })

        compassViewModel.qiblaFound.observe(viewLifecycleOwner, Observer {
            if (it) Toast.makeText(requireActivity(), R.string.found, Toast.LENGTH_LONG).show()
        })
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        compassViewModel.observeValues(sensorEvent)
    }
}
