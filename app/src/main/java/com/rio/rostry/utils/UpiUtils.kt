package com.rio.rostry.utils

import java.net.URLEncoder

object UpiUtils {
    // Simple UPI deep link generator
    // upi://pay?pa=<vpa>&pn=<name>&am=<amount>&cu=INR&tn=<note>
    fun buildUpiUri(vpa: String, name: String, amount: Double, note: String?): String {
        val params = mutableListOf(
            "pa=" + URLEncoder.encode(vpa, "UTF-8"),
            "pn=" + URLEncoder.encode(name, "UTF-8"),
            "am=" + String.format("%.2f", amount),
            "cu=INR"
        )
        if (!note.isNullOrBlank()) params += ("tn=" + URLEncoder.encode(note, "UTF-8"))
        return "upi://pay?" + params.joinToString("&")
    }
}
