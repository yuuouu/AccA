package mattecarra.accapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import mattecarra.accapp.acc.Acc
import mattecarra.accapp.models.DashboardValues

class DashboardViewModel : ViewModel() {
    val dashboardValues: StateFlow<DashboardValues?> = flow {
        while (true) {
            emit(
                DashboardValues(
                    Acc.instance.getBatteryInfo(),
                    Acc.instance.isAccdRunning()
                )
            )
            delay(2000)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000L),
        initialValue = null
    )
}
