package com.example.tallerpracticoi_dsm.schedules

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.tallerpracticoi_dsm.R
import com.example.tallerpracticoi_dsm.databinding.ActivityCitesListBinding
import com.example.tallerpracticoi_dsm.databinding.ActivityCreateAppointmentBinding
import com.example.tallerpracticoi_dsm.utils.AppLayout
import java.text.SimpleDateFormat
import java.util.*

class CreateAppointment : AppLayout() {
    private lateinit var binding: ActivityCreateAppointmentBinding
    private lateinit var iptDate: EditText
    private val formatter = SimpleDateFormat("dd/MM/yyyy")
    private val timeFormatter = SimpleDateFormat("HH:mm")

    private val items = listOf("Bryan Walberto Garay Alvarado", "Michelle Alejandra Hernandez Ayala")

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCreateAppointmentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)

        startListeners()
        startDropdown()
    }

    private fun startDropdown() {
        val adapter = ArrayAdapter(this, R.layout.dropdown_item_adapter, items)
        binding.dpdDoctors.setAdapter(adapter)
        binding.dpdDoctors.onItemClickListener = AdapterView.OnItemClickListener {
                adapterView, view, i, l ->
            val selectedItem = adapterView.getItemAtPosition(i)
            Toast.makeText(this, "Doctor $selectedItem", Toast.LENGTH_SHORT)
        }
    }

    private fun initDatePicker(editText: EditText, label: TextView) {
        if(editText.error != null && editText.error.isNotEmpty()) editText.error = null
        val calendar = Calendar.getInstance()
        if(editText.text.isNotEmpty()) calendar.time = formatter.parse(editText.text.toString())
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(this, {
                _, year, month , dayOfMonth ->

            if(year < calendar.get(Calendar.YEAR) || month < calendar.get(Calendar.MONTH) || dayOfMonth < calendar.get(
                    Calendar.DAY_OF_MONTH)) {
                editText.error = getString(R.string.error_input_date)
                return@DatePickerDialog
            }
            val actualMonth = month +1;
            editText.setText("$dayOfMonth/$actualMonth/$year")
            if(label.visibility === View.GONE) label.visibility = View.VISIBLE
        }, year, month, day)
        datePickerDialog.show()
    }
    private fun initTimePicker(editText: EditText, label: TextView) {
        if(editText.error != null && editText.error.isNotEmpty()) editText.error = null
        val calendar = Calendar.getInstance()
        if(editText.text.isNotEmpty()) calendar.time = timeFormatter.parse(editText.text.toString())
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(this, {
                _, hour, minute ->
            if(label.visibility === View.GONE) label.visibility = View.VISIBLE
            if(label.id == R.id.labelStartTime) binding.iptEndTime.isEnabled = true
            else {
                val values = binding.iptStartTime.text.split(':').map { value -> value.toInt() }
                if(hour <= values[0] && minute < values[1]) {
                    editText.error = getString(R.string.error_end_time)
                    return@TimePickerDialog
                }
            }
            editText.setText("$hour:$minute")
        }, hour, minute, false)
        timePickerDialog.show()
    }
    private fun startListeners() {
        // Date Picker
        val iptDate = binding.inputDate
        val labelDate = binding.labelDate

        iptDate.setOnClickListener { initDatePicker(iptDate, labelDate )}
        iptDate.setOnFocusChangeListener { _, hasFocus -> if(hasFocus) initDatePicker(iptDate, labelDate)}

        // Start Time
        val iptStartTime = binding.iptStartTime
        val labelStartTime = binding.labelStartTime
        iptStartTime.setOnClickListener { initTimePicker(iptStartTime, labelStartTime) }
        iptStartTime.setOnFocusChangeListener { _, hasFocus -> if(hasFocus) initTimePicker(iptStartTime, labelStartTime) }

        // End Time
        val iptEndTime = binding.iptEndTime
        val labelEndTime = binding.labelEndTime
        iptEndTime.setOnClickListener { initTimePicker(iptEndTime, labelEndTime) }
        iptEndTime.setOnFocusChangeListener { _, hasFocus -> if(hasFocus) initTimePicker(iptEndTime, labelEndTime) }

        // End button
        binding.btnCreateSchedule.setOnClickListener {
            if(!binding.inputDate.text.isNotEmpty() || !binding.iptStartTime.text.isNotEmpty() || !binding.iptEndTime.text.isNotEmpty() || !binding.dpdDoctors.text.isNotEmpty()) {
                Toast.makeText(this, R.string.complete_form, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(this, R.string.schedule_done, Toast.LENGTH_SHORT).show()
            val intent = Intent(this, CitesList::class.java)
            intent.putExtra("doctor", binding.dpdDoctors.text.toString())
            intent.putExtra("date", binding.inputDate.text.toString() + " 00:00")
            intent.putExtra("startTime", binding.inputDate.text.toString() + " " + binding.iptStartTime.text)
            intent.putExtra("endTime", binding.inputDate.text.toString() + " " + binding.iptEndTime.text)
            startActivity(intent)
            return@setOnClickListener
        }
        // Floating Button
        binding.btnReturn.setOnClickListener {
            val intent = Intent(this, CitesList::class.java)
            intent.putExtra("itemMenuSelected", R.id.home)
            startActivity(intent)
        }
    }
}