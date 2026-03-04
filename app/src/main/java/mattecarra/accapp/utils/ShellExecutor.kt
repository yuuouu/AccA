package mattecarra.accapp.utils

import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

object ShellExecutor {
    private val mutex = Mutex()

    suspend fun execute(command: String): Shell.Result = withContext(Dispatchers.IO) {
        mutex.withLock {
            Shell.getShell().newJob().add(command).exec()
        }
    }
}
