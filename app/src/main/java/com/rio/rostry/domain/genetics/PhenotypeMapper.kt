package com.rio.rostry.domain.genetics

import com.rio.rostry.domain.model.*

/**
 * Translates a [GeneticProfile] (Genotype) into a [BirdAppearance] (Phenotype).
 * Approximation of real-world poultry genetics logic.
 */
object PhenotypeMapper {

    fun mapToAppearance(genotype: GeneticProfile, ageWeeks: Int): BirdAppearance {
        val isMale = true // Todo: determine sex from genotype? Usually passed separately or implied by Z/W. 
        // For simplicity, we might store sex in GeneticProfile or pass it. 
        // Let's assume for now we derive it or default.
        // Actually, GeneticProfile has pairs. If Z/W it's female, Z/Z male.
        // But our GeneticProfile uses pairs for everything.
        // Let's assume standard behavior: passed externally or determined by caller.
        // For this mapper, let's allow passing isMale, or default to male for visualization if unknown.
        
        // Base Template
        val base = BirdAppearance(
            isMale = isMale,
            bodySize = BodySize.MEDIUM, 
            comb = CombStyle.SINGLE
        )
        
        // 1. Determine Base Color (E Locus)
        // E > ER > eWh > e+ > eb
        val eAlleles = listOf(genotype.eLocus.first, genotype.eLocus.second)
        val dominantE = eAlleles.maxByOrNull { it.dominance } ?: AlleleE.EXTENDED
        
        var appearance = when (dominantE) {
            AlleleE.EXTENDED -> base.copy(
                chestColor = PartColor.BLACK,
                backColor = PartColor.BLACK,
                wingColor = PartColor.BLACK,
                tailColor = PartColor.GREEN_BLACK,
                chest = PlumagePattern.SOLID,
                wingPattern = PlumagePattern.SOLID
            )
            AlleleE.BIRCHEN -> base.copy(
                chestColor = PartColor.BLACK, // Will be modified by S locus (Silver/Gold chest)
                backColor = PartColor.WHITE,  // Silver Birchen default
                wingColor = PartColor.BLACK,
                tailColor = PartColor.BLACK,
                chest = PlumagePattern.LACED // Birchen often implies lacing on chest
            )
            AlleleE.DOMINANT_WHEATEN -> base.copy(
                chestColor = if (isMale) PartColor.BLACK else PartColor.WHEATEN,
                backColor = if (isMale) PartColor.RED else PartColor.WHEATEN,
                wingColor = if (isMale) PartColor.RED else PartColor.WHEATEN,
                tailColor = PartColor.BLACK
            )
            else -> base.copy( // Wild Type / Brown
                 chestColor = if (isMale) PartColor.BLACK else PartColor.BROWN,
                 backColor = if (isMale) PartColor.RED else PartColor.BROWN,
                 wingColor = if (isMale) PartColor.RED else PartColor.BROWN,
                 tailColor = PartColor.BLACK
            )
        }

        // 2. Apply Red/Silver (S Locus) - Sex Linked
        // S = Silver (White), s+ = Gold (Red)
        // Only affects non-black areas (unless E/E extended black covers everything)
        val sAlleles = listOf(genotype.sLocus.first, genotype.sLocus.second)
        val hasSilver = sAlleles.any { it == AlleleS.SILVER } // S is dominant
        
        if (hasSilver) {
            // Replace Gold/Red with Silver/White
            if (appearance.backColor == PartColor.RED || appearance.backColor == PartColor.GOLD) {
                appearance = appearance.copy(backColor = PartColor.WHITE)
            }
            if (appearance.wingColor == PartColor.RED || appearance.wingColor == PartColor.GOLD) {
                appearance = appearance.copy(wingColor = PartColor.WHITE)
            }
            if (appearance.chestColor == PartColor.RED || appearance.chestColor == PartColor.GOLD) {
                appearance = appearance.copy(chestColor = PartColor.WHITE)
            }
            // For Birchen (ER), Silver makes "Silver Birchen" (Black body, Silver neck/chest lacing)
            if (dominantE == AlleleE.BIRCHEN) {
                appearance = appearance.copy(
                    chestColor = PartColor.BLACK, // Main body black
                    // Secondary/Accent would be silver
                    // We need to set secondary colors for renderer
                    customSecondaryColor = PartColor.SILVER.color.toLong()
                )
            }
        } else {
             // Gold/Red
             if (dominantE == AlleleE.BIRCHEN) {
                appearance = appearance.copy(
                    customSecondaryColor = PartColor.GOLD.color.toLong() // Gold Birchen
                )
             }
        }

        // 3. Apply Columbian (Co) - Restriction
        // Co/Co restricts black to neck and tail.
        val coAlleles = listOf(genotype.coLocus.first, genotype.coLocus.second)
        val isColumbian = coAlleles.any { it == AlleleCo.COLUMBIAN }
        
        if (isColumbian) {
            // Override E locus darkness
             appearance = appearance.copy(
                chest = PlumagePattern.COLUMBIAN,
                chestColor = if (hasSilver) PartColor.WHITE else PartColor.BUFF,
                backColor = if (hasSilver) PartColor.WHITE else PartColor.BUFF,
                wingColor = if (hasSilver) PartColor.WHITE else PartColor.BUFF,
                tailColor = PartColor.BLACK
            )
        }

        // 4. Pattern Gene (Pg) & Melanotic (Ml) -> Lacing / Double Lacing
        val pgScore = genotype.pgLocus.toList().count { it == AllelePg.PATTERNED } // 0, 1, 2
        val mlScore = genotype.mlLocus.toList().count { it == AlleleMl.MELANOTIC } // 0, 1, 2
        
        if (pgScore > 0) {
            // Pg organizes pigment. 
            // Pg + Ml + Co -> Laced (Wyandotte)
            if (isColumbian && mlScore > 0) {
                appearance = appearance.copy(
                    chest = PlumagePattern.LACED,
                    wingPattern = PlumagePattern.LACED,
                    // Colors set by S locus logic above (White/Silver vs Gold/Buff)
                    // Secondary color determines the Rim (usually Black)
                    // In Renderer: color = center, secondary = rim
                    chestColor = if (hasSilver) PartColor.WHITE else PartColor.BUFF, // Center
                    customSecondaryColor = PartColor.BLACK.color.toLong() // Rim
                )
            } 
            // Pg + Ml (no Co) -> Double Laced (Barnevelder) / Penciled
            else if (!isColumbian && mlScore > 0) {
                 appearance = appearance.copy(
                    chest = PlumagePattern.DOUBLE_LACED,
                    wingPattern = PlumagePattern.DOUBLE_LACED,
                    chestColor = if (hasSilver) PartColor.WHITE else PartColor.MAHOGANY, 
                    customSecondaryColor = PartColor.BLACK.color.toLong()
                )
            }
        }

        // 5. Barring (B Locus)
        val bScore = genotype.bLocus.toList().count { it == AlleleB.BARRED }
        if (bScore > 0) {
            // Barring overlays everything
            appearance = appearance.copy(
                chest = PlumagePattern.BARRED,
                wingPattern = PlumagePattern.BARRED,
                tail = TailStyle.SHORT, // Often short in commercial barred
                // Barring is Black/White stripes usually
                chestColor = PartColor.BLACK,
                customSecondaryColor = PartColor.WHITE.color.toLong() 
            )
        }
        
        // 6. Mottling (Mo)
        // Recessive: mo/mo required
        val moScore = genotype.moLocus.toList().count { it == AlleleMo.MOTTLED }
        if (moScore == 2) {
             appearance = appearance.copy(
                chest = PlumagePattern.MOTTLED,
                wingPattern = PlumagePattern.MOTTLED,
                customSecondaryColor = PartColor.WHITE.color.toLong()
            )
        }
        
        // 7. Blue (Bl) - Dilution
        // Bl/bl+ = Blue, Bl/Bl = Splash
        val blScore = genotype.blLocus.toList().count { it == AlleleBl.BLUE }
        if (blScore == 1) {
            // Blue
            if (appearance.chestColor == PartColor.BLACK) appearance = appearance.copy(chestColor = PartColor.BLUE)
            if (appearance.backColor == PartColor.BLACK) appearance = appearance.copy(backColor = PartColor.BLUE)
            if (appearance.wingColor == PartColor.BLACK) appearance = appearance.copy(wingColor = PartColor.BLUE)
            if (appearance.tailColor == PartColor.BLACK) appearance = appearance.copy(tailColor = PartColor.BLUE)
            if (appearance.tailColor == PartColor.GREEN_BLACK) appearance = appearance.copy(tailColor = PartColor.BLUE)
        } else if (blScore == 2) {
            // Splash
            appearance = appearance.copy(
                chest = PlumagePattern.SPLASH,
                wingPattern = PlumagePattern.SPLASH,
                chestColor = PartColor.WHITE, // Splash is white with blue/black spots
                customSecondaryColor = PartColor.BLUE.color.toLong()
            )
        }

        // Final adjustments for gender
        // ... (e.g. sickle feathers)

        return appearance
    }
    
    // Helper to get Color Long
    private fun androidx.compose.ui.graphics.Color.toLong(): Long = value.toLong()
}

// Extension to map list to list
private fun Pair<Allele, Allele>.toList() = listOf(first, second)
