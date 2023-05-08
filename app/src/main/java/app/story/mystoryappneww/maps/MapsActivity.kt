package app.story.mystoryappneww.maps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import app.story.mystoryappneww.R
import app.story.mystoryappneww.dataclass.Stories
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Mendapatkan data cerita yang memiliki lokasi
        val url = "https://story-api.dicoding.dev/v1/stories?location=id"
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Failed to fetch stories", e)
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()

                // Check if the response is an object instead of an array
                val jsonObject = JSONObject(body)
                if (jsonObject.has("location")) {
                    // Extract the array of stories from the object
                    val storiesArray = jsonObject.getJSONArray("location")

                    // Parse the array of stories using Gson
                    val stories =
                        Gson().fromJson(storiesArray.toString(), Array<Stories>::class.java)

                    runOnUiThread {
                        // Display the markers on the map
                        for (story in stories) {
                            val latLng = LatLng(story.location.latitude, story.location.longitude)
                            mMap.addMarker(MarkerOptions().position(latLng).title(story.title))
                        }

                        // Set the map view
                        mMap.setMinZoomPreference(10f)
                        val indonesia = LatLng(-2.548926, 118.014863)
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(indonesia))
                    }
                } else {
                    Log.e(TAG, "Invalid JSON response: $body")
                }
            }
        })
    }
    companion object {
        private const val TAG = "MapsActivity"
    }
}
