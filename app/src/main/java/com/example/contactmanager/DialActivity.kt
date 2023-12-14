package com.example.contactmanager

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.contactmanager.databinding.ActivityDialBinding
import com.example.contactmanager.databinding.ActivityMainBinding

class DialActivity : AppCompatActivity() {

    private val numList = ArrayList<String>()
    private lateinit var binding: ActivityDialBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tv1.setOnClickListener {
            val input: TextView = findViewById<TextView?>(R.id.tv1)
            typeNumbers(input.text.toString())
        }
        binding.tv2.setOnClickListener {
            val input: TextView = findViewById<TextView?>(R.id.tv2)
            typeNumbers(input.text.toString())
        }
        binding.tv3.setOnClickListener {
            val input: TextView = findViewById<TextView?>(R.id.tv3)
            typeNumbers(input.text.toString())
        }
        binding.tv4.setOnClickListener {
            val input: TextView = findViewById<TextView?>(R.id.tv4)
            typeNumbers(input.text.toString())
        }
        binding.tv5.setOnClickListener {
            val input: TextView = findViewById<TextView?>(R.id.tv5)
            typeNumbers(input.text.toString())
        }
        binding.tv6.setOnClickListener {
            val input: TextView = findViewById<TextView?>(R.id.tv6)
            typeNumbers(input.text.toString())
        }
        binding.tv7.setOnClickListener {
            val input: TextView = findViewById<TextView?>(R.id.tv7)
            typeNumbers(input.text.toString())
        }
        binding.tv8.setOnClickListener {
            val input: TextView = findViewById<TextView?>(R.id.tv8)
            typeNumbers(input.text.toString())
        }
        binding.tv9.setOnClickListener {
            val input: TextView = findViewById<TextView?>(R.id.tv9)
            typeNumbers(input.text.toString())
        }
        binding.tv0.setOnClickListener {
            val input: TextView = findViewById<TextView?>(R.id.tv0)
            typeNumbers(input.text.toString())
        }
        binding.tvStar.setOnClickListener {
            val input: TextView = findViewById<TextView?>(R.id.tvStar)
            typeNumbers(input.text.toString())
        }
        binding.tvHash.setOnClickListener {
            val input: TextView = findViewById<TextView?>(R.id.tvHash)
            typeNumbers(input.text.toString())
        }
        binding.ibDialBackspace.setOnClickListener {
            backspace()
        }
        binding.ibDialCall.setOnClickListener {
            val numberInput = callDial().toString()
            val callIntent: Intent = Uri.parse("tel:$numberInput").let { number ->
                Intent(Intent.ACTION_CALL, number)
            }
            startActivity(callIntent)
        }
        binding.ibDialReturn.setOnClickListener {
            val intent: Intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }
    fun typeNumbers(input: String) {
        if (numList.size < 17) {
            numList.add(input)
            var numberInput: String = ""

            for (number in numList) {
                numberInput += number
            }
            binding.tvDialNumber.text = numberInput
        }
    }

    fun backspace() {
        if (numList.size > 0){
            numList.removeLast()
            var numberInput: String = ""
            for (number in numList) {
                numberInput += number
            }
            binding.tvDialNumber.text = numberInput
        }
    }

    fun callDial(): String {
        if (numList.size > 0){
            var numberInput: String = ""
            for (number in numList) {
                numberInput += number
            }
            return numberInput
        }
        return "0"
    }


}