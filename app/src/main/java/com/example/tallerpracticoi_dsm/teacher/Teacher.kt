package com.example.tallerpracticoi_dsm.teacher

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.tallerpracticoi_dsm.R
import com.example.tallerpracticoi_dsm.utils.AppLayout

import androidx.recyclerview.widget.RecyclerView
import com.example.tallerpracticoi_dsm.ProfesorApi
import okhttp3.Credentials
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import android.app.AlertDialog
import android.util.Log
import android.content.Intent
import com.example.tallerpracticoi_dsm.AlumnoApi
import com.example.tallerpracticoi_dsm.databinding.ActivityCitesListBinding
import com.example.tallerpracticoi_dsm.databinding.ActivityScholarBinding
import com.example.tallerpracticoi_dsm.databinding.ActivityTeacherBinding
import com.example.tallerpracticoi_dsm.schedules.CreateAppointment
import com.example.tallerpracticoi_dsm.scholar.Alumno
import com.example.tallerpracticoi_dsm.scholar.AlumnoAdapter
import com.example.tallerpracticoi_dsm.scholar.CreateScholar
import com.example.tallerpracticoi_dsm.teacher.ProfesorAdapter

class Teacher : AppLayout() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProfesorAdapter
    private lateinit var api: ProfesorApi
    private lateinit var binding: ActivityTeacherBinding

    // Obtener las credenciales de autenticación
    val auth_username = "admin"
    val auth_password = "admin123"

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_teacher)
        binding = ActivityTeacherBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)

        recyclerView = findViewById(R.id.recyclerViewP)
        recyclerView.layoutManager = LinearLayoutManager(this)
// Crea un cliente OkHttpClient con un interceptor que agrega las credenciales de autenticación
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", Credentials.basic(auth_username,
                        auth_password))
                    .build()
                chain.proceed(request)
            }
            .build()
        // Crea una instancia de Retrofit con el cliente OkHttpClient
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.0.6/ApiDSM/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        // Crea una instancia del servicio que utiliza la autenticación HTTP básica
        api = retrofit.create(ProfesorApi::class.java)

        cargarDatos(api)

        // Cuando el usuario quiere agregar un nuevo registro
//        fab_agregar.setOnClickListener(View.OnClickListener {
//            val i = Intent(getBaseContext(), CrearAlumnoActivity::class.java)
//            i.putExtra("auth_username", auth_username)
//            i.putExtra("auth_password", auth_password)
//            startActivity(i)
//        })


        binding.newProfesor.setOnClickListener {
            val intent = Intent(this, CreateTeacher::class.java)
            intent.putExtra("auth_username", auth_username)
            intent.putExtra("auth_password", auth_password)
            intent.putExtra("itemMenuSelected", R.id.home)
            startActivity(intent)

        }



    }
    override fun onResume() {
        super.onResume()
        cargarDatos(api)
    }
    private fun cargarDatos(api: ProfesorApi) {
        val call = api.obtenerProfesores()
        call.enqueue(object : Callback<List<Profesor>> {
            override fun onResponse(call: Call<List<Profesor>>, response: Response<List<Profesor>>) {
                if (response.isSuccessful) {
                    val profesores = response.body()
                    if (profesores != null) {
                        adapter = ProfesorAdapter(profesores)
                        recyclerView.adapter = adapter

                        // Establecemos el escuchador de clics en el adaptador
                        adapter.setOnItemClickListener(object : ProfesorAdapter.OnItemClickListener {
                            override fun onItemClick(profesor: Profesor) {
                                val opciones = arrayOf("Modificar Profesor", "Eliminar Profesor")

                                AlertDialog.Builder(this@Teacher)
                                    .setTitle(profesor.nombre)
                                    .setItems(opciones) { dialog, index ->
                                        when (index) {
                                            0 -> Modificar(profesor)
                                            1 -> eliminarAlumno(profesor, api)
                                        }
                                    }
                                    .setNegativeButton("Cancelar", null)
                                    .show()
                            }
                        })
                    }
                } else {
                    val error = response.errorBody()?.string()
                    Log.e("API", "Error al obtener los profesores: $error")
                    Toast.makeText(
                        this@Teacher,
                        "Error al obtener los profesores 1",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Profesor>>, t: Throwable) {
                Log.e("API", "Error al obtener los alumnos: ${t.message}")
                Toast.makeText(
                    this@Teacher,
                    "Error al obtener los alumnos 2",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun Modificar(profesor: Profesor) {
        // Creamos un intent para ir a la actividad de actualización de alumnos
        val i = Intent(getBaseContext(), CreateTeacher::class.java)
        // Pasamos el ID del alumno seleccionado a la actividad de actualización
        i.putExtra("profesor_id", profesor.id)
        i.putExtra("nombre", profesor.nombre)
        i.putExtra("apellido", profesor.apellido)
        i.putExtra("edad", profesor.edad)
        i.putExtra("auth_username", auth_username)
        i.putExtra("auth_password", auth_password)
        i.putExtra("itemMenuSelected", R.id.home)
        // Iniciamos la actividad de actualización de alumnos
        startActivity(i)
    }

    private fun eliminarAlumno(profesor: Profesor, api: ProfesorApi) {
        val profesorTMP = Profesor(profesor.id,"", "", -987)
        Log.e("API", "id : $profesor")
        val llamada = api.eliminarProfesor(profesor.id)
        llamada.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@Teacher, "Profesor eliminado", Toast.LENGTH_SHORT).show()
                    cargarDatos(api)
                } else {
                    val error = response.errorBody()?.string()
                    Log.e("API", "Error al eliminar Profesor : $error")
                    Toast.makeText(this@Teacher, "Error al eliminar Profesor 1", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("API", "Error al eliminar Profesor : $t")
                Toast.makeText(this@Teacher, "Error al eliminar Profesor 2", Toast.LENGTH_SHORT).show()
            }
        })
    }
}