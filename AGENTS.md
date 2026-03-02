# AGENTS.md

本文档包含 AccA 项目的核心规范和架构约定，作为所有接入此代码库的 AI Agent 的系统级上下文指令（System Prompt Context）。在进行任何修改前，必须严格遵守以下工程红线和架构约定。

## 1. 核心操作与代码风格纪律 (Operational & Style Guardrails)
- **禁用自动格式化**：绝对禁止使用或触发任何自动格式化工具（Auto-formatting tools）对全局或局部文件进行重新排版。
- **谋定而后动 (强制约束)**：在对任何文件执行写入修改之前，AI **必须**先运行 `cat` 或 `grep` 命令检查目标文件，以确定精确的行号和当前的缩进层级。
- **完美融入上下文**：新增或修改的代码必须完美匹配周围现有的代码风格，包括缩进大小（严格区分空格与 Tab）、大括号的换行位置以及变量命名习惯。
- **最小化侵入**：仅对实现目标所绝对必需的代码行进行精准修改，严禁对无关逻辑进行“顺手优化”。
- **保留注释**：必须完整保留现有代码中的所有注释，除非该注释所描述的逻辑已被彻底移除。

## 2. Android 架构与安全规范 (Architecture Rules)
- **Shell 交互唯一入口**：本项目涉及所有 Root 权限和底层 Shell 的调用，**严禁**直接使用 `Shell.su("...").exec()`。必须且只能通过封装好的 `ShellExecutor` 执行。
- **防范命令注入**：任何涉及用户输入或外部变量的 Shell 参数拼接，必须通过 `ShellUtils.escape()` 进行严格的单引号包裹和转义，防止 Root 提权注入。
- **并发与线程安全**：严禁在主线程执行 I/O 或 Shell 交互。绝不允许为了适配同步方法而滥用 `runBlocking`。所有底层调用必须在 `Dispatchers.IO` 中通过 `suspend` 函数执行，或在安全的协程作用域内启动。
- **响应式状态管理**：严禁使用 `while(true)` 和 `delay()` 进行后台无脑轮询。UI 状态管理必须使用 Kotlin `StateFlow`，并结合 `viewModelScope` 与 `SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000L)` 实现生命周期感知。全面弃用 `LiveData`。
- **Intent 安全与空指针防御**：在处理跨进程 `Intent` 数据提取时，必须使用安全的类型转换（如 `as? Type`）配合 `?.let` 或 Elvis 操作符，防止抛出 `ClassCastException` 或 `NullPointerException`。

## 3. 业务背景提示 (Context)
- 本应用是基于 Android Root 权限的高级充电控制器前端。
- 极度关注应用自身在后台的资源消耗、性能瓶颈以及 Doze 休眠机制下的保活（如使用 `dumpsys deviceidle whitelist`）。
