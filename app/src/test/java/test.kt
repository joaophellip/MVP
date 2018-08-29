import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Test

class HelloWorld{

    @Test
    fun myFirstTest(){
        assertThat("MyTest" is String, `is`(true))
    }
}