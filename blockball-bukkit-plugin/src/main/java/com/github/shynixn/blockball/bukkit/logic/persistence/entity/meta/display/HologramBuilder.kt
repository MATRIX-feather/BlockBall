package com.github.shynixn.blockball.bukkit.logic.persistence.entity.meta.display

import com.github.shynixn.blockball.api.business.enumeration.PlaceHolder
import com.github.shynixn.blockball.api.persistence.entity.basic.StorageLocation
import com.github.shynixn.blockball.api.persistence.entity.meta.display.HologramMeta
import com.github.shynixn.blockball.bukkit.logic.business.helper.YamlSerializer
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.PersistenceObject
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.basic.LocationBuilder

/**
 * Created by Shynixn 2018.
 * <p>
 * Version 1.2
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2018 by Shynixn
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
class HologramBuilder : PersistenceObject(), HologramMeta {
    /** Position of the hologram. */
    override var position: StorageLocation?
        get() = internalPosition
        set(value) {
            this.internalPosition = value as LocationBuilder
        }
    /** Lines of the hologram being rendered. */
    @YamlSerializer.YamlSerialize("lines", orderNumber = 1)
    override val lines: MutableList<String> = ArrayList();

    @YamlSerializer.YamlSerialize("location", orderNumber = 1)
    private var internalPosition: LocationBuilder? = null

    init {
        this.lines.add(PlaceHolder.RED_COLOR.placeHolder + PlaceHolder.TEAM_RED + ' ' + PlaceHolder.RED_GOALS
                + " : " + PlaceHolder.BLUE_COLOR.placeHolder + PlaceHolder.BLUE_GOALS + ' ' + PlaceHolder.TEAM_BLUE)
    }
}