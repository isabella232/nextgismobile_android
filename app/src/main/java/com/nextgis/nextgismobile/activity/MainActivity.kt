/*
 * Project:  NextGIS Mobile
 * Purpose:  Mobile GIS for Android
 * Author:   Stanislav Petriakov, becomeglory@gmail.com
 * ****************************************************************************
 * Copyright © 2018-2019 NextGIS, info@nextgis.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.nextgis.nextgismobile.activity

import android.content.res.ColorStateList
import android.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.view.Menu
import android.view.MenuItem
import com.nextgis.maplib.API
import com.nextgis.maplib.Envelope
import com.nextgis.maplib.GestureDelegate
import com.nextgis.maplib.MapDocument
import com.nextgis.nextgismobile.R
import com.nextgis.nextgismobile.databinding.ActivityMainBinding
import com.pawegio.kandroid.getColorCompat
import com.pawegio.kandroid.startActivity
import com.pawegio.kandroid.toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import android.view.View
import android.view.WindowManager


class MainActivity : BaseActivity(), GestureDelegate {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(bottomBar)

        initMap()
        binding.activity = this
        binding.executePendingBindings()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            fab.backgroundTintList = ColorStateList.valueOf(getColorCompat(R.color.colorButton))
//        else
//            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//        getWindow().setStatusBarColor(getColor(R.color.whiteAlpha));
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
//            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);


        fab.setOnClickListener {
            Snackbar.make(coordinator, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).apply {
                    view.layoutParams = (view.layoutParams as CoordinatorLayout.LayoutParams).apply {
                        setMargins(leftMargin, topMargin, rightMargin, bottomBar.height + fab.height / 4 * 3)
                    }
                }.show()
        }
    }

    private fun initMap() {
        API.init(this@MainActivity)
        val map = API.getMap("main")
        map?.let {
            it.setExtentLimits(
                MIN_X,
                MIN_Y,
                MAX_X,
                MAX_Y
            )
            mapView.setMap(it)
            addOSMTo(it)
//            it.save()
        }
        mapView.registerGestureRecognizers(this)
        mapView.freeze = false
    }

    private fun addOSMTo(map: MapDocument) {
        val dataDir = API.getDataDirectory()
        if (dataDir != null) {
            val bbox = Envelope(
                MIN_X,
                MAX_X,
                MIN_Y,
                MAX_Y
            )
            val baseMap = dataDir.createTMS(
                "osm.wconn",
                OSM_URL, 3857, 0, 18, bbox, bbox, 14
            )
            map.addLayer("OSM", baseMap!!)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity<SettingsActivity>()
                true
            }
            R.id.action_null -> {
                toast(R.string.not_implemented)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun layers() {
        toast(R.string.not_implemented)
    }

    fun search() {
        toast(R.string.not_implemented)
    }

    companion object {
        const val OSM_URL = "http://tile.openstreetmap.org/{z}/{x}/{y}.png"
        const val MAX_X = 20037508.34
        const val MIN_X = -MAX_X
        const val MAX_Y = 20037508.34
        const val MIN_Y = -MAX_Y
    }
}
