package ai.mcpdirect.studio.app

import ai.mcpdirect.studio.app.model.AIPortServiceResponse.Companion.ACCOUNT_EXISTED
import ai.mcpdirect.studio.app.model.AIPortServiceResponse.Companion.ACCOUNT_INCORRECT
import ai.mcpdirect.studio.app.model.AIPortServiceResponse.Companion.ACCOUNT_NOT_EXIST
import ai.mcpdirect.studio.app.model.AIPortServiceResponse.Companion.OTP_EXPIRED
import ai.mcpdirect.studio.app.model.AIPortServiceResponse.Companion.OTP_FAILED
import ai.mcpdirect.studio.app.model.AIPortServiceResponse.Companion.PASSWORD_INCORRECT
import ai.mcpdirect.studio.app.model.AIPortServiceResponse.Companion.SERVICE_ERROR
import ai.mcpdirect.studio.app.model.AIPortServiceResponse.Companion.SERVICE_FAILED
import ai.mcpdirect.studio.app.model.AIPortServiceResponse.Companion.SERVICE_NOT_FOUND
import ai.mcpdirect.studio.app.model.AIPortServiceResponse.Companion.SERVICE_SUCCESSFUL
import ai.mcpdirect.studio.app.model.AIPortServiceResponse.Companion.SERVICE_TIMEOUT
import ai.mcpdirect.studio.app.model.AIPortServiceResponse.Companion.SERVICE_UNAUTHORIZED
import ai.mcpdirect.studio.app.model.AIPortServiceResponse.Companion.SERVICE_UNAVAILABLE
import ai.mcpdirect.studio.app.model.AIPortServiceResponse.Companion.SIGN_IN_FAILED
import ai.mcpdirect.studio.app.model.AIPortServiceResponse.Companion.TEAM_NOT_EXIST
import kotlin.Int

sealed class UIState {
    object Idle : UIState()
    object Loading : UIState()
    object Success : UIState()
    data class Error(val code: Int=Int.MAX_VALUE,val message:String?=null) : UIState()

    companion object{
        fun state(code: Int=Int.MAX_VALUE,message:String?=null):UIState{
            if(code==SERVICE_SUCCESSFUL){
                return Success
            }
            if(message==null) return when(code){
                SERVICE_FAILED -> Error(code,"Service Failed")
                SERVICE_NOT_FOUND -> Error(code,"Service Not Found")
                SERVICE_UNAVAILABLE -> Error(code,"Service Unavailable")
                SERVICE_UNAUTHORIZED -> Error(code,"Service Unauthorized")
                SERVICE_TIMEOUT -> Error(code,"Service Timeout")
                SERVICE_ERROR -> Error(code,"Service Internal Error")

                //auth
                ACCOUNT_NOT_EXIST -> Error(code,"Account Not Exists")
                ACCOUNT_EXISTED -> Error(code,"Account Exists")
                PASSWORD_INCORRECT -> Error(code,"Password Incorrect")
                SIGN_IN_FAILED -> Error(code,"Sign In Failed")
                ACCOUNT_INCORRECT -> Error(code,"Account Incorrect")
                OTP_EXPIRED -> Error(code,"OTP Expired")
                OTP_FAILED -> Error(code,"OTP Failed")

                TEAM_NOT_EXIST -> Error(code,"Team Not Exists")
                else -> Error(code,"Something Wrong")
            }else return Error(code,message)
        }
    }
}