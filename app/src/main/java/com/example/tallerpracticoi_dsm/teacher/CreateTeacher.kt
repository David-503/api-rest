package com.example.tallerpracticoi_dsm.teacher

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.tallerpracticoi_dsm.R
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import com.example.tallerpracticoi_dsm.databinding.ActivityCreateAppointmentBinding
import com.example.tallerpracticoi_dsm.schedules.CitesList
import com.example.tallerpracticoi_dsm.utils.AppLayout


import android.util.Log
import android.widget.Toast
import com.example.tallerpracticoi_dsm.ProfesorApi
import com.example.tallerpracticoi_dsm.databinding.ActivityCreateTeacherBinding
import com.example.tallerpracticoi_dsm.scholar.*
import okhttp3.Credentials
import okhttp3.OkHttpClient
import org.json.JSONObject

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
private var idProfesor: Int = 0;
private lateinit var nombreEditText: EditText
private lateinit var apellidoEditText: EditText
private lateinit var edadEditText: EditText
private lateinit var crearButton: Button
class CreateTeacher : AppLayout() {
    private lateinit var binding: ActivityCreateTeacherBinding
    // Obtener las credenciales de autenticación
    var auth_username = ""
    var auth_password = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        idProfesor = 0;
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_teacher)
        binding = ActivityCreateTeacherBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Obtención de datos que envia actividad anterior
        val datos: Bundle? = intent.getExtras()
        if (datos != null) {
            auth_username = datos.getString("auth_username").toString()
            auth_password = datos.getString("auth_password").toString()
        }
        binding.btnReturn2.setOnClickListener {
            val intent = Intent(this, Teacher::class.java)
            intent.putExtra("itemMenuSelected", R.id.profesor)
            startActivity(intent)
        }
        nombreEditText = findViewById(R.id.editTextNombreP)
        apellidoEditText = findViewById(R.id.editTextApellidoP)
        edadEditText = findViewById(R.id.editTextNumberP)
        crearButton = findViewById(R.id.btnCreateProfesor)
        idProfesor = datos?.getInt("profesor_id",0)!!
        if(idProfesor > 0){
            val nombre = intent.getStringExtra("nombre").toString()
            val apellido = intent.getStringExtra("apellido").toString()
            val edad = intent.getIntExtra("edad", 1)

            nombreEditText.setText(nombre)
            apellidoEditText.setText(apellido)
            edadEditText.setText(edad.toString())
        }
        crearButton.setOnClickListener {
            val nombre = nombreEditText.text.toString()
            val apellido = apellidoEditText.text.toString()
            val edad = edadEditText.text.toString().toInt()

            val profesor = Profesor(0,nombre, apellido, edad)
            Log.e("API", "auth_username: $auth_username")
            Log.e("API", "auth_password: $auth_password")

            // Crea un cliente OkHttpClient con un interceptor que agrega las credenciales de autenticación
            val client = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("Authorization", Credentials.basic(auth_username, auth_password))
                        .build()
                    chain.proceed(request)
                }
                .build()

            // Crea una instancia de Retrofit con el cliente OkHttpClient
            val retrofit = Retrofit.Builder()
                .baseUrl("http://192.168.0.6:80/ApiDSM/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            // Crea una instancia del servicio que utiliza la autenticación HTTP básica
            val api = retrofit.create(ProfesorApi::class.java)
            if(idProfesor > 0) {
                val profeActualizado = Profesor(
                    idProfesor,
                    nombreEditText.text.toString(),
                    apellidoEditText.text.toString(),
                    edadEditText.text.toString().toInt()
                )
                // Realizar una solicitud PUT para actualizar el objeto Profesor
                api.actualizarProfesor(idProfesor, profeActualizado).enqueue(object : Callback<Profesor>{
                    override fun onResponse(call: Call<Profesor>, response: Response<Profesor>) {
                        if (response.isSuccessful && response.body() != null) {
                            // Si la solicitud es exitosa, mostrar un mensaje de éxito en un Toast

                            Toast.makeText(
                                this@CreateTeacher,
                                "Profesor actualizado exitosamente",
                                Toast.LENGTH_SHORT
                            ).show()
                            val i = Intent(getBaseContext(), Teacher::class.java)
                            i.putExtra("itemMenuSelected", R.id.profesor)
                            startActivity(i)
                        } else {
                            // Si la respuesta del servidor no es exitosa, manejar el error
                            try {
                                val errorJson = response.errorBody()?.string()
                                val errorObj = JSONObject(errorJson)
                                val errorMessage = errorObj.getString("message")
                                Toast.makeText(this@CreateTeacher, errorMessage, Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                // Si no se puede parsear la respuesta del servidor, mostrar un mensaje de error genérico
                                Toast.makeText(this@CreateTeacher, "Error al actualizar el Profesor", Toast.LENGTH_SHORT).show()
                                Log.e("API", "Error al parsear el JSON: ${e.message}")
                            }
                        }
                    }

                    override fun onFailure(call: Call<Profesor>, t: Throwable) {
                        // Si la solicitud falla, mostrar un mensaje de error en un Toast
                        Log.e("API", "onFailure : $t")
                        Toast.makeText(this@CreateTeacher, "Error al actualizar el Profesor", Toast.LENGTH_SHORT).show()

                        // Si la respuesta JSON está malformada, manejar el error
                        try {
                            val gson = GsonBuilder().setLenient().create()
                            val error = t.message ?: ""
                            val profesor = gson.fromJson(error, Profesor::class.java)
                            // trabajar con el objeto Profesor si se puede parsear
                        } catch (e: JsonSyntaxException) {
                            Log.e("API", "Error al parsear el JSON: ${e.message}")
                        } catch (e: IllegalStateException) {
                            Log.e("API", "Error al parsear el JSON: ${e.message}")
                        }
                    }
                })
            }
            else {
                api.crearProfesor(profesor).enqueue(object : Callback<Profesor> {
                    override fun onResponse(call: Call<Profesor>, response: Response<Profesor>) {
                        if (response.isSuccessful) {
                            Toast.makeText(
                                this@CreateTeacher,
                                "Profesor creado exitosamente",
                                Toast.LENGTH_SHORT
                            ).show()
                            val i = Intent(getBaseContext(), Teacher::class.java)
                            i.putExtra("itemMenuSelected", R.id.home)
                            startActivity(i)

                        } else {
                            val error = response.errorBody()?.string()
                            Log.e("API", "Error crear profesor: $error")
                            Toast.makeText(
                                this@CreateTeacher,
                                "Error al crear el profesor",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }

                    override fun onFailure(call: Call<Profesor>, t: Throwable) {
                        Toast.makeText(
                            this@CreateTeacher,
                            "Error al crear el Profesor",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }

        }
    }
}