package com.example.projectfirebase

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.projectfirebase.databinding.ActivityFormLoginBinding
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.*
import kotlinx.coroutines.*
import java.lang.Exception

class FormLogin : AppCompatActivity() {

    private lateinit var binding: ActivityFormLoginBinding
    private lateinit var mensagens: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mensagens = resources.getStringArray(R.array.mensagens_erro_sucesso_array)

        supportActionBar?.hide()

        binding.run {
            tvTelaCadastro.setOnClickListener {
                telaCadastro()
            }
            btnEntrar.setOnClickListener {
                val email = etEmail.text.toString()
                val senha = etSenha.text.toString()

                if (email.isEmpty() || senha.isEmpty()) {
                    Snackbar.make(root, mensagens[0], Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(Color.WHITE)
                        .setTextColor(Color.BLACK)
                        .show()
                } else {
                    autenTicarUsuario()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val usuarioAtual = FirebaseAuth.getInstance().currentUser

        if (usuarioAtual != null) {
            telaPrincipal()
        }
    }

    private fun autenTicarUsuario() {
        binding.run {
            val email = etEmail.text.toString()
            val senha = etSenha.text.toString()

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        pbLogin.visibility = View.VISIBLE
                        CoroutineScope(Dispatchers.Default).launch {
                            coroutineDelay()
                            telaPrincipal()
                        }
                    } else {
                        val erro = tratamentoDeErroLogin(task)
                        Snackbar.make(root, erro, Snackbar.LENGTH_SHORT)
                            .setBackgroundTint(Color.WHITE)
                            .setTextColor(Color.BLACK)
                            .show()
                    }
                }
        }
    }

    private fun tratamentoDeErroLogin(task: Task<AuthResult>): String {
        val erro = try {
            throw task.exception!!
        } catch (e: Exception) {
            "Erro ao logar usuário"
        }
        return erro
    }

    private fun telaPrincipal() {
        Intent(this, TelaPrincipal::class.java).also {
            startActivity(it)
        }
    }

    private fun telaCadastro() {
        Intent(this@FormLogin, FormCadastro::class.java).also {
            startActivity(it)
        }
    }

    private suspend fun coroutineDelay() {
        delay(3000)
    }
}

