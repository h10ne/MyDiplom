package ru.rksi.mydiplom.APIClasses

data class Lesson(
    val time: String,
    val name: String,
    val teacher: String,
    val room: String,
    val type: String
)

data class Day(val lessons: ArrayList<Lesson>)

data class Week(val days: ArrayList<Day>)
data class Timetable(val Weeks: ArrayList<Week>)

data class Task(val discipline:String, val taskStatus:String, val taskText:String, val taskUntil:String)