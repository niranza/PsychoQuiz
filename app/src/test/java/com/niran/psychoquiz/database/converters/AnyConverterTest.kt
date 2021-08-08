package com.niran.psychoquiz.database.converters

import org.junit.Before
import org.junit.Test

class AnyConverterTest {

    private lateinit var converter: AnyConverter

    @Before
    fun setUp() {
        converter = AnyConverter()
    }

    @Test
    fun `converting from any to boolean`() {
        val boolean = true
        val formattedBoolean = converter.fromAny(boolean)
        assert(converter.toAny(formattedBoolean) == boolean)
    }

    @Test
    fun `converting from any to int`() {
        val int = 14
        val formattedBoolean = converter.fromAny(int)
        assert(converter.toAny(formattedBoolean) == int)
    }

    @Test
    fun `converting from any to string`() {
        val string = "hello"
        val formattedBoolean = converter.fromAny(string)
        assert(converter.toAny(formattedBoolean) == string)
    }

    @Test
    fun `converting from any to double`() {
        val double = 53.42
        val formattedBoolean = converter.fromAny(double)
        assert(converter.toAny(formattedBoolean) == double)
    }

}