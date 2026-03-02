package mattecarra.accapp.models

import mattecarra.accapp.CurrentUnit
import mattecarra.accapp.TemperatureUnit
import mattecarra.accapp.VoltageUnit
import org.junit.Assert.assertEquals
import org.junit.Test

class BatteryInfoTest {

    private fun createBatteryInfo(voltageNow: Float = 0f, currentNow: Float = 0f, temperature: Int = 0): BatteryInfo {
        return BatteryInfo(
            name = "battery",
            isInputSuspend = false,
            status = "Discharging",
            health = "Good",
            present = 1,
            chargeType = "Fast",
            capacity = 50,
            chargerTemp = 30,
            chargerTempMax = 40,
            isInputCurrentLimited = false,
            voltageNow = voltageNow,
            voltageMax = 4400,
            voltageQnovo = 0,
            currentNow = currentNow,
            currentQnovo = 0,
            constantChargeCurrentMax = 2000,
            temperature = temperature,
            technology = "Li-ion",
            isStepChargingEnabled = false,
            isSwJeitaEnabled = false,
            isTaperControlEnabled = false,
            isChargeDisabled = false,
            isChargeDone = false,
            isParallelDisabled = false,
            setShipMode = false,
            dieHealth = "Good",
            rerunAicl = false,
            dpDm = false,
            chargeControlLimitMax = 2000,
            chargeControlLimit = 2000,
            inputCurrentMax = 2000,
            cycleCount = 100
        )
    }

    @Test
    fun testGetVoltageNow() {
        // Assume voltageNow is in mV as per Acc standard
        val batteryInfo = createBatteryInfo(voltageNow = 4000f) // 4000 mV

        // Test Float outputs (always in V)
        assertEquals(4.0f, batteryInfo.getVoltageNow(VoltageUnit.mV), 0.001f)
        assertEquals(4000.0f, batteryInfo.getVoltageNow(VoltageUnit.V), 0.001f) // If input is V, it returns raw
        assertEquals(0.004f, batteryInfo.getVoltageNow(VoltageUnit.uV), 0.001f) // 4000 / 1000000

        // Test String outputs
        // If input is mV, output is V
        assertEquals("4.000 V", batteryInfo.getVoltageNow(VoltageUnit.mV, VoltageUnit.V, true))
        // If input is mV, output is mV
        assertEquals("4000 mV", batteryInfo.getVoltageNow(VoltageUnit.mV, VoltageUnit.mV, true))

        // If input is uV (raw is 4,000,000 uV)
        val batteryInfoUv = createBatteryInfo(voltageNow = 4000000f)
        assertEquals("4.000 V", batteryInfoUv.getVoltageNow(VoltageUnit.uV, VoltageUnit.V, true))
        assertEquals("4000 mV", batteryInfoUv.getVoltageNow(VoltageUnit.uV, VoltageUnit.mV, true))
    }

    @Test
    fun testGetCurrentNow() {
        // Assume currentNow is in mA
        val batteryInfo = createBatteryInfo(currentNow = -500f) // -500 mA

        // Test Float outputs (always in A)
        assertEquals(-0.5f, batteryInfo.getCurrentNow(CurrentUnit.mA), 0.001f)
        assertEquals(-500.0f, batteryInfo.getCurrentNow(CurrentUnit.A), 0.001f) // If input is A, returns raw

        // Test String outputs
        assertEquals("500 mA", batteryInfo.getCurrentNow(CurrentUnit.mA, CurrentUnit.mA, false, true))
        assertEquals("0.500 A", batteryInfo.getCurrentNow(CurrentUnit.mA, CurrentUnit.A, false, true))

        // If input is uA (raw is -500,000 uA)
        val batteryInfoUa = createBatteryInfo(currentNow = -500000f)
        assertEquals("0.500 A", batteryInfoUa.getCurrentNow(CurrentUnit.uA, CurrentUnit.A, false, true))
        assertEquals("500 mA", batteryInfoUa.getCurrentNow(CurrentUnit.uA, CurrentUnit.mA, false, true))
    }

    @Test
    fun testGetTemperature() {
        val batteryInfo = createBatteryInfo(temperature = 25) // 25 C

        assertEquals(25f, batteryInfo.getTemperature(TemperatureUnit.C), 0.001f)
        assertEquals(77f, batteryInfo.getTemperature(TemperatureUnit.F), 0.001f)

        assertEquals("25 °C", batteryInfo.getTemperature(TemperatureUnit.C, true))
        assertEquals("77.0 °F", batteryInfo.getTemperature(TemperatureUnit.F, true))
        assertEquals("25 °C/77.0 °F", batteryInfo.getTemperature(TemperatureUnit.CF, true))
    }

    @Test
    fun testZeroValues() {
        val batteryInfo = createBatteryInfo(voltageNow = 0f, currentNow = 0f, temperature = 0)

        assertEquals(0f, batteryInfo.getVoltageNow(VoltageUnit.V), 0.001f)
        assertEquals(0f, batteryInfo.getCurrentNow(CurrentUnit.mA), 0.001f)
        assertEquals(0f, batteryInfo.getTemperature(TemperatureUnit.C), 0.001f)
        assertEquals(32f, batteryInfo.getTemperature(TemperatureUnit.F), 0.001f)
    }
}
