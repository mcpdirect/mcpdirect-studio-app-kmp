package ai.mcpdirect.studio.app

sealed class UIState {
    object Idle : UIState()
    object Loading : UIState()
    object Success : UIState()
    data class Error(val code: Int) : UIState()
}