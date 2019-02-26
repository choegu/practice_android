package com.choegu.practicepermission

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Telephony
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_PERMISSIONS = 1111
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_check_per.setOnClickListener {
            checkPermission()
        }

        Message.readSmsMms(applicationContext)
    }

    /**
     * 권한 체크
     */
    private fun checkPermission() {
        // 현재 권한 체크
        if (ContextCompat.checkSelfPermission(applicationContext,
                Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {

            // 추가설명 필요여부 체크 : 이 전에 한 번이라도 거부했다면 이 부분이 true 반환됨
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // 이 경우 권한 얻는 다이얼로그에 '다시 묻지 않음' 체크박스가 추가됨 (requestPermissions)
                // 따라서 이 권한이 왜 필요한지 좀 더 자세한 설명을 해주는 것이 좋음

            } else {

                // shouldShowRequestPermissionRationale false 일 때 필요한 것 있다면 진행
            }

            // 권한 요청 다이얼로그
            // REQUEST_PERMISSIONS : onRequestPermissionsResult 에서 사용하기 위한 requestCode
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_PERMISSIONS)

        } else {
            // 권한이 이미 있는 경우이므로 작업 진행

        }

    }

    /**
     * requestPermissions 이후 진행되는 콜백
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            // requestCode 에 따라 작업 진행
            REQUEST_PERMISSIONS -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 사용자가 권한 허용한 경우이므로, 작업 진행

                } else {

                    // 사용자가 권한 거부한 경우이므로, 그에 따른 시나리오 진행

                }
            }
        }
    }


}
