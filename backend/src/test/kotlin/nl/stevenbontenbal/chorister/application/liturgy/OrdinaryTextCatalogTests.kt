package nl.stevenbontenbal.chorister.application.liturgy

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import nl.stevenbontenbal.chorister.domain.rites.MassPart
import org.junit.jupiter.api.Test

class OrdinaryTextCatalogTests {
    @Test
    fun `resolves the Latin text for an ordinary part`() {
        val gloria = OrdinaryTextCatalog.resolve(MassPart.GLORIA, "la")

        gloria?.text shouldStartWith "Gloria in excelsis Deo"
        gloria?.languageTag shouldBe "la"
    }

    @Test
    fun `falls back to Latin for an unknown language`() {
        val sanctus = OrdinaryTextCatalog.resolve(MassPart.SANCTUS, "xx-XX")

        sanctus?.text shouldStartWith "Sanctus, Sanctus, Sanctus"
        sanctus?.languageTag shouldBe "la"
    }

    @Test
    fun `resolves registered vernacular translations`() {
        OrdinaryTextCatalog.register(MassPart.KYRIE, "nl", "Heer, ontferm U over ons.\nChristus, ontferm U over ons.\nHeer, ontferm U over ons.")

        val exact = OrdinaryTextCatalog.resolve(MassPart.KYRIE, "nl")
        exact?.text shouldStartWith "Heer, ontferm"
        val regional = OrdinaryTextCatalog.resolve(MassPart.KYRIE, "nl-NL")
        regional?.text shouldStartWith "Heer, ontferm"
    }

    @Test
    fun `returns null for parts that are not ordinary texts`() {
        OrdinaryTextCatalog.resolve(MassPart.ENTRANCE, "en") shouldBe null
        OrdinaryTextCatalog.resolve(MassPart.COLLECT, "en") shouldBe null
    }

    @Test
    fun `returns null for ordinary parts without any registered text`() {
        OrdinaryTextCatalog.resolve(MassPart.PENITENTIAL_ACT, "en") shouldBe null
    }
}
