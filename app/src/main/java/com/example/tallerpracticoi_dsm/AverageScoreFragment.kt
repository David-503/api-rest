package com.example.tallerpracticoi_dsm

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.tallerpracticoi_dsm.dto.StudentDTO
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlin.math.roundToInt

class AverageScoreFragment : Fragment() {
  private var name: String? = null
  private var grade:  DoubleArray? = null
  private var avg:  Double? = null


  // Declarando variables
  private lateinit var txtName: EditText
  private lateinit var txtRating1: EditText
  private lateinit var txtRating2: EditText
  private lateinit var txtRating3: EditText
  private lateinit var txtRating4: EditText
  private lateinit var txtRating5: EditText
  private lateinit var lblStatus: TextView
  private lateinit var lblAverage: TextView
  private lateinit var btnClear: Button
  private lateinit var btnCalculate: Button
  private lateinit var database: FirebaseDatabase
  private lateinit var students: DatabaseReference

  private fun clear(onlyLabel: Boolean = false) {
    if(!onlyLabel) {
      txtName.setText("")
      txtRating1.setText("")
      txtRating2.setText("")
      txtRating3.setText("")
      txtRating4.setText("")
      txtRating5.setText("")
    }
    lblAverage.text = ""
    lblStatus.text = ""
  }
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    arguments?.let {
      name = it.getString(NAME)
      grade = it.getDoubleArray(GRADE)
      avg = it.getDouble(AVG)
    }
    database  = FirebaseDatabase.getInstance()
    students = database.getReference("students")
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_average_score, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    // Inicializando variables
    txtName = requireView().findViewById(R.id.txtName)
    txtRating1 = requireView().findViewById(R.id.txtRating1)
    txtRating2 = requireView().findViewById(R.id.txtRating2)
    txtRating3 = requireView().findViewById(R.id.txtRating3)
    txtRating4 = requireView().findViewById(R.id.txtRating4)
    txtRating5 = requireView().findViewById(R.id.txtRating5)
    lblStatus = requireView().findViewById(R.id.lblStatus)
    lblAverage = requireView().findViewById(R.id.lblAverage)
    btnClear = requireView().findViewById(R.id.btnClear)
    btnCalculate = requireView().findViewById(R.id.btnCalculate)

    clear(true)

    if(arguments != null) {
      txtName.setText(name)
      txtName.isEnabled = false
      txtRating1.setText(grade?.get(0).toString())
      txtRating2.setText(grade?.get(1).toString())
      txtRating3.setText(grade?.get(2).toString())
      txtRating4.setText(grade?.get(3).toString())
      txtRating5.setText(grade?.get(4).toString())
      val average = ((avg ?: 0.0) * 100.0).roundToInt() / 100.0 // Redondeandolo a 2 decimales
      if (average >= 6) {
        lblStatus.setTextColor(getResources().getColor(R.color.teal_700))
        lblStatus.text = "$name has aprovado!!!"
      } else {
        lblStatus.setTextColor(getResources().getColor(androidx.appcompat.R.color.error_color_material_dark))
        lblStatus.text = "$name has reprobado!!!"
      }
      lblAverage.text = "Promedio: $average"
    }

    // Acción a ejecutar al presionar el botón limpiar
    btnClear.setOnClickListener {
      clear()
    }

    // Acción a ejecutar al presionar el botón calcular
    btnCalculate.setOnClickListener{
      clear(true)

      val name: String = txtName.text.toString()
      val rating1: Double? = txtRating1.text.toString().toDoubleOrNull()
      val rating2: Double? = txtRating2.text.toString().toDoubleOrNull()
      val rating3: Double? = txtRating3.text.toString().toDoubleOrNull()
      val rating4: Double? = txtRating4.text.toString().toDoubleOrNull()
      val rating5: Double? = txtRating5.text.toString().toDoubleOrNull()

      if(
        name.isEmpty() ||
        rating1 == null ||
        rating2 == null ||
        rating3 == null ||
        rating4 == null ||
        rating5 == null
      ) {
        Toast.makeText(activity, "Debes indicar todos los valores!!!", Toast.LENGTH_SHORT).show()
        return@setOnClickListener
      }

      if(
        rating1 < 0 || rating1 > 10 ||
        rating2 < 0 || rating2 > 10 ||
        rating3 < 0 || rating3 > 10 ||
        rating4 < 0 || rating4 > 10 ||
        rating5 < 0 || rating5 > 10
      ) {
        Toast.makeText(activity, "Las notas deben estar entre 0 y 10!!!", Toast.LENGTH_SHORT).show()
        return@setOnClickListener
      }

      //var average = (rating1 + rating2 + rating3 + rating4 + rating5) / 5 // Calculando promedio
      var studentData = StudentDTO(name, arrayOf(rating1, rating2, rating3, rating4, rating5))

      var average =  studentData.avg
      average = ((average ?: 0.0) * 100.0).roundToInt() / 100.0 // Redondeandolo a 2 decimales


      println("***** MY DATA *****")
      println(studentData.toMap())
      students.child(name).setValue(studentData)
        .addOnCanceledListener {
          println("****** CANCEL ******")
        }
        .addOnSuccessListener { Toast.makeText(activity, "Se guardo con éxito este resultado!", Toast.LENGTH_SHORT).show() }
        .addOnFailureListener {
          println("****** ERROR ******")
          println(it.message)
          Toast.makeText(activity, "Ha fallado guardar este registro, intente de nuevo!", Toast.LENGTH_SHORT).show()
        }
      if (average >= 6) {
        lblStatus.setTextColor(resources.getColor(R.color.teal_700))
        lblStatus.text = "$name has aprovado!!!"
      } else {
        lblStatus.setTextColor(resources.getColor(androidx.appcompat.R.color.error_color_material_dark))
        lblStatus.text = "$name has reprobado!!!"
      }
      lblAverage.text = "Promedio: $average"
    }
  }

  companion object {

    private val NAME = "name"
    private var GRADE = "grade"
    private var AVG = "avg"
    fun newInstance(name: String, grade: DoubleArray, avg: Double) =
      AverageScoreFragment().apply {
        arguments = Bundle().apply {
          putString(NAME, name)
          putDoubleArray(GRADE, grade)
          putDouble(AVG, avg)
        }
      }

  }
}