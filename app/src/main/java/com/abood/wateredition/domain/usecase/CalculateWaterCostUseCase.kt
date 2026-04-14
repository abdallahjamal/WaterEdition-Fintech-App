package com.abood.wateredition.domain.usecase

import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

/**
 * Converts volts to kWh and computes cost.
 * Formula: kWh = volts / 10  →  cost = kWh × pricePerKwh
 */
class CalculateWaterCostUseCase @Inject constructor() {
    operator fun invoke(volts: BigDecimal, pricePerKwh: BigDecimal): BigDecimal {
        val kwh = volts.divide(BigDecimal("10"), 10, RoundingMode.HALF_UP)
        return (kwh * pricePerKwh).setScale(2, RoundingMode.HALF_UP)
    }
}
