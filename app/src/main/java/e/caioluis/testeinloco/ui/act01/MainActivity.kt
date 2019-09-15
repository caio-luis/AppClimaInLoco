package e.caioluis.testeinloco.ui.act01

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.firebase.iid.FirebaseInstanceId
import com.inlocomedia.android.engagement.InLocoEngagement
import com.inlocomedia.android.engagement.InLocoEngagementOptions
import com.inlocomedia.android.engagement.request.FirebasePushProvider
import e.caioluis.testeinloco.R
import e.caioluis.testeinloco.adapter.CitiesListAdapter
import e.caioluis.testeinloco.json.City
import e.caioluis.testeinloco.ui.act02.CityInfoActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MainActivityContract.IView {

    private lateinit var context: Context
    private lateinit var googleMap: GoogleMap
    private lateinit var cityAdapter: CitiesListAdapter
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var mPresenter: MainActivityContract.IPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initVars()
        initActions()
    }

    private fun initVars() {

        context = this@MainActivity

        val bottomSheet: View = findViewById(R.id.frag_bottom_sheet)

        mapFragment = supportFragmentManager
            .findFragmentById(R.id.frag_map)
                as SupportMapFragment

        mPresenter = MainActivityPresenter(
            context,
            this,
            bottomSheet
        )
    }

    @SuppressLint("MissingPermission")
    private fun initActions() {

        if (!mPresenter.hasGpsPermission()) {

            mPresenter.requestGPSPermission()
            return
        }

        mPresenter.setBottomSheetConfigs()

        mapFragment.getMapAsync { it ->

            googleMap = it

            mPresenter.goToMyLocation(googleMap)

            googleMap.setOnMapClickListener {

                mPresenter.setMapMarker(it, googleMap)
            }
        }

        mapfrag_btn_search.setOnClickListener {

            if (!mPresenter.hasMarker())
                return@setOnClickListener

            mPresenter.startApiRequest()

        }

        bs_btn_show_list.setOnClickListener {

            mPresenter.setBottomSheetState(true)
        }

        bs_btn_close.setOnClickListener {

            mPresenter.setBottomSheetState(false)
        }

        bottom_sheet_lv_cities.setOnItemClickListener { parent, view, position, id ->

            val city = parent.getItemAtPosition(position) as City

            mPresenter.getCityData(city)
        }
    }

    override fun showToastMessage(message: String) {

        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    override fun showList(list: ArrayList<City>) {

        cityAdapter = CitiesListAdapter(
            context,
            R.layout.list_item,
            list
        )
        bottom_sheet_lv_cities.adapter = cityAdapter

        mPresenter.setBottomSheetState(true)
    }

    override fun execNav(data: City) {

        val mIntent = Intent(context, CityInfoActivity::class.java)

        mIntent.putExtra("data", data)

        startActivity(mIntent)
    }
}