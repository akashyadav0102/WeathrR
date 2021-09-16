package com.example.weathrr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.geometry.SpatialReferences
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.Viewpoint
import com.esri.arcgisruntime.mapping.view.Graphic
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay
import com.esri.arcgisruntime.mapping.view.MapView
import com.esri.arcgisruntime.portal.Portal
import com.esri.arcgisruntime.portal.PortalItem
import com.esri.arcgisruntime.symbology.SimpleLineSymbol
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol
import kotlinx.android.synthetic.main.fragment_radar_screen.view.*


class RadarScreen : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var newMapView : MapView
    private var latitude = 0.00
    private var longitude = 0.00

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val radarScreenView = inflater.inflate(R.layout.fragment_radar_screen, container, false)
        val spinner = radarScreenView.layer_spinner
        val bundle = this.arguments

        if (bundle != null) {
            latitude = bundle.getDouble("LATITUDE")
        }

        if (bundle != null) {
            longitude = bundle.getDouble("LONGITUDE")
        }

        newMapView = radarScreenView.mapView

        setupMap("b8d5d260e54344c99b2061b8f565a250")
        addGraphics()

        spinner.onItemSelectedListener = this

        ArrayAdapter.createFromResource(
            requireContext(),R.array.layers_array,R.layout.layer_dropdown).also { adapter->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        return radarScreenView

    }

    private fun addGraphics() {
        val graphicsOverlay = GraphicsOverlay()
        newMapView.graphicsOverlays.add(graphicsOverlay)

        val point = Point(longitude, latitude, SpatialReferences.getWgs84())
        val simpleMarkerSymbol = SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE,
            0xff1e2ceb.toInt(), 15f)

        val blueOutlineSymbol = SimpleLineSymbol(SimpleLineSymbol.Style.SOLID,
            0xff001aff.toInt(), 2f)
        simpleMarkerSymbol.outline = blueOutlineSymbol

        val pointGraphic = Graphic(point, simpleMarkerSymbol)
        graphicsOverlay.graphics.add(pointGraphic)
    }

    private fun setupMap(ItemId : String) {

        ArcGISRuntimeEnvironment.setApiKey(getString(R.string.arcGis_api_key))

        val portal = Portal("https://arcgis.com", false)

        val portalItem = PortalItem(portal, ItemId)

        val map = ArcGISMap(portalItem)

        newMapView.map = map
        newMapView.setViewpoint(Viewpoint(latitude, longitude, 50000000.0))

    }

    override fun onPause() {
        newMapView.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        newMapView.resume()
    }

    override fun onDestroy() {
        newMapView.dispose()
        super.onDestroy()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (position) {
            0 -> {
                setupMap("b8d5d260e54344c99b2061b8f565a250")
            }
            1 -> {
                setupMap("1b71ae189f7c4015b7bf529c02ea6211")
            }
            2 -> {
                setupMap("70fe2fe348a24b2ea9814dcdb8911981")
            }
            3 -> {
                setupMap("488c4baa9ddd4b9eaf7bf9d75b3de263")
            }
            4-> {
                setupMap("8acbdff6ad204f9ea4da37c01728ade2")
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) { }

}