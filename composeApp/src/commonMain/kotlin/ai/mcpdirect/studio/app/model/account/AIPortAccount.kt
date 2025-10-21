package ai.mcpdirect.studio.app.model.account

import kotlinx.serialization.Serializable

@Serializable
data class AIPortAccount(
    var id: Long = 0,
    var account: String = "",
    var status: Int = 0
) {
    constructor() : this(0, "", 0)
    
    constructor(id: Long) : this(id, "", 0)
    
    constructor(account: String, status: Int) : this(0, account, status)
    
    companion object {
        const val ACCOUNT_NOT_EXIST = 0x10000
        const val ACCOUNT_EXISTED = 0x10001
        
        const val PASSWORD_INCORRECT = 0x10100
        
        const val SIGN_IN_FAILED = 0x10200
        const val PASSWORD = 0
        const val ACCESS_KEY = 1
        const val ECC_SIGNATURE = 2
        
        const val GOOGLE_ID_TOKEN = 10000
    }
}