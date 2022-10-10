/*
 *         Freenet.org - web application
 *         Copyright (C) 2022  Freenet Project Inc
 *
 *         This program is free software: you can redistribute it and/or modify
 *         it under the terms of the GNU Affero General Public License as
 *         published by the Free Software Foundation, either version 3 of the
 *         License, or (at your option) any later version.
 *
 *         This program is distributed in the hope that it will be useful,
 *         but WITHOUT ANY WARRANTY; without even the implied warranty of
 *         MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *         GNU Affero General Public License for more details.
 *
 *         You should have received a copy of the GNU Affero General Public License
 *         along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package org.freenet.website

import kweb.ElementCreator
import kweb.html.BodyElement
import kweb.route
import org.freenet.website.landing.landingPage
import org.freenet.website.names.connectNamesRoutes

fun ElementCreator<BodyElement>.routes() {
    route {

        path("") {
            landingPage()
        }

        connectNamesRoutes()
    }

}