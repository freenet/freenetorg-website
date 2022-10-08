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