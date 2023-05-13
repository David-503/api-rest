package com.example.tallerpracticoi_dsm.schedules

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tallerpracticoi_dsm.R
import com.example.tallerpracticoi_dsm.databinding.ActivityCitesListBinding
import com.example.tallerpracticoi_dsm.dto.ScheduleDTO
import com.example.tallerpracticoi_dsm.utils.AppLayout
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class CitesList : AppLayout() {
    private lateinit var binding: ActivityCitesListBinding


    private val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm")
    private val schedules = mutableListOf<ScheduleDTO>(
        ScheduleDTO(1, "2020", "Leslie", "Bryan Walberto Garay Alvarado", 1, "01/05/2023", "08:00", "09:00", "01/05/2023", "01/05/2023"),
        ScheduleDTO(2, "2020", "Leslie", "David Alejandro Bonilla Avilés", 1, "02/05/2023", "09:00", "09:00", "01/05/2023", "01/05/2023"),
        //ScheduleDTO('Bryan Walberto Garay Alvarado', formatter.parse("01/05/2023 00:00"), formatter.parse("01/05/2023 08:00"), formatter.parse("01/05/2023 08:15"))
    )
    override fun onCreate(savedInstanceState: Bundle?) {

        val data = intent.extras
        /*if(data?.getString("doctor") != null && data?.getString("doctor")!!.isNotEmpty()) {
            schedules.add(1,
                ScheduleDTO(
                    data?.getString("doctor"),
                    formatter.parse(data?.getString("date")),
                    formatter.parse(data?.getString("startTime")),
                    formatter.parse(data?.getString("endTime"))
                )
            )
        }
         */
        binding = ActivityCitesListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)

        binding.scheduleList.layoutManager = LinearLayoutManager(this)
        binding.scheduleList.adapter = ScheduleAdapter(this, schedules)

        binding.newSchedule.setOnClickListener {
            val intent = Intent(this, CreateAppointment::class.java)
            intent.putExtra("itemMenuSelected", R.id.home)
            startActivity(intent)
        }
    }

}