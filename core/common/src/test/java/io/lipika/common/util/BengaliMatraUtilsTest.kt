package io.lipika.common.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BengaliMatraUtilsTest {

    @Test
    fun testAllBengaliCombiningCharsAreIdentified() {
        // Dependent vowel signs (Kars)
        assertTrue(BengaliMatraUtils.isBengaliCombiningChar('\u09BE')) // া
        assertTrue(BengaliMatraUtils.isBengaliCombiningChar('\u09BF')) // ি
        assertTrue(BengaliMatraUtils.isBengaliCombiningChar('\u09C0')) // ী
        assertTrue(BengaliMatraUtils.isBengaliCombiningChar('\u09C1')) // ু
        assertTrue(BengaliMatraUtils.isBengaliCombiningChar('\u09C2')) // ূ
        assertTrue(BengaliMatraUtils.isBengaliCombiningChar('\u09C3')) // ৃ
        assertTrue(BengaliMatraUtils.isBengaliCombiningChar('\u09C4')) // ৄ
        assertTrue(BengaliMatraUtils.isBengaliCombiningChar('\u09C7')) // ে
        assertTrue(BengaliMatraUtils.isBengaliCombiningChar('\u09C8')) // ৈ
        assertTrue(BengaliMatraUtils.isBengaliCombiningChar('\u09CB')) // ো
        assertTrue(BengaliMatraUtils.isBengaliCombiningChar('\u09CC')) // ৌ
        assertTrue(BengaliMatraUtils.isBengaliCombiningChar('\u09CD')) // ্ Hasanta
        assertTrue(BengaliMatraUtils.isBengaliCombiningChar('\u09D7')) // ৗ Au length mark
        assertTrue(BengaliMatraUtils.isBengaliCombiningChar('\u09E2')) // ৢ
        assertTrue(BengaliMatraUtils.isBengaliCombiningChar('\u09E3')) // ৣ

        // Diacritics
        assertTrue(BengaliMatraUtils.isBengaliCombiningChar('\u0981')) // ঁ Chandrabindu
        assertTrue(BengaliMatraUtils.isBengaliCombiningChar('\u0982')) // ং Anusvara
        assertTrue(BengaliMatraUtils.isBengaliCombiningChar('\u0983')) // ঃ Visarga
        assertTrue(BengaliMatraUtils.isBengaliCombiningChar('\u09BC')) // ় Nukta
    }

    @Test
    fun testIndependentVowelsAndConsonantsAreNotCombiningChars() {
        assertFalse(BengaliMatraUtils.isBengaliCombiningChar('অ'))
        assertFalse(BengaliMatraUtils.isBengaliCombiningChar('আ'))
        assertFalse(BengaliMatraUtils.isBengaliCombiningChar('ই'))
        assertFalse(BengaliMatraUtils.isBengaliCombiningChar('ক'))
        assertFalse(BengaliMatraUtils.isBengaliCombiningChar('খ'))
        assertFalse(BengaliMatraUtils.isBengaliCombiningChar('গ'))
        assertFalse(BengaliMatraUtils.isBengaliCombiningChar('ৎ'))
        assertFalse(BengaliMatraUtils.isBengaliCombiningChar('A'))
        assertFalse(BengaliMatraUtils.isBengaliCombiningChar('1'))
    }

    @Test
    fun testIsBengaliCombiningMark() {
        assertTrue(BengaliMatraUtils.isBengaliCombiningMark("া"))
        assertTrue(BengaliMatraUtils.isBengaliCombiningMark("ি"))
        assertTrue(BengaliMatraUtils.isBengaliCombiningMark("ী"))
        assertTrue(BengaliMatraUtils.isBengaliCombiningMark("ু"))
        assertTrue(BengaliMatraUtils.isBengaliCombiningMark("ূ"))
        assertTrue(BengaliMatraUtils.isBengaliCombiningMark("ৃ"))
        assertTrue(BengaliMatraUtils.isBengaliCombiningMark("ে"))
        assertTrue(BengaliMatraUtils.isBengaliCombiningMark("ৈ"))
        assertTrue(BengaliMatraUtils.isBengaliCombiningMark("ো"))
        assertTrue(BengaliMatraUtils.isBengaliCombiningMark("ৌ"))
        assertTrue(BengaliMatraUtils.isBengaliCombiningMark("্"))
        assertTrue(BengaliMatraUtils.isBengaliCombiningMark("ঁ"))
        assertTrue(BengaliMatraUtils.isBengaliCombiningMark("ং"))
        assertTrue(BengaliMatraUtils.isBengaliCombiningMark("ঃ"))
        assertTrue(BengaliMatraUtils.isBengaliCombiningMark("়"))

        // Length != 1 or non-combining characters
        assertFalse(BengaliMatraUtils.isBengaliCombiningMark("ক"))
        assertFalse(BengaliMatraUtils.isBengaliCombiningMark("কা"))
        assertFalse(BengaliMatraUtils.isBengaliCombiningMark("a"))
        assertFalse(BengaliMatraUtils.isBengaliCombiningMark(""))
        assertFalse(BengaliMatraUtils.isBengaliCombiningMark(null))
    }

    @Test
    fun testStandaloneBengaliCombiningMarks() {
        assertTrue(BengaliMatraUtils.isStandaloneBengaliCombiningMark("া"))
        assertTrue(BengaliMatraUtils.isStandaloneBengaliCombiningMark("ি"))
        assertTrue(BengaliMatraUtils.isStandaloneBengaliCombiningMark("ী"))
        assertTrue(BengaliMatraUtils.isStandaloneBengaliCombiningMark("ু"))
        assertTrue(BengaliMatraUtils.isStandaloneBengaliCombiningMark("ূ"))
        assertTrue(BengaliMatraUtils.isStandaloneBengaliCombiningMark("ৃ"))
        assertTrue(BengaliMatraUtils.isStandaloneBengaliCombiningMark("ে"))
        assertTrue(BengaliMatraUtils.isStandaloneBengaliCombiningMark("ৈ"))
        assertTrue(BengaliMatraUtils.isStandaloneBengaliCombiningMark("ো"))
        assertTrue(BengaliMatraUtils.isStandaloneBengaliCombiningMark("ৌ"))
        assertTrue(BengaliMatraUtils.isStandaloneBengaliCombiningMark("্"))
        assertTrue(BengaliMatraUtils.isStandaloneBengaliCombiningMark("ঁ"))
        assertTrue(BengaliMatraUtils.isStandaloneBengaliCombiningMark("ং"))
        assertTrue(BengaliMatraUtils.isStandaloneBengaliCombiningMark("ঃ"))

        // Multiple marks e.g. Hasanta or combined without consonant
        assertTrue(BengaliMatraUtils.isStandaloneBengaliCombiningMark("\u09C7\u09BE")) // ো decomposed

        // Words with consonants are NOT standalone combining marks
        assertFalse(BengaliMatraUtils.isStandaloneBengaliCombiningMark("কা"))
        assertFalse(BengaliMatraUtils.isStandaloneBengaliCombiningMark("কি"))
        assertFalse(BengaliMatraUtils.isStandaloneBengaliCombiningMark("আমি"))
        assertFalse(BengaliMatraUtils.isStandaloneBengaliCombiningMark("ক"))
        assertFalse(BengaliMatraUtils.isStandaloneBengaliCombiningMark("abc"))
        assertFalse(BengaliMatraUtils.isStandaloneBengaliCombiningMark("?"))
        assertFalse(BengaliMatraUtils.isStandaloneBengaliCombiningMark(" "))
        assertFalse(BengaliMatraUtils.isStandaloneBengaliCombiningMark(""))
        assertFalse(BengaliMatraUtils.isStandaloneBengaliCombiningMark(null))
    }

    @Test
    fun testDisplayLabelHelper() {
        assertEquals("\u00A0া", BengaliMatraUtils.getDisplayLabel("া"))
        assertEquals("\u00A0ি", BengaliMatraUtils.getDisplayLabel("ি"))
        assertEquals("ক", BengaliMatraUtils.getDisplayLabel("ক"))
        assertEquals("A", BengaliMatraUtils.getDisplayLabel("A"))
    }
}
