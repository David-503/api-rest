package com.example.tallerpracticoi_dsm.scholar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.tallerpracticoi_dsm.R
import com.example.tallerpracticoi_dsm.databinding.ActivityCreateAppointmentBinding
import com.example.tallerpracticoi_dsm.schedules.CitesList
import com.example.tallerpracticoi_dsm.utils.AppLayout


import android.util.Log
import android.widget.Toast
import com.example.tallerpracticoi_dsm.AlumnoApi
import com.example.tallerpracticoi_dsm.databinding.ActivityCreateScholarBinding
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

private lateinit var nombreEditText: EditText
private lateinit var apellidoEditText: EditText
private lateinit var edadEditText: EditText
private lateinit var crearButton: Button
private var idAlumno: Int = 0;
class CreateScholar : AppLayout() {
    private lateinit var binding: ActivityCreateScholarBinding

    // Obtener las credenciales de autenticación
    var auth_username = ""
    var auth_password = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        idAlumno = 0;
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_scholar)
        binding = ActivityCreateScholarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtención de datos que envia actividad anterior
        val datos: Bundle? = intent.getExtras()
        if (datos != null) {
            auth_username = datos.getString("auth_username").toString()
            auth_password = datos.getString("auth_password").toString()
        }
        binding.btnReturn2.setOnClickListener {
            val intent = Intent(this, Scholar::class.java)
            intent.putExtra("itemMenuSelected", R.id.home)
            startActivity(intent)
        }
        nombreEditText = findViewById(R.id.editTextNombre)
        apellidoEditText = findViewById(R.id.editTextApellido)
        edadEditText = findViewById(R.id.editTextNumber)
        crearButton = findViewById(R.id.btnCreateAlumno)

        idAlumno = datos?.getInt("alumno_id",0)!!
        if(idAlumno > 0){
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

            val alumno = Alumno(0,nombre, apellido, edad)
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
            val api = retrofit.create(AlumnoApi::class.java)
            if(idAlumno > 0) {
                val alumnoActualizado = Alumno(
                    idAlumno,
                    nombreEditText.text.toString(),
                    apellidoEditText.text.toString(),
                    edadEditText.text.toString().toInt()
                )
                // Realizar una solicitud PUT para actualizar el objeto Alumno
                api.actualizarAlumno(idAlumno, alumnoActualizado).enqueue(object : Callback<Alumno>{
                    override fun onResponse(call: Call<Alumno>, response: Response<Alumno>) {
                        if (response.isSuccessful && response.body() != null) {
                            // Si la solicitud es exitosa, mostrar un mensaje de éxito en un Toast

                            Toast.makeText(
                                this@CreateScholar,
                                "Alumno actualizado exitosamente",
                                Toast.LENGTH_SHORT
                            ).show()
                            val i = Intent(getBaseContext(), Scholar::class.java)
                            i.putExtra("itemMenuSelected", R.id.home)
                            startActivity(i)
                        } else {
                            // Si la respuesta del servidor no es exitosa, manejar el error
                            try {
                                val errorJson = response.errorBody()?.string()
                                val errorObj = JSONObject(errorJson)
                                val errorMessage = errorObj.getString("message")
                                Toast.makeText(this@CreateScholar, errorMessage, Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                // Si no se puede parsear la respuesta del servidor, mostrar un mensaje de error genérico
                                Toast.makeText(this@CreateScholar, "Error al actualizar el alumno", Toast.LENGTH_SHORT).show()
                                Log.e("API", "Error al parsear el JSON: ${e.message}")
                            }
                        }
                    }

                    override fun onFailure(call: Call<Alumno>, t: Throwable) {
                        // Si la solicitud falla, mostrar un mensaje de error en un Toast
                        Log.e("API", "onFailure : $t")
                        Toast.makeText(this@CreateScholar, "Error al actualizar el alumno", Toast.LENGTH_SHORT).show()

                        // Si la respuesta JSON está malformada, manejar el error
                        try {
                            val gson = GsonBuilder().setLenient().create()
                            val error = t.message ?: ""
                            val alumno = gson.fromJson(error, Alumno::class.java)
                            // trabajar con el objeto Alumno si se puede parsear
                        } catch (e: JsonSyntaxException) {
                            Log.e("API", "Error al parsear el JSON: ${e.message}")
                        } catch (e: IllegalStateException) {
                            Log.e("API", "Error al parsear el JSON: ${e.message}")
                        }
                    }
                })
            }
            else {
                api.crearAlumno(alumno).enqueue(object : Callback<Alumno> {
                    override fun onResponse(call: Call<Alumno>, response: Response<Alumno>) {
                        if (response.isSuccessful) {
                            Toast.makeText(
                                this@CreateScholar,
                                "Alumno creado exitosamente",
                                Toast.LENGTH_SHORT
                            ).show()
                            val i = Intent(getBaseContext(), Scholar::class.java)
                            i.putExtra("itemMenuSelected", R.id.home)
                            startActivity(i)

                        } else {
                            val error = response.errorBody()?.string()
                            Log.e("API", "Error crear alumno: $error")
                            Toast.makeText(
                                this@CreateScholar,
                                "Error al crear el alumno",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }

                    override fun onFailure(call: Call<Alumno>, t: Throwable) {
                        Toast.makeText(
                            this@CreateScholar,
                            "Error al crear el alumno",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }

        }
    }
}