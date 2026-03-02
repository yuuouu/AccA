package mattecarra.accapp.djs.legacy

import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mattecarra.accapp.djs.DjsInterface
import mattecarra.accapp.djs.DjsSchedule
import mattecarra.accapp.models.Schedule
import mattecarra.accapp.utils.ShellUtils

class DjsHandler: DjsInterface {
    val SCHEDULE = """^\s*(//)?([0-9]{4}|boot) (.*)""".toRegex(RegexOption.MULTILINE)
    val ID_REGEX = """^: accaScheduleId(\d*)""".toRegex()
    val EXECUTE_ONCE_MATCH_REGEX = """: --delete""".toPattern()
    val EXECUTE_ON_BOOT_MATCH_REGEX = """: --boot""".toPattern()

    override suspend fun list(pattern: String): List<DjsSchedule> = withContext(Dispatchers.IO) {
        Shell.su("djsc --list ${ShellUtils.escape(pattern)}").exec().out.mapNotNull { line ->
            SCHEDULE.find(line)?.destructured?.let { (_, time: String, command: String) ->
                ID_REGEX.find(command)?.destructured?.component1()?.toIntOrNull()?.let { id ->
                    val executeOnce = EXECUTE_ONCE_MATCH_REGEX.matcher(command).find()
                    val executeOnBoot = EXECUTE_ON_BOOT_MATCH_REGEX.matcher(command).find()
                    DjsSchedule(
                        id,
                        !line.startsWith("//"),
                        time,
                        executeOnce,
                        executeOnBoot,
                        command
                    )
                }
            }
        }
    }

    override suspend fun append(line: String): Boolean = withContext(Dispatchers.IO) {
        Shell.su("djsc --append ${ShellUtils.escape(line)}").exec().isSuccess
    }

    override suspend fun edit(pattern: String, newLine: String): Boolean = withContext(Dispatchers.IO) {
        Shell.su("sed -i 's#.*${ShellUtils.escape(pattern)}.*#${ShellUtils.escape(newLine)}#' \$(djs-config --edit echo)").exec().isSuccess
    }

    override suspend fun delete(pattern: String): Boolean = withContext(Dispatchers.IO) {
        Shell.su("djsc --delete ${ShellUtils.escape(pattern)}").exec().isSuccess
    }

    override suspend fun stop(): Boolean = withContext(Dispatchers.IO) {
        Shell.su("djsd-stop").exec().isSuccess
    }
}