import com.example.thesystem.questlogscreen.XpCategory
import com.example.thesystem.questlogscreen.getXpInfo
import org.junit.Assert
import org.junit.Test

class QuestLogUtilsTest {

    @Test
    fun getXpInfo_for_HalfHourOrLess_returns_correct_string() {
        // 1. Arrange (Set up your input)
        val category = XpCategory.HalfHourOrLess

        // 2. Act (Call the function you want to test)
        val result = getXpInfo(category)

        // 3. Assert (Verify the output)
        val expectedString = "Accomplishing a quest that is ≤ 30 minutes long gives you 21 XP"
        Assert.assertEquals(expectedString, result)

        // You can also print to the console for quick verification during development:
        println("Test: HalfHourOrLess -> Result: $result")
    }

    @Test
    fun getXpInfo_for_HourOrLess_returns_correct_string() {
        val category = XpCategory.HourOrLess
        val result = getXpInfo(category)
        val expectedString = "Accomplishing a quest that is ≤ 1 hour long gives you 42 XP"
        Assert.assertEquals(expectedString, result)
        println("Test: HourOrLess -> Result: $result")
    }
}

/*class QuestLogUtilsTest {
    @Test
    fun testGetXpInfo() {
        val category = XpCategory.HourOrLess
        val result = getXpInfo(category) // Directly call the function
        Assert.assertEquals("Expected output", result)
    }
}*/
