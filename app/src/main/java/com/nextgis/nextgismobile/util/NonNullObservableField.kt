/*
 * Project:  NextGIS Mobile
 * Purpose:  Mobile GIS for Android
 * Author:   Stanislav Petriakov, becomeglory@gmail.com
 * ****************************************************************************
 * Copyright © 2018 NextGIS, info@nextgis.com
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

package com.nextgis.nextgismobile.util

import android.databinding.Observable
import android.databinding.ObservableField

// https://medium.com/meesho-tech/non-null-observablefield-in-kotlin-bd72d31ab54f
class NonNullObservableField<T : Any>(value: T, vararg dependencies: Observable) : ObservableField<T>(*dependencies) {
    init {
        set(value)
    }

    override fun get(): T = super.get()!!

    @Suppress("RedundantOverride") // Only allow non-null `value`.
    override fun set(value: T) = super.set(value)
}