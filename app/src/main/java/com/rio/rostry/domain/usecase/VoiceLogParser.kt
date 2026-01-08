package com.rio.rostry.domain.usecase

import com.rio.rostry.ui.farmer.QuickLogType
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Parsed result from voice input.
 */
data class VoiceLogEntry(
    val logType: QuickLogType,
    val amount: Double?,           // For feed (kg), weight (grams), expense (₹)
    val count: Int?,               // For mortality, sale counts
    val targetName: String?,       // Bird/batch name mentioned
    val notes: String?,            // Any additional notes
    val confidence: VoiceConfidence,
    val rawText: String
)

enum class VoiceConfidence {
    HIGH,    // Clear match with numbers
    MEDIUM,  // Type detected but values unclear
    LOW      // Best guess, needs confirmation
}

/**
 * Parser for converting spoken text to structured log entries.
 * 
 * Supports natural language patterns like:
 * - "Fed batch alpha 5 kilos"
 * - "Mortality 2 birds"
 * - "Weight rooster 2.5 kg"
 * - "Vaccine done for growers"
 * - "Expense 500 rupees for feed"
 */
@Singleton
class VoiceLogParser @Inject constructor() {
    
    /**
     * Parse spoken text into a structured log entry.
     */
    fun parse(spokenText: String): VoiceLogEntry? {
        val text = spokenText.lowercase().trim()
        if (text.isBlank()) return null
        
        // Try to detect log type
        val logType = detectLogType(text)
        
        // Extract numbers
        val numbers = extractNumbers(text)
        
        // Extract target name (bird/batch)
        val targetName = extractTargetName(text)
        
        // Build entry based on type
        return when (logType) {
            QuickLogType.FEED -> parseFeedLog(text, numbers, targetName)
            QuickLogType.MORTALITY -> parseMortalityLog(text, numbers, targetName)
            QuickLogType.WEIGHT -> parseWeightLog(text, numbers, targetName)
            QuickLogType.VACCINATION -> parseVaccinationLog(text, targetName)
            QuickLogType.EXPENSE -> parseExpenseLog(text, numbers)
            QuickLogType.MEDICATION -> parseMedicationLog(text, targetName)
            QuickLogType.MAINTENANCE -> parseEggLog(text, numbers)  // Eggs logged as maintenance
            QuickLogType.DEWORMING -> parseDewormingLog(text, targetName)
            QuickLogType.SANITATION -> parseSanitationLog(text)
            else -> VoiceLogEntry(
                logType = QuickLogType.MAINTENANCE,  // Default to maintenance for unrecognized
                amount = null,
                count = null,
                targetName = targetName,
                notes = text,
                confidence = VoiceConfidence.LOW,
                rawText = spokenText
            )
        }
    }
    
    private fun detectLogType(text: String): QuickLogType {
        return when {
            // Feed keywords
            text.containsAny("feed", "fed", "feeding", "kilo", "kg", "food") -> QuickLogType.FEED
            
            // Mortality keywords
            text.containsAny("mortality", "dead", "died", "death", "lost") -> QuickLogType.MORTALITY
            
            // Weight keywords
            text.containsAny("weight", "weigh", "grams", "gram", "heavy") -> QuickLogType.WEIGHT
            
            // Vaccination keywords
            text.containsAny("vaccine", "vaccination", "vaccinated", "shot", "inject") -> QuickLogType.VACCINATION
            
            // Expense keywords
            text.containsAny("expense", "spent", "cost", "rupees", "rs", "bought", "purchased") -> QuickLogType.EXPENSE
            
            // Medication keywords
            text.containsAny("medicine", "medication", "medicated", "antibiotic", "treatment") -> QuickLogType.MEDICATION
            
            // Egg keywords
            text.containsAny("egg", "eggs", "collected", "laid") -> QuickLogType.MAINTENANCE
            
            // Deworming keywords
            text.containsAny("deworm", "deworming", "dewormed", "worm") -> QuickLogType.DEWORMING
            
            // Sanitation keywords
            text.containsAny("clean", "cleaned", "sanitize", "sanitized", "wash") -> QuickLogType.SANITATION
            
            else -> QuickLogType.MAINTENANCE
        }
    }
    
    /**
     * Extract numbers from text (handles decimals and common formats).
     */
    private fun extractNumbers(text: String): List<Double> {
        val numberPattern = Regex("""(\d+\.?\d*)""")
        return numberPattern.findAll(text)
            .mapNotNull { it.value.toDoubleOrNull() }
            .toList()
    }
    
    /**
     * Extract bird/batch name from text.
     */
    private fun extractTargetName(text: String): String? {
        // Common patterns: "batch alpha", "rooster", bird names
        val batchPattern = Regex("""batch\s+(\w+)""", RegexOption.IGNORE_CASE)
        batchPattern.find(text)?.let { return it.groupValues[1] }
        
        // Bird type mentions
        val birdTypes = listOf("rooster", "hen", "cock", "pullet", "chick", "stag", "aseel", "asil")
        for (bird in birdTypes) {
            if (text.contains(bird)) return bird.replaceFirstChar { it.uppercase() }
        }
        
        // Zone mentions
        val zones = mapOf(
            "nursery" to "Nursery",
            "grower" to "Growers",
            "layer" to "Layers",
            "breeder" to "Breeders"
        )
        for ((key, value) in zones) {
            if (text.contains(key)) return value
        }
        
        return null
    }
    
    private fun parseFeedLog(text: String, numbers: List<Double>, targetName: String?): VoiceLogEntry {
        val amount = numbers.firstOrNull()
        val confidence = when {
            amount != null && amount > 0 -> VoiceConfidence.HIGH
            text.containsAny("feed", "fed") -> VoiceConfidence.MEDIUM
            else -> VoiceConfidence.LOW
        }
        
        return VoiceLogEntry(
            logType = QuickLogType.FEED,
            amount = amount,
            count = null,
            targetName = targetName,
            notes = if (amount != null) "${amount} kg feed" else null,
            confidence = confidence,
            rawText = text
        )
    }
    
    private fun parseMortalityLog(text: String, numbers: List<Double>, targetName: String?): VoiceLogEntry {
        val count = numbers.firstOrNull()?.toInt() ?: 1
        
        return VoiceLogEntry(
            logType = QuickLogType.MORTALITY,
            amount = null,
            count = count,
            targetName = targetName,
            notes = "$count bird(s) mortality",
            confidence = VoiceConfidence.HIGH,
            rawText = text
        )
    }
    
    private fun parseWeightLog(text: String, numbers: List<Double>, targetName: String?): VoiceLogEntry {
        var weight = numbers.firstOrNull()
        
        // Convert kg to grams if small number
        if (weight != null && weight < 10 && text.containsAny("kg", "kilo")) {
            weight *= 1000
        }
        
        return VoiceLogEntry(
            logType = QuickLogType.WEIGHT,
            amount = weight,
            count = null,
            targetName = targetName,
            notes = weight?.let { "${it.toInt()} grams" },
            confidence = if (weight != null) VoiceConfidence.HIGH else VoiceConfidence.MEDIUM,
            rawText = text
        )
    }
    
    private fun parseVaccinationLog(text: String, targetName: String?): VoiceLogEntry {
        return VoiceLogEntry(
            logType = QuickLogType.VACCINATION,
            amount = null,
            count = null,
            targetName = targetName,
            notes = "Vaccination completed",
            confidence = VoiceConfidence.HIGH,
            rawText = text
        )
    }
    
    private fun parseExpenseLog(text: String, numbers: List<Double>): VoiceLogEntry {
        val amount = numbers.firstOrNull()
        
        // Try to extract expense type
        val expenseType = when {
            text.containsAny("feed", "food") -> "Feed"
            text.containsAny("medicine", "medication") -> "Medicine"
            text.containsAny("vaccine") -> "Vaccine"
            else -> null
        }
        
        return VoiceLogEntry(
            logType = QuickLogType.EXPENSE,
            amount = amount,
            count = null,
            targetName = expenseType,
            notes = amount?.let { "₹${it.toInt()}" + (expenseType?.let { t -> " for $t" } ?: "") },
            confidence = if (amount != null) VoiceConfidence.HIGH else VoiceConfidence.MEDIUM,
            rawText = text
        )
    }
    
    private fun parseMedicationLog(text: String, targetName: String?): VoiceLogEntry {
        return VoiceLogEntry(
            logType = QuickLogType.MEDICATION,
            amount = null,
            count = null,
            targetName = targetName,
            notes = "Medication administered",
            confidence = VoiceConfidence.HIGH,
            rawText = text
        )
    }
    
    private fun parseEggLog(text: String, numbers: List<Double>): VoiceLogEntry {
        val count = numbers.firstOrNull()?.toInt()
        
        return VoiceLogEntry(
            logType = QuickLogType.MAINTENANCE,  // Eggs logged as maintenance
            amount = null,
            count = count,
            targetName = null,
            notes = count?.let { "$it eggs collected" },
            confidence = if (count != null) VoiceConfidence.HIGH else VoiceConfidence.MEDIUM,
            rawText = text
        )
    }
    
    private fun parseDewormingLog(text: String, targetName: String?): VoiceLogEntry {
        return VoiceLogEntry(
            logType = QuickLogType.DEWORMING,
            amount = null,
            count = null,
            targetName = targetName,
            notes = "Deworming completed",
            confidence = VoiceConfidence.HIGH,
            rawText = text
        )
    }
    
    private fun parseSanitationLog(text: String): VoiceLogEntry {
        return VoiceLogEntry(
            logType = QuickLogType.SANITATION,
            amount = null,
            count = null,
            targetName = null,
            notes = "Sanitation/cleaning done",
            confidence = VoiceConfidence.HIGH,
            rawText = text
        )
    }
    
    private fun String.containsAny(vararg keywords: String): Boolean {
        return keywords.any { this.contains(it) }
    }
}
