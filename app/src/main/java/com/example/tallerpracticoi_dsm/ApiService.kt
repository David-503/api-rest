package com.example.tallerpracticoi_dsm
import com.example.tallerpracticoi_dsm.scholar.Alumno
import com.example.tallerpracticoi_dsm.teacher.Profesor

import retrofit2.Call
import retrofit2.http.*
interface AlumnoApi {
    @GET("alumno.php")
    fun obtenerAlumnos(): Call<List<Alumno>>

    @GET("alumno.php/{id}")
    fun obtenerAlumnoPorId(@Path("id") id: Int): Call<Alumno>

    @POST("alumno.php")
    fun crearAlumno(@Body alumno: Alumno): Call<Alumno>

    @PUT("alumno.php")
    fun actualizarAlumno(@Query("id") id: Int, @Body alumno: Alumno): Call<Alumno>

    @DELETE("alumno.php")
    fun eliminarAlumno(@Query("id") id: Int): Call<Void>
}

interface ProfesorApi {
    @GET("profesor.php")
    fun obtenerProfesores(): Call<List<Profesor>>

    @GET("profesor.php/{id}")
    fun obtenerProfesorPorId(@Path("id") id: Int): Call<Profesor>

    @POST("profesor.php")
    fun crearProfesor(@Body profesor: Profesor): Call<Profesor>

    @PUT("profesor.php")
    fun actualizarProfesor(@Query("id") id: Int, @Body profesor: Profesor): Call<Profesor>

    @DELETE("profesor.php")
    fun eliminarProfesor(@Query("id") id: Int): Call<Void>
}