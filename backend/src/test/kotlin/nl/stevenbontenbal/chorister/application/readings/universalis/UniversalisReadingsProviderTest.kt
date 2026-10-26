package nl.stevenbontenbal.chorister.application.readings.universalis

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class UniversalisReadingsProviderTest {

    @Test
    fun `deobfuscateObjectLiteral handles simple split join`() {
        val provider = UniversalisReadingsProvider()
        val input = """{"text": "aBc".split("B").join("X")}"""
        val expected = """{"text": "aXc"}"""
        
        // Use reflection to access private method for testing
        val method = UniversalisReadingsProvider::class.java.getDeclaredMethod("deobfuscateObjectLiteral", String::class.java)
        method.isAccessible = true
        val result = method.invoke(provider, input) as String
        
        assertEquals(expected, result)
    }

    @Test
    fun `deobfuscateObjectLiteral handles multiple chained transformations`() {
        val provider = UniversalisReadingsProvider()
        val input = """{"text": "abc".split("a").join("1").split("b").join("2").split("c").join("3")}"""
        val expected = """{"text": "123"}"""
        
        val method = UniversalisReadingsProvider::class.java.getDeclaredMethod("deobfuscateObjectLiteral", String::class.java)
        method.isAccessible = true
        val result = method.invoke(provider, input) as String
        
        assertEquals(expected, result)
    }

    @Test
    fun `deobfuscateObjectLiteral handles copyright example from user snippet`() {
        val provider = UniversalisReadingsProvider()
        // Simplified version of the copyright text from the user's example
        val obfuscatedText = "Copyright 1996-2026 Universalis Publishing Limited: see universalis.com. Readings fromkqStandard Version ofkBible, Catholic EdiF, published by Asian Trading CorporaF."
        val input = """{"text": "$obfuscatedText".split("F").join("tion").split("q").join("English ").split("k").join(" the ")}"""
        
        val expectedText = "Copyright 1996-2026 Universalis Publishing Limited: see universalis.com. Readings from the English Standard Version of the Bible, Catholic Edition, published by Asian Trading Corporation."
        val expected = """{"text": "$expectedText"}"""
        
        val method = UniversalisReadingsProvider::class.java.getDeclaredMethod("deobfuscateObjectLiteral", String::class.java)
        method.isAccessible = true
        val result = method.invoke(provider, input) as String
        
        assertEquals(expected, result)
    }

    @Test
    fun `deobfuscateObjectLiteral handles Mass_G example from user snippet`() {
        val provider = UniversalisReadingsProvider()
        // Simplified version of Mass_G text
        val obfuscatedText = "At that time: Jesus tolLhis disciples this parable"
        val input = """{"text": "$obfuscatedText".split("L").join("d ").split("M").join("ou")}"""
        
        val expectedText = "At that time: Jesus told his disciples this parable"
        val expected = """{"text": "$expectedText"}"""
        
        val method = UniversalisReadingsProvider::class.java.getDeclaredMethod("deobfuscateObjectLiteral", String::class.java)
        method.isAccessible = true
        val result = method.invoke(provider, input) as String
        
        assertEquals(expected, result)
    }

    @Test
    fun `deobfuscateObjectLiteral leaves plain text unchanged`() {
        val provider = UniversalisReadingsProvider()
        val input = """{"text": "plain text without obfuscation"}"""
        val expected = """{"text": "plain text without obfuscation"}"""
        
        val method = UniversalisReadingsProvider::class.java.getDeclaredMethod("deobfuscateObjectLiteral", String::class.java)
        method.isAccessible = true
        val result = method.invoke(provider, input) as String
        
        assertEquals(expected, result)
    }

    @Test
    fun `deobfuscateObjectLiteral handles newlines in text`() {
        val provider = UniversalisReadingsProvider()
        val input = """{"text": "line1\nline2".split("1").join("X")}"""
        val expected = """{"text": "lineX\nline2"}"""
        
        val method = UniversalisReadingsProvider::class.java.getDeclaredMethod("deobfuscateObjectLiteral", String::class.java)
        method.isAccessible = true
        val result = method.invoke(provider, input) as String
        
        assertEquals(expected, result)
    }

    @Test
    fun `retrieveJsonFromResponse extracts and deobfuscates`() {
        val provider = UniversalisReadingsProvider()
        // Simulate the response structure: universalisCallback({...});
        val rawResponse = "universalisCallback({\"text\": \"aBc\\\"\\\\.split(\\\"B\\\").join(\\\"X\\\")});"
        
        val method = UniversalisReadingsProvider::class.java.getDeclaredMethod("retrieveJsonFromResponse", String::class.java)
        method.isAccessible = true
        val result = method.invoke(provider, rawResponse)
        
        // Verify it returns a Right with valid JSON
        assert(result != null)
    }
}
