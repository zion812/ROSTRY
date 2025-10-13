package com.rio.rostry.ui.scan

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QrScannerParseTest {

    @Test
    fun parse_plain_id_returns_same() {
        assertEquals("abc123", parseProductId("abc123"))
        assertEquals("XYZ-789", parseProductId("XYZ-789"))
    }

    @Test
    fun parse_scheme_prefixed_uri_extracts_id() {
        assertEquals("p42", parseProductId("rostry://product/p42"))
        assertEquals("p42", parseProductId("rostry://product/p42?foo=bar"))
        assertEquals("p42", parseProductId("rostry://product/p42#frag"))
    }

    @Test
    fun parse_trims_whitespace_and_stops_at_whitespace() {
        assertEquals("p42", parseProductId("  rostry://product/p42  "))
        assertEquals("p42", parseProductId("p42 more"))
    }

    @Test
    fun parse_empty_or_invalid_returns_input_trimmed() {
        assertEquals("", parseProductId(""))
        assertEquals("invalid://qr", parseProductId("invalid://qr"))
    }
}
