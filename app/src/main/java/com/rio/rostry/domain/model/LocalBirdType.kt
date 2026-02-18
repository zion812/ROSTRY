package com.rio.rostry.domain.model

/**
 * Enum defining local Andhra/Telangana bird color types.
 * Maps traditional knowledge to standardized codes.
 */
enum class LocalBirdType(
    val code: String,
    val typeName: String,
    val teluguName: String,
    val description: String,
    val baseColorHex: Long // Representative color for UI
) {
    KAKI("L01", "Kaki", "కాకి", "Completely black feathers.", 0xFF212121),
    SETHU("L02", "Sethu", "సేతు", "Entirely white feathers.", 0xFFFAFAFA),
    DEGA("L03", "Dega", "డేగ", "Completely red feathers (Eagle-colored).", 0xFFD32F2F),
    SAVALA("L04", "Savala", "సవల", "Black feathers specifically on the neck.", 0xFF455A64),
    PARLA("L05", "Parla", "పర్ల", "Black and white evenly distributed.", 0xFF9E9E9E),
    KOKKIRAYI("L06", "Kokkirayi", "కొక్కిరాయి", "Black body with 2-3 different colored feathers.", 0xFF37474F),
    NEMALI("L07", "Nemali", "నెమలి", "Yellow feathers on wings/back (Peacock-colored).", 0xFFFBC02D),
    KOWJU("L08", "Kowju", "కౌజు", "Tri-colored: Black, Red, Yellow mix.", 0xFF795548),
    MAILA("L09", "Maila", "మైల", "Mix of red and ash/grey feathers.", 0xFF8D6E63),
    POOLA("L10", "Poola", "పూల", "Each feather blends black, white, and red.", 0xFFBCAAA4),
    PINGALA("L11", "Pingala", "పింగళ", "Wings mostly white with black/brown patches.", 0xFFFFF9C4),
    NALLA_BORA("L12", "Nalla Bora", "నల్ల బోర", "Black-breasted (often with red spots).", 0xFF263238),
    MUNGISA("L13", "Mungisa", "ముంగిస", "Feathers resemble mongoose fur.", 0xFF5D4037),
    ABRASU("L14", "Abrasu", "అబ్రాసు", "Light golden color feathers.", 0xFFFFECB3),
    GERUVA("L15", "Geruva", "గేరువ", "Combination of white and light red.", 0xFFFFCDD2),
    
    // Fallback
    UNKNOWN("L00", "Unknown", "తెలియదు", "Unclassified color type.", 0xFFE0E0E0);

    companion object {
        fun fromCode(code: String): LocalBirdType = entries.find { it.code == code } ?: UNKNOWN
    }
}
