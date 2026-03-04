package com.rio.rostry.domain.model

data class BreedingPrediction(
    val weightRange: Range<Double>,
    val heightRange: Range<Double>,
    val colorProbabilities: List<Probability<String>>,
    val likelyTraits: List<String> = emptyList()
)

data class Range<T>(val min: T, val max: T)
data class Probability<T>(val item: T, val percentage: Int)
