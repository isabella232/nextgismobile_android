/*
 * Project:  NextGIS Mobile
 * Purpose:  Mobile GIS for Android
 * Author:   Stanislav Petriakov, becomeglory@gmail.com
 * ****************************************************************************
 * Copyright © 2019 NextGIS, info@nextgis.com
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

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nextgis.maplib.API
import com.nextgis.maplib.Geometry
import com.nextgis.nextgismobile.R
import com.nextgis.nextgismobile.adapter.*
import com.nextgis.nextgismobile.data.Field
import com.nextgis.nextgismobile.databinding.ActivityNewLayerBinding
import com.nextgis.nextgismobile.fragment.AddFieldDialog
import com.nextgis.nextgismobile.util.setup
import com.nextgis.nextgismobile.util.tint
import com.nextgis.nextgismobile.viewmodel.LayerViewModel
import com.nextgis.nextgismobile.viewmodel.MapViewModel
import com.pawegio.kandroid.toast
import kotlinx.android.synthetic.main.activity_new_layer.*

class NewEmptyLayerActivity : AppCompatActivity(), OnFieldClickListener {
    override fun onEditClick(field: Field) {
        AddFieldDialog().show(this, field)
    }

    override fun onDeleteClick(field: Field) {
        binding.model?.deleteField(field)
        binding.list.adapter?.notifyDataSetChanged()
    }

    private lateinit var binding: ActivityNewLayerBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_layer)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val layerModel = ViewModelProviders.of(this).get(LayerViewModel::class.java)

        binding.apply {
            model = layerModel
            activity = this@NewEmptyLayerActivity

            val entries = resources.getStringArray(R.array.geometry_type)
            val values = resources.getStringArray(R.array.geometry_type_value)
            val adapter = DropdownAdapter(this@NewEmptyLayerActivity, R.layout.item_dropdown, entries)
            type.setAdapter(adapter)

            type.setText(entries[0])
            layerModel.vectorLayer.geometryType = Geometry.Type.POINT
            type.setOnItemClickListener { _, _, position, _ -> layerModel.vectorLayer.geometryType = Geometry.Type.from(values[position].toInt()) }
            type.setup()

            layerModel.fields.observe(this@NewEmptyLayerActivity, Observer { fields ->
                fields?.let {
                    (list.adapter as? FieldsAdapter)?.items?.clear()
                    (list.adapter as? FieldsAdapter)?.items?.addAll(fields)
                    list.adapter?.notifyDataSetChanged()
                }
            })

            list.adapter = FieldsAdapter(arrayListOf(), this@NewEmptyLayerActivity)
            list.layoutManager = LinearLayoutManager(this@NewEmptyLayerActivity, RecyclerView.VERTICAL, false)
            layerModel.init()

            fab.tint(R.color.white)
        }

        binding.executePendingBindings()
    }

    fun save() {
        API.getStore()?.let { store ->
            val options = mapOf(
                "CREATE_OVERVIEWS" to "ON",
                "ZOOM_LEVELS" to "2,3,4,5,6,7,8,9,10,11,12,13,14"
            )

            binding.model?.let {
                val fields = arrayListOf<com.nextgis.maplib.Field>()
                for (field in it.fields.value!!) {
                    fields.add(com.nextgis.maplib.Field(field.name, field.alias, field.type, field.def))
                }

                val newLayer = store.createFeatureClass(it.vectorLayer.title, it.vectorLayer.geometryType, fields, options)
                if (newLayer != null) {
                    val mapModel = ViewModelProviders.of(this).get(MapViewModel::class.java)
                    val map = mapModel.load()
                    map?.addLayer(it.vectorLayer.title, newLayer)
                    map?.save()?.let { success ->
                        if (success) {
                            setResult(Activity.RESULT_OK)
                            finish()
                        } else {
                            toast(R.string.not_implemented)
                        }
                    }
                } else {
                    toast(R.string.not_implemented)
                }
            }
        }
    }

    fun addField() {
        AddFieldDialog().show(this)
    }

    fun addField(field: Field, change: Boolean = false) {
        if (!change)
            binding.model?.addField(field)
        binding.list.adapter?.notifyDataSetChanged()
    }
}