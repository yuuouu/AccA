package mattecarra.accapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import mattecarra.accapp.CurrentUnit
import mattecarra.accapp.TemperatureUnit
import mattecarra.accapp.VoltageUnit

/**
 * A POJO for recording and reading data from 'acc -i'.
 *
 * @param name
 * @param isInputSuspend
 * @param status Status of the charger (Charging or Discharging).
 * @param health Physical health of the battery.
 * @param present
 * @param chargeType Speed of the charging (Fast or Slow).
 * @param capacity Returns current battery percentage.
 * @param chargerTemp Returns charger temperature.
 * @param chargerTempMax Returns max (reported?) charger temperature.
 * @param isInputCurrentLimited Returns if the input is current limited.
 * @param voltageNow Battery's measured voltage.
 * @param voltageMax
 * @param voltageQnovo
 * @param currentNow Battery's measured current.
 * @param currentQnovo
 * @param constantChargeCurrentMax
 * @param temperature Temperature of the battery.
 * @param technology Returns the battery technology.
 * @param isStepChargingEnabled
 * @param isSwJeitaEnabled
 * @param isTaperControlEnabled
 * @param isChargeDisabled Returns if charging is disabled by the module.
 * @param isChargeDone Returns if the device is done charging.
 * @param isParallelDisabled
 * @param setShipMode
 * @param dieHealth
 * @param rerunAicl Returns if you need to re-run AICL.
 * @param dpDm
 * @param chargeControlLimitMax
 * @param chargeControlLimit
 * @param inputCurrentMax
 * @param cycleCount Returns the number of charge cycles completed by the battery.
 */
@Parcelize
class BatteryInfo(val name: String,
                  val isInputSuspend: Boolean,
                  val status: String,
                  val health: String,
                  val present: Int,
                  val chargeType: String,
                  val capacity: Int,
                  val chargerTemp: Int,
                  val chargerTempMax: Int,
                  val isInputCurrentLimited: Boolean,
                  val voltageNow: Float,
                  val voltageMax: Int,
                  val voltageQnovo: Int,
                  val currentNow: Float,
                  val currentQnovo: Int,
                  val constantChargeCurrentMax: Int,
                  val temperature: Int,
                  val technology: String,
                  val isStepChargingEnabled: Boolean,
                  val isSwJeitaEnabled: Boolean,
                  val isTaperControlEnabled: Boolean,
                  val isChargeDisabled: Boolean,
                  val isChargeDone: Boolean,
                  val isParallelDisabled: Boolean,
                  val setShipMode: Boolean,
                  val dieHealth: String,
                  val rerunAicl: Boolean,
                  val dpDm: Boolean,
                  val chargeControlLimitMax: Int,
                  val chargeControlLimit: Int,
                  val inputCurrentMax: Int,
                  val cycleCount: Int,
                  val powerNow: Float = 0.0f): Parcelable
{
    /**
     * Returns whether the battery is charging or not.
     * @return if battery is charging.
     */
    fun isCharging(): Boolean = status == "Charging"

    fun getRawVoltageNow(): Float = voltageNow
    fun getRawCurrentNow(): Float = currentNow

    /**
     * Returns voltage now as float in V.
     * @return current battery operating voltage in V.
     */
    fun getVoltageNow(unit: VoltageUnit): Float
    {
        return if (voltageNow <= 0f) voltageNow
        else if (unit == VoltageUnit.uV) voltageNow / 1000000f
        else if (unit == VoltageUnit.V) voltageNow
        else voltageNow / 1000f // VoltageUnit.mV
    }

    fun getVoltageNow(input: VoltageUnit, output: VoltageUnit, withMeaUnit: Boolean): String
    {
        val voltageV = getVoltageNow(input)
        return if (output == VoltageUnit.V) { String.format("%.3f", voltageV) + if (withMeaUnit) " V" else "" }
        else (voltageV * 1000f).toInt().toString() + if (withMeaUnit) " mV" else ""
    }

    /**
     * Returns value for CURRENT_NOW in A.
     * @return current draw in A.
     */
    fun getCurrentNow(unit: CurrentUnit): Float
    {
        return if (unit == CurrentUnit.uA) (currentNow / 1000000f)
        else if(unit == CurrentUnit.mA) (currentNow / 1000f)
        else currentNow // CurrentUnit.A
    }

    fun getCurrentNow(input: CurrentUnit, output: CurrentUnit, positive: Boolean, withMeaUnit: Boolean): String
    {
        val currentA = getCurrentNow(input)
        val rmd = if (positive) 1 else -1
        return if (output == CurrentUnit.A) { String.format("%.3f", currentA * rmd) + if (withMeaUnit) " A" else "" }
        else (currentA * 1000f * rmd).toInt().toString() + if (withMeaUnit) " mA" else ""
    }

    //------------------------------------------------------
    // with Round to one decimal places

    fun getTemperature(unit: TemperatureUnit): Float
    {
        return if (unit == TemperatureUnit.C) temperature.toFloat()
        else {
            val tempF = temperature * 1.8 + 32
            String.format("%.1f", tempF).replace(",", ".", true).toFloat()
        }
    }

    fun getTemperature(unit: TemperatureUnit, withMeaUnit: Boolean): String
    {
        return if (unit == TemperatureUnit.C) { getTemperature(unit).toInt().toString() + if (withMeaUnit) " "+Typography.degree+"C" else "" }
        else if (unit == TemperatureUnit.F) { getTemperature(unit).toString() + if (withMeaUnit) " "+Typography.degree+"F" else "" }
        else { getTemperature(TemperatureUnit.C, withMeaUnit) +"/"+ getTemperature(TemperatureUnit.F, withMeaUnit) }
    }

    //------------------------------------------------------

}