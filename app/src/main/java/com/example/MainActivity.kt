package com.example

import android.os.Bundle
import android.widget.Button
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.example.calculator.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.GoCalculator).setOnClickListener({
            val calc_Intent = Intent(this, CalculatorActivity::class.java)
            startActivity(calc_Intent)
        });

//        findViewById<Button>(R.id.GoMedia).setOnClickListener({
//            val calc_Intent = Intent(this, CalculatorActivity::class.java)
//            startActivity(calc_Intent)
//        });
//
//        findViewById<Button>(R.id.GoLocate).setOnClickListener({
//            val calc_Intent = Intent(this, CalculatorActivity::class.java)
//            startActivity(calc_Intent)
//        });
//
//        findViewById<Button>(R.id.GoMobile_connect).setOnClickListener({
//            val calc_Intent = Intent(this, CalculatorActivity::class.java)
//            startActivity(calc_Intent)
//        });
//
//        findViewById<Button>(R.id.GoSoket).setOnClickListener({
//            val calc_Intent = Intent(this, CalculatorActivity::class.java)
//            startActivity(calc_Intent)
//        });

    }
}