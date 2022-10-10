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

import org.freenet.website.names.isUsernameValid
import org.freenet.website.names.isValidEmail
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class RegexTest {
    @Test
    fun basicUsername() {
        assert(isUsernameValid("JohnSmith"))
    }

    @Test
    fun usernameTooShort() {
        assertEquals(isUsernameValid("d"), false)
    }

    @Test
    fun usernameTooLong() {
        assertEquals(isUsernameValid("abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz"), false)
    }

    @Test
    fun usernameHasLegalSymbols() {
        assertEquals(
            isUsernameValid("derek_."), true,
            "A username should be allowed to contain a period and underscore")
    }

    @Test
    fun usernameHasIllegalSymbols() {
        assertEquals(
            isUsernameValid("""!@#$%^&*()-=+[]{}\|;:'",<>/? """), false,
            "A username should not be allowed to include any of these symbols")
    }

    @Test
    fun validEmailTest() {
        assertEquals(isValidEmail("whomever@gmail.com"), true, "This email should be valid")
    }

    @Test
    fun invalidEmailTest() {
        assertEquals(
            isValidEmail("whoaf*&*@alfja.s"), false,
            "This email should be read as invalid")
    }

}