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

data class Group(
    val id_group: Int,
    val name: String,
    val course: String,
    val faculty: String
)

data class GroupSchedule(
    val id_groupSchedule: Int,
    val groupName: String,
    val id_group: Int
)

data class Schedule(
    val id_schedule: Int,
    val id_groupSchedule: Int,
    val number: Int,
    val weekday: String
)

data class Teacher(
    val id_teacher: Int,
    val fullName: String,
    val shortName: String,
    val imagePath: String,
    val roleTeacher: String,
    val chair: String,
    val academicDegree: String,
    val academicRank: String,
    val education: String,
    val discipline: String,
    val totalWorkExperience: String,
    val scientificPedagogicalExperience: String,
    val dateQualificationImprovement: String,
    val placeQualificationImprovement: String,
    val note: String
)