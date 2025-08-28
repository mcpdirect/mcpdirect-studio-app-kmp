//package ai.mcpdirect.studio.app.logbook
//import ai.mcpdirect.backend.dao.entity.account.AIPortAccessKeyCredential
//import ai.mcpdirect.studio.app.logbook.ToolLog
//import ai.mcpdirect.studio.app.logbook.ToolsLogRepository
//
//object FakeToolsLogDataGenerator {
//    private val tools = listOf(
//        "WingInspector", "FlapAnalyzer", "LandingGearTester",
//        "EngineDiagnostic", "FuelSystemChecker", "HydraulicMonitor",
//        "AvionicsValidator", "PressureTester", "NavigationCalibrator",
//        "CommunicationTester"
//    )
//
//    private val makers = listOf(
//        "Boeing", "Airbus", "Lockheed Martin", "Northrop Grumman",
//        "Raytheon", "General Electric", "Rolls-Royce", "Honeywell",
//        "Pratt & Whitney", "Safran"
//    )
//
//    private val agents = listOf(
//        "TechOps Alpha", "Maintenance Bravo", "Engineering Charlie",
//        "Quality Delta", "Inspection Echo", "Repair Foxtrot",
//        "Service Golf", "Support Hotel", "Diagnostics India",
//        "Testing Juliet"
//    )
//
//    private val parameterTypes = listOf(
//        "pressure" to "psi",
//        "temperature" to "°C",
//        "voltage" to "V",
//        "current" to "A",
//        "frequency" to "Hz",
//        "resistance" to "Ω",
//        "flow rate" to "L/min",
//        "vibration" to "mm/s",
//        "torque" to "Nm",
//        "rpm" to "rpm"
//    )
//
//    private val statusMessages = listOf(
//        "OK", "Warning - Minor Issue", "Error - Requires Attention",
//        "Critical - Immediate Action Needed", "Out of Spec", "Within Tolerance",
//        "Passed", "Failed", "Needs Calibration", "Component Replacement Required"
//    )
//
//    fun generateFakeLogs(count: Int): List<ToolLog> {
//        return List(count) { index ->
//            val tool = tools.random()
//            val maker = makers.random()
//            val agent = agents.random()
//            val paramsCount = (1..5).random()
//
//            ToolLog(
//                id = index.toLong(),
//                credential = null,
//                agentName = agent,
//                makerName = maker,
//                toolName = tool,
//                input = generateParameters(paramsCount),
//                output = generateOutput(),
//                timestamp = System.currentTimeMillis() - (0..30L * 24 * 60 * 60 * 1000).random()
//            )
//        }
//    }
//
//    private fun generateParameters(count: Int): Map<String, Any> {
//        return (1..count).associate {
//            val (paramName, unit) = parameterTypes.random()
//            val value = when (unit) {
//                "psi" -> (0..150).random().toDouble()
//                "°C" -> (-40..120).random().toDouble()
//                "V" -> (0..48).random().toDouble()
//                "A" -> (0..20).random().toDouble() / 10
//                "Hz" -> (0..10000).random().toDouble()
//                "Ω" -> (0..1000).random().toDouble()
//                "L/min" -> (0..50).random().toDouble()
//                "mm/s" -> (0..20).random().toDouble() / 10
//                "Nm" -> (0..500).random().toDouble()
//                else -> (0..5000).random().toDouble()
//            }
//            "$paramName ($unit)" to value
//        }
//    }
//
//    private fun generateOutput(): String {
//        val status = statusMessages.random()
//        val details = when {
//            status.contains("Warning") -> "Minor deviation detected in secondary systems"
//            status.contains("Error") -> "Primary system parameter out of acceptable range"
//            status.contains("Critical") -> "Immediate maintenance required - safety concern"
//            else -> "All parameters within normal operating ranges"
//        }
//
//        return """{
//            "status": "$status",
//            "timestamp": "${System.currentTimeMillis()}",
//            "details": "$details",
//            "recommendation": "${generateRecommendation(status)}"
//        }"""
//    }
//
//    private fun generateRecommendation(status: String): String {
//        return when {
//            status.contains("Warning") -> "Schedule inspection within 72 hours"
//            status.contains("Error") -> "Service required before next flight"
//            status.contains("Critical") -> "GROUND AIRCRAFT - Immediate action required"
//            else -> "No action required"
//        }
//    }
//
//    // For testing the ViewModel directly
//    fun populateRepository(repository: ToolsLogRepository, count: Int = 100) {
//        generateFakeLogs(count).forEach { repository.addLog(it) }
//    }
//}