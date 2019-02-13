package com.choegu.cognitosignin

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler
import com.amazonaws.regions.Regions
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        val TAG = MainActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val userPool = CognitoUserPool(applicationContext,
            "ap-northeast-2_63MSt3TpK",
            "7ovsn5rl3el84vvl8btm5611qs",
            null,
            Regions.AP_NORTHEAST_2)

        btn_sign_in.setOnClickListener {
            userPool.getUser(edit_id.text.toString()).getSessionInBackground(object : AuthenticationHandler {
                override fun onSuccess(cognitoUserSession: CognitoUserSession, device: CognitoDevice) {
                    Log.d(TAG, " -- Auth Success")
                    Log.d(TAG, "accessToken : " + cognitoUserSession.accessToken.jwtToken)
                    Log.d(TAG, "refreshToken : " + cognitoUserSession.refreshToken.token)
                    Log.d(TAG, "idToken : " + cognitoUserSession.idToken.jwtToken)
                    Log.d(TAG, "expiration : " + cognitoUserSession.accessToken.expiration)
                }

                override fun getAuthenticationDetails(
                    authenticationContinuation: AuthenticationContinuation, username: String) {

                    val userId = edit_id.text.toString()
                    val password = edit_password.text.toString()

                    val authenticationDetails = AuthenticationDetails(userId, password, null)
                    authenticationContinuation.setAuthenticationDetails(authenticationDetails)
                    authenticationContinuation.continueTask()
                }

                override fun getMFACode(multiFactorAuthenticationContinuation: MultiFactorAuthenticationContinuation) {
                }

                override fun onFailure(e: Exception) {
                }

                override fun authenticationChallenge(continuation: ChallengeContinuation) {
                }
            })
        }

    }


}
