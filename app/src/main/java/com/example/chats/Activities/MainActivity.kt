package com.example.chats.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.chats.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit
import com.google.firebase.auth.PhoneAuthCredential

import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.google.firebase.auth.AuthResult
import com.google.android.gms.tasks.OnCompleteListener

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var fauth: FirebaseAuth
    private var verificationId: String? = null
    private var userUid: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fauth = FirebaseAuth.getInstance()
//        if (fauth.currentUser != null) {
//            var intent = Intent(this, SetUpProfile::class.java)
//            startActivity(intent)
//            finish()
//        }
        binding.sendOtpBtn.setOnClickListener {
            if (binding.userPhone.text.equals(null)) {
                Toast.makeText(this, "Enter Valid Phone number!!", Toast.LENGTH_SHORT).show()
            } else {
                var phone = "+91" + binding.userPhone.text.toString()
                sendVerificationCode(phone)
            }
        }
        binding.signupBtn.setOnClickListener {
            if (binding.phoneOtp.text.equals(null)) {
                Toast.makeText(this, "Enter Valid Otp!!", Toast.LENGTH_SHORT).show()
            } else {
                verifyCode(binding.phoneOtp.text.toString())
            }
        }

    }

    private fun sendVerificationCode(phone: String) {
        var options: PhoneAuthOptions = PhoneAuthOptions.newBuilder(fauth).setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS).setActivity(this).setCallbacks(mCallBack).build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val mCallBack: OnVerificationStateChangedCallbacks =
        object : OnVerificationStateChangedCallbacks() {
            override fun onCodeSent(s: String, forceResendingToken: ForceResendingToken) {
                super.onCodeSent(s, forceResendingToken)
                verificationId = s
            }

            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                val code = phoneAuthCredential.smsCode
                if (code != null) {
                    binding.phoneOtp.setText(code)
                    verifyCode(code)
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }

    private fun verifyCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        signInWithCredential(credential)
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        fauth.signInWithCredential(credential)
            .addOnCompleteListener(OnCompleteListener<AuthResult?> { task ->
                if (task.isSuccessful) {
                    val i = Intent(this@MainActivity, SetUpProfile::class.java)
                    startActivity(i)
                    finish()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        task.exception!!.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}