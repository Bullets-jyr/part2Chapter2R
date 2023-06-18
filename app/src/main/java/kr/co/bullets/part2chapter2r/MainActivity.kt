package kr.co.bullets.part2chapter2r

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kr.co.bullets.part2chapter2r.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}