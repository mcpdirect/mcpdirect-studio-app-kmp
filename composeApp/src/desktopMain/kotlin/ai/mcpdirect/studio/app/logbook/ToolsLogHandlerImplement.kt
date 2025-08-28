package ai.mcpdirect.studio.app.logbook

import ai.mcpdirect.studio.handler.ToolLogHandler

class ToolsLogHandlerImplement(private val viewModel: ToolsLogViewModel) : ToolLogHandler {
    override fun log(log:ToolLogHandler.ToolLog) {
        viewModel.addLog(log);
//        repository.addLog(log)
    }
}