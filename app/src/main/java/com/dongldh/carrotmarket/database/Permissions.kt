package com.dongldh.carrotmarket.database

import android.Manifest
import android.content.Context
import android.widget.Toast
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import java.util.ArrayList

class Permissions(val context: Context) {
    // 위치 정보를 수집하는 퍼미션 승인 여부를 체크한다.
    fun permissionLocation() {
        // 위치정보 수집 동의 혹은 거절시 발생하는 리스너
        // ============ 후에 실제로 자신의 위치에 따라 값이 초기화 되도록 설정을 해야한다.  =============
        // ============ 로그 박아둘테니 기회가 된다면 꼭 해봐요~~ ============
        val locationPermissionListener = object: PermissionListener {
            override fun onPermissionGranted() {
                Toast.makeText(context, "지역 정보가 업데이트 된다!!", Toast.LENGTH_SHORT).show()
            }

            override fun onPermissionDenied(deniedPermissions: ArrayList<String>?) {
                Toast.makeText(context, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show()
            }
        }

        TedPermission(context)
            .setPermissionListener(locationPermissionListener)
            .setRationaleMessage("위치 정보를 수집하여 다양한 기능을 활용합니다.")
            .setDeniedMessage("If you reject permission, you cannot find your nearest location automatically\n\n" +
                    "Please turn on permission at [Setting] > [Permission]")
            .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
            .setGotoSettingButton(true)
            .setGotoSettingButtonText("설정으로 이동")
            .check()
    }


    // 저장소에 접근 가능한 권한을 주는 메서드.
    fun permissionStorage() {
        val storagePermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                Toast.makeText(context, "사진 정보 사용 가능", Toast.LENGTH_SHORT).show()
            }

            override fun onPermissionDenied(deniedPermissions: ArrayList<String>?) {
                Toast.makeText(
                    context,
                    "Permission Denied\n" + deniedPermissions.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        TedPermission(context)
            .setPermissionListener(storagePermissionListener)
            .setRationaleMessage("갤러리에 접근하여 사진과 관련된 다양한 기능을 활용합니다.")
            .setDeniedMessage(
                "If you reject permission, you cannot upload photos.\n\n" +
                        "Please turn on permission at [Setting] > [Permission]"
            )
            .setPermissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
            .setGotoSettingButton(true)
            .setGotoSettingButtonText("설정으로 이동")
            .check()
    }
}