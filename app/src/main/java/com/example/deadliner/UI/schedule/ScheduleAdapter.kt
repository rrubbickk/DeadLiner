package com.example.deadliner.UI.schedule

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.contains
import androidx.recyclerview.widget.RecyclerView
import com.example.deadliner.R
import com.example.deadliner.model.Subject
import com.example.deadliner.viewmodel.SubjectViewModel
import java.text.SimpleDateFormat
import java.util.*

class ScheduleAdapter(
        val inflater: LayoutInflater,
        private val subjectViewModel: SubjectViewModel)
        : RecyclerView.Adapter<ScheduleAdapter.ViewHolder>() {

    private val dates = mutableListOf<Date>()
            private val todayCalendar = Calendar.getInstance()
            private val endCalendar = Calendar.getInstance()

    init {
        endCalendar.set(2021,7,1)
        while(!todayCalendar.after(endCalendar))
        {
            dates.add(todayCalendar.time)
            todayCalendar.add(Calendar.DATE, 1)
        }
    }

    inner class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        private val dayTextView: TextView = item.findViewById(R.id.day_of_week)
        private val dateTextView: TextView = item.findViewById(R.id.date)
        private val classesAmount: TextView = item.findViewById(R.id.classes)
        private var classesLayout: LinearLayout = item.findViewById(R.id.classes_layout)
        private val simpleDateFormat = SimpleDateFormat("EEEE")

        fun bind(date: Date) {

            simpleDateFormat.applyPattern("EEEE")
            if (!DateUtils.isToday(date.time))
                { dayTextView.text = simpleDateFormat.format(date) }
            else { dayTextView.text = "Cегодня" }

            simpleDateFormat.applyPattern("d MMMM")
            dateTextView.text = simpleDateFormat.format(date)

            val subjects = subjectViewModel.getSubjectsByDate(date)
            classesAmount.text = getClassesAmountString(subjects.size)

            for (subj in subjects) {
               var subjectview = makeSubjectView(subj)
                classesLayout.addView(subjectview)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.item_schedule, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dates.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dates[position])
    }

    fun makeSubjectView(subject: Subject) : View {
        var subjectView = inflater.inflate(R.layout.item_subject, null)
        val type = subjectView.findViewById<TextView>(R.id.type)
        val name = subjectView.findViewById<TextView>(R.id.name)
        val place = subjectView.findViewById<TextView>(R.id.place)
        val timeBegin = subjectView.findViewById<TextView>(R.id.time_begin)
        val timeEnd = subjectView.findViewById<TextView>(R.id.time_end)
        val simpleDateFormat = SimpleDateFormat("H:mm")
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = subject.date

        type.text = subject.type
        name.text = subject.name
        place.text = subject.place
        timeBegin.text = simpleDateFormat.format(subject.date)
        calendar.add(Calendar.HOUR_OF_DAY,1)
        calendar.add(Calendar.MINUTE,30)
        timeEnd.text = simpleDateFormat.format(calendar.time)
        subjectView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
        return subjectView
    }

    fun getClassesAmountString(amount: Int) : String {
        return when (amount) {
            0 -> ", пар нет"
            1 -> ", 1 пара"
            2,3,4 -> ", $amount пары"
            5,6,7,8,9,10 -> ", $amount пар"
            else -> ", куда столько пар"
        }
    }

    fun setData() {
        notifyDataSetChanged()
    }


}