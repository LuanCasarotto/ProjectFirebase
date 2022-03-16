package com.example.projectfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.projectfirebase.databinding.ActivityTelaPrincipalBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class TelaPrincipal : AppCompatActivity() {

    private lateinit var binding: ActivityTelaPrincipalBinding
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTelaPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.btnDeslogar.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            telaLogin()
        }
    }

    override fun onStart() {
        super.onStart()

        val email = Firebase.auth.currentUser?.email.toString()
        val usuarioID = Firebase.auth.currentUser?.uid.toString()

        val documentReference = db.collection("Usuarios").document(usuarioID)
        documentReference.addSnapshotListener { documentSnapshot, error ->
            if (documentSnapshot != null) {
                binding.run {
                    tvNomeUsuario.text = documentSnapshot.getString("Nome")
                    tvEmailUsuario.text = email
                }
            }
        }
    }

    private fun telaLogin() {
        Intent(this, FormLogin::class.java).also {
            startActivity(it)
            finish()
        }
    }
}