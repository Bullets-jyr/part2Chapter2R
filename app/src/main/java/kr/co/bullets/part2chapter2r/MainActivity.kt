package kr.co.bullets.part2chapter2r

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kr.co.bullets.part2chapter2r.databinding.ActivityMainBinding
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    companion object {
        private const val REQUEST_RECORD_AUDIO_CODE = 200
    }

    private var recorder: MediaRecorder? = null
    private var fileName: String = ""

    // 릴리즈 -> 녹음 -> 릴리즈
    // 릴리즈 -> 재생 -> 릴리즈
    private enum class State {
        RELEASE, RECORDING, PLAYING
    }

    private var state: State = State.RELEASE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fileName = "${externalCacheDir?.absolutePath}/audioRecordTest.3gp"

        binding.recordButton.setOnClickListener {
            when (state) {
                State.RELEASE -> {
                    record()
                }

                State.RECORDING -> {
                    onRecord(false)
                }

                State.PLAYING -> {

                }
            }
        }
    }

    private fun record() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                onRecord(true)
            }
            // android 권한 시스템 팝업을 한번만 봤을 경우... (교육용 팝업)
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.RECORD_AUDIO
            ) -> {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected, and what
                // features are disabled if it's declined. In this UI, include a
                // "cancel" or "no thanks" button that lets the user continue
                // using your app without granting the permission.
                showPermissionRationaleDialog()
            }

            else -> {
                // You can directly ask for the permission.
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.RECORD_AUDIO),
                    REQUEST_RECORD_AUDIO_CODE
                )
            }
        }
    }

    private fun onRecord(start: Boolean) = if (start) {
        startRecording()
    } else {
        stopRecording()
    }

    private fun startRecording() {
        state = State.RECORDING

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            recorder = MediaRecorder(this).apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setOutputFile(fileName)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

                try {
                    prepare()
                } catch (e: IOException) {
                    Log.d("MainActivity", "prepare() failed $e")
                }

                start()
            }
        } else {
            recorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setOutputFile(fileName)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

                try {
                    prepare()
                } catch (e: IOException) {
                    Log.d("MainActivity", "prepare() failed $e")
                }

                start()
            }
        }

        binding.recordButton.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.baseline_stop_24
            )
        )
        binding.recordButton.imageTintList =
            ColorStateList.valueOf(ContextCompat.getColor(this, R.color.black))
//        binding.recordButton.imageTintList = ColorStateList.valueOf(Color.BLACK)

        binding.playButton.isEnabled = false
        binding.playButton.alpha = 0.3f
    }

    private fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
        state = State.RELEASE

        binding.recordButton.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.baseline_fiber_manual_record_24
            )
        )
        binding.recordButton.imageTintList =
            ColorStateList.valueOf(ContextCompat.getColor(this, R.color.red))
//        binding.recordButton.imageTintList = ColorStateList.valueOf(Color.BLACK)

        binding.playButton.isEnabled = true
        binding.playButton.alpha = 1.0f
    }

    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(this)
            .setMessage("녹음 권한을 허용해주셔야 앱을 정상적으로 사용할 수 있습니다.")
            .setPositiveButton("권한 허용하기") { _, _ ->
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.RECORD_AUDIO),
                    REQUEST_RECORD_AUDIO_CODE
                )
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    private fun showPermissionSettingDialog() {
        AlertDialog.Builder(this)
            .setMessage("녹음 권한을 허용해주셔야 앱을 정상적으로 사용할 수 있습니다. 앱 설정 화면으로 진입하셔서 권한을 켜주세요.")
            .setPositiveButton("권한 변경하러 가기") { _, _ ->
                navigateToAppSetting()
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    private fun navigateToAppSetting() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val audioRecordPermissionGranted =
            requestCode == REQUEST_RECORD_AUDIO_CODE && grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED
        if (audioRecordPermissionGranted) {
            // TODO: 녹음 작업 시작
            onRecord(true)
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    android.Manifest.permission.RECORD_AUDIO
                )
            ) {
                showPermissionRationaleDialog()
            } else {
                showPermissionSettingDialog()
            }
        }
    }
}