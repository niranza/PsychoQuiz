package com.niran.psychoquiz.database.converters

import org.junit.Test

class StringListConverterTest {

    @Test
    fun `formatted list of strings equals to original list of string`() {
        val converter = StringListConverter()
        val stringList = listOf(
            "first string",
            "second string",
            "third string"
        )
        val formattedString = converter.fromStringList(stringList)
        assert(converter.toStringList(formattedString) == stringList)
    }

}