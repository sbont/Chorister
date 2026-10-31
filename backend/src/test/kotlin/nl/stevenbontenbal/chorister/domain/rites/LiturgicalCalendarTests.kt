package nl.stevenbontenbal.chorister.domain.rites

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.time.LocalDate

class LiturgicalCalendarTests {
    private val calendar = LiturgicalCalendar()

    @Test
    fun `computes Easter Sunday`() {
        calendar.easterSunday(2025) shouldBe LocalDate.of(2025, 4, 20)
        calendar.easterSunday(2026) shouldBe LocalDate.of(2026, 4, 5)
        calendar.easterSunday(2027) shouldBe LocalDate.of(2027, 3, 28)
    }

    @Test
    fun `computes the first Sunday of Advent`() {
        calendar.firstAdventSunday(2023) shouldBe LocalDate.of(2023, 12, 3)
        calendar.firstAdventSunday(2025) shouldBe LocalDate.of(2025, 11, 30)
        calendar.firstAdventSunday(2026) shouldBe LocalDate.of(2026, 11, 29)
    }

    @Test
    fun `resolves an Ordinary Time Sunday with Gloria, Creed and second reading`() {
        val info = calendar.resolve(LocalDate.of(2025, 9, 7))

        info.name shouldBe "Twenty-Third Sunday in Ordinary Time"
        info.season shouldBe Season.ORDINARY_TIME
        info.color shouldBe LiturgicalColor.GREEN
        info.massType shouldBe MassType.SUNDAY
        info.includesGloria shouldBe true
        info.includesCreed shouldBe true
        info.includesSecondReading shouldBe true
    }

    @Test
    fun `resolves Christ the King as the thirty-fourth Sunday`() {
        val info = calendar.resolve(LocalDate.of(2025, 11, 23))

        info.name shouldBe "Thirty-Fourth Sunday in Ordinary Time"
        info.massType shouldBe MassType.SUNDAY
    }

    @Test
    fun `resolves a Lent Sunday without Gloria`() {
        val info = calendar.resolve(LocalDate.of(2026, 3, 15))

        info.name shouldBe "Fourth Sunday of Lent"
        info.season shouldBe Season.LENT
        info.color shouldBe LiturgicalColor.PURPLE
        info.massType shouldBe MassType.SUNDAY
        info.includesGloria shouldBe false
        info.includesCreed shouldBe true
        info.includesSecondReading shouldBe true
    }

    @Test
    fun `resolves an Advent Sunday without Gloria`() {
        val info = calendar.resolve(LocalDate.of(2026, 12, 13))

        info.name shouldBe "Third Sunday of Advent"
        info.season shouldBe Season.ADVENT
        info.includesGloria shouldBe false
        info.includesCreed shouldBe true
    }

    @Test
    fun `resolves a Lent weekday without Gloria, Creed or second reading`() {
        val info = calendar.resolve(LocalDate.of(2026, 2, 24))

        info.name shouldBe "Tuesday of the First Week of Lent"
        info.season shouldBe Season.LENT
        info.massType shouldBe MassType.FERIA
        info.includesGloria shouldBe false
        info.includesCreed shouldBe false
        info.includesSecondReading shouldBe false
    }

    @Test
    fun `resolves Ash Wednesday`() {
        val info = calendar.resolve(LocalDate.of(2026, 2, 18))

        info.name shouldBe "Ash Wednesday"
        info.massType shouldBe MassType.FERIA
        info.includesGloria shouldBe false
    }

    @Test
    fun `resolves the first days of Lent as weekdays after Ash Wednesday`() {
        val thursday = calendar.resolve(LocalDate.of(2026, 2, 19))

        thursday.name shouldBe "Thursday after Ash Wednesday"
        thursday.season shouldBe Season.LENT
    }

    @Test
    fun `resolves Palm Sunday and Holy Week`() {
        val monday = calendar.resolve(LocalDate.of(2026, 3, 30))

        calendar.resolve(LocalDate.of(2026, 3, 29)).name shouldBe "Palm Sunday of the Passion of the Lord"
        monday.name shouldBe "Monday of Holy Week"
        monday.season shouldBe Season.LENT
    }

    @Test
    fun `resolves the Paschal Triduum`() {
        val holyThursday = calendar.resolve(LocalDate.of(2026, 4, 2))
        val goodFriday = calendar.resolve(LocalDate.of(2026, 4, 3))
        val holySaturday = calendar.resolve(LocalDate.of(2026, 4, 4))

        holyThursday.name shouldBe "Holy Thursday"
        holyThursday.massType shouldBe MassType.HOLY_THURSDAY
        holyThursday.includesGloria shouldBe true

        goodFriday.name shouldBe "Good Friday"
        goodFriday.massType shouldBe MassType.GOOD_FRIDAY
        goodFriday.includesGloria shouldBe false
        goodFriday.includesSecondReading shouldBe true

        holySaturday.name shouldBe "Holy Saturday"
        holySaturday.massType shouldBe MassType.EASTER_VIGIL
        holySaturday.includesGloria shouldBe true
    }

    @Test
    fun `resolves Easter and the Easter season`() {
        val easter = calendar.resolve(LocalDate.of(2026, 4, 5))
        val easterMonday = calendar.resolve(LocalDate.of(2026, 4, 6))
        val secondSunday = calendar.resolve(LocalDate.of(2026, 4, 12))

        easter.massType shouldBe MassType.SOLEMNITY
        easter.name shouldBe "Easter Sunday"
        easter.season shouldBe Season.EASTER
        easter.includesGloria shouldBe true

        easterMonday.name shouldBe "Monday of the Octave of Easter"
        secondSunday.name shouldBe "Second Sunday of Easter"
        secondSunday.includesGloria shouldBe true
    }

    @Test
    fun `resolves movable solemnities on their England and Wales dates`() {
        val ascension = calendar.resolve(LocalDate.of(2026, 5, 17))
        val pentecost = calendar.resolve(LocalDate.of(2026, 5, 24))
        val trinity = calendar.resolve(LocalDate.of(2026, 5, 31))
        val corpusChristi = calendar.resolve(LocalDate.of(2026, 6, 7))
        val sacredHeart = calendar.resolve(LocalDate.of(2026, 6, 12))

        ascension.massType shouldBe MassType.SOLEMNITY
        ascension.name shouldBe "The Ascension of the Lord"
        ascension.season shouldBe Season.EASTER
        pentecost.name shouldBe "Pentecost"
        pentecost.color shouldBe LiturgicalColor.RED
        trinity.name shouldBe "The Most Holy Trinity"

        corpusChristi.name shouldBe "The Most Holy Body and Blood of Christ"
        corpusChristi.massType shouldBe MassType.SOLEMNITY
        LocalDate.of(2026, 6, 7).dayOfWeek shouldBe java.time.DayOfWeek.SUNDAY

        sacredHeart.name shouldBe "The Most Sacred Heart of Jesus"
        sacredHeart.massType shouldBe MassType.SOLEMNITY
        LocalDate.of(2026, 6, 12).dayOfWeek shouldBe java.time.DayOfWeek.FRIDAY
    }

    @Test
    fun `resolves fixed solemnities`() {
        val allSaints = calendar.resolve(LocalDate.of(2026, 11, 1))
        val immaculateConception = calendar.resolve(LocalDate.of(2025, 12, 8))

        allSaints.massType shouldBe MassType.SOLEMNITY
        allSaints.name shouldBe "All Saints"
        allSaints.season shouldBe Season.ORDINARY_TIME
        allSaints.includesGloria shouldBe true

        immaculateConception.name shouldBe "The Immaculate Conception of the Blessed Virgin Mary"
        immaculateConception.season shouldBe Season.ADVENT
        immaculateConception.color shouldBe LiturgicalColor.WHITE
        immaculateConception.includesGloria shouldBe true
    }

    @Test
    fun `resolves the Christmas season and the Baptism of the Lord`() {
        val secondDay = calendar.resolve(LocalDate.of(2025, 12, 26))
        val baptism = calendar.resolve(LocalDate.of(2026, 1, 11))
        val firstOrdinaryMonday = calendar.resolve(LocalDate.of(2026, 1, 12))

        secondDay.season shouldBe Season.CHRISTMAS
        secondDay.massType shouldBe MassType.FERIA

        baptism.name shouldBe "The Baptism of the Lord"
        baptism.season shouldBe Season.CHRISTMAS
        baptism.massType shouldBe MassType.SUNDAY
        baptism.includesGloria shouldBe true

        firstOrdinaryMonday.season shouldBe Season.ORDINARY_TIME
        firstOrdinaryMonday.name shouldBe "Monday of the First Week in Ordinary Time"
    }

    @Test
    fun `resolves the first Sundays and weekdays of Ordinary Time`() {
        val secondSunday = calendar.resolve(LocalDate.of(2026, 1, 18))
        val mondayAfterTrinity = calendar.resolve(LocalDate.of(2025, 6, 16))

        secondSunday.name shouldBe "Second Sunday in Ordinary Time"
        secondSunday.massType shouldBe MassType.SUNDAY

        mondayAfterTrinity.name shouldBe "Monday of the Eleventh Week in Ordinary Time"
        mondayAfterTrinity.massType shouldBe MassType.FERIA
    }

    @Test
    fun `resolves the last Ordinary Time weekdays as the thirty-third and thirty-fourth week`() {
        val afterTheThirtyThirdSunday = calendar.resolve(LocalDate.of(2025, 11, 17))
        val afterChristTheKing = calendar.resolve(LocalDate.of(2025, 11, 24))

        afterTheThirtyThirdSunday.name shouldBe "Monday of the Thirty-Third Week in Ordinary Time"
        afterChristTheKing.name shouldBe "Monday of the Thirty-Fourth Week in Ordinary Time"
    }

    @Test
    fun `transfers the Epiphany to the Sunday between 2 and 8 January`() {
        val epiphany = calendar.resolve(LocalDate.of(2026, 1, 4))
        val sixthOfJanuary = calendar.resolve(LocalDate.of(2026, 1, 6))

        epiphany.massType shouldBe MassType.SOLEMNITY
        epiphany.name shouldBe "The Epiphany of the Lord"
        epiphany.season shouldBe Season.CHRISTMAS

        sixthOfJanuary.massType shouldBe MassType.FERIA
        sixthOfJanuary.season shouldBe Season.CHRISTMAS
    }

    @Test
    fun `transfers All Saints from a Saturday to the following Sunday`() {
        val transferredSunday = calendar.resolve(LocalDate.of(2025, 11, 2))
        val originalSaturday = calendar.resolve(LocalDate.of(2025, 11, 1))

        transferredSunday.massType shouldBe MassType.SOLEMNITY
        transferredSunday.name shouldBe "All Saints"
        transferredSunday.season shouldBe Season.ORDINARY_TIME

        originalSaturday.massType shouldBe MassType.FERIA
    }

    @Test
    fun `transfers Saints Peter and Paul from a Monday to the preceding Sunday`() {
        val transferredSunday = calendar.resolve(LocalDate.of(2026, 6, 28))
        val originalMonday = calendar.resolve(LocalDate.of(2026, 6, 29))

        transferredSunday.massType shouldBe MassType.SOLEMNITY
        transferredSunday.name shouldBe "Saints Peter and Paul"

        originalMonday.massType shouldBe MassType.FERIA
    }

    @Test
    fun `keeps Saint George as a solemnity`() {
        val saintGeorge = calendar.resolve(LocalDate.of(2026, 4, 23))

        saintGeorge.massType shouldBe MassType.SOLEMNITY
        saintGeorge.name shouldBe "Saint George"
    }
}
