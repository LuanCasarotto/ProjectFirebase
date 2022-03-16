package com.example.projectfirebase

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.projectfirebase.databinding.ActivityFormCadastroBinding
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.lang.Exception

class FormCadastro : AppCompatActivity() {

    private lateinit var binding: ActivityFormCadastroBinding
    private lateinit var mensagens: Array<String>
    private lateinit var usuarioId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mensagens = resources.getStringArray(R.array.mensagens_erro_sucesso_array)

        supportActionBar?.hide()

        binding.run {
            btnCadastrar.setOnClickListener {

                val nome = etNomeCadastro.text.toString()
                val email = etEmailCadastro.text.toString()
                val senha = etSenhaCadastro.text.toString()

                if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                    Snackbar.make(
                        root,
                        mensagens[0],
                        Snackbar.LENGTH_SHORT
                    )
                        .setBackgroundTint(Color.WHITE)
                        .setTextColor(Color.BLACK)
                        .show()
                } else {
                    cadastrarUsuario()
                }
            }
        }
    }

    private fun cadastrarUsuario() {
        binding.run {
            val email = etEmailCadastro.text.toString().trim()
            val senha = etSenhaCadastro.text.toString().trim()

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        salvarDadosUsuario()
                        Snackbar.make(root, mensagens[1], Snackbar.LENGTH_SHORT)
                            .setBackgroundTint(Color.WHITE)
                            .setTextColor(Color.BLACK)
                            .show()
                    } else {
                        val erro = tratamentoDeErroCadastro(task)
                        Snackbar.make(root, erro, Snackbar.LENGTH_SHORT)
                            .setBackgroundTint(Color.WHITE)
                            .setTextColor(Color.BLACK)
                            .show()
                    }
                }
        }
    }

    private fun salvarDadosUsuario() {
        val nome = binding.etNomeCadastro.text.toString()
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()

        val usuarios = mutableMapOf<String, Any>()
        usuarios.put("Nome", nome)

        usuarioId = Firebase.auth.currentUser?.uid.toString()

        val documentReference = db.collection("Usuarios").document(usuarioId)
        documentReference.set(usuarios).addOnSuccessListener {
            Log.d("db", "Sucesso ao salvar os dados")
        }
            .addOnFailureListener {
                Log.d("db_error", "Erro ao salvar os dados")
            }
    }

    private fun tratamentoDeErroCadastro(task: Task<AuthResult>): String {
        val erro = try {
            throw task.exception!!
        } catch (e: FirebaseAuthWeakPasswordException) {
            "Digite uma senha com no mínimo 6 caracteres"
        } catch (e: FirebaseAuthUserCollisionException) {
            "Esta conta já foi cadastrada"
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            "E-mail inválido"
        } catch (e: Exception) {
            "Erro ao cadastrar usuário"
        }
        return erro
    }
}