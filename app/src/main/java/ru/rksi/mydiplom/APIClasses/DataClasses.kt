package ru.rksi.mydiplom.APIClasses

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.*

@Serializable
data class Faculty(
    val facultyId: Int,
    val number: Int,
    val name: String
)


@Serializable
data class Course(
    val courseId: Int,
    val number: Int
)


@Serializable
data class Group(
    val groupId: Int,
    val number: Int,
    val name: String
) {
    fun toJson(): String {
        return Json.stringify(serializer(), this)
    }
}

@Serializable
data class Teacher(
    val teacherId: Int,
    val name: String,
    val position: String
) {

    fun toJson(): String {
        return Json.stringify(serializer(), this)
    }
}

@Serializable
data class Classroom(
    val number: String,
    val corps: String,
    val address: String
)

@Serializable
data class Subject(
    val lessonId: Int,
    val title: String,
    val startTime: String,
    val endTime: String,
    val classroom: Classroom,
    val teacher: Teacher,
    val group: Group,
    val subgroup: Int,
    val typeOfWork: String
)


@Serializable
data class Day(
    val title: String,
    val date: Int,
    val subjects: ArrayList<Subject>
) {
    fun toTime(): String {
        val sdf = SimpleDateFormat("EEEE", Locale("ru")).format(Date(date.toLong() * 1000))
        return sdf
    }
}


@Serializable
data class Schedule(
    val isThisWeekEven: Boolean,
    val evenWeek: Week,
    val unevenWeek: Week
) {
    fun toJson(): String {
        return Json.stringify(serializer(), this)
    }
}

@Serializable
data class Week(
    val days: ArrayList<Day>
)

@Serializable
data class TaskResponse(
    val taskId: Long,
    val text: String,
    val deadline: Long,
    val teacher: Teacher,
    val group: Group,
    val lesson: Lesson
) {
    fun toJson(): String {
        return Json.stringify(serializer(), this)
    }
}

@Serializable
data class News(
    val date: String,
    val title: String,
    val mainImage: String,
    val images: ArrayList<String>,
    val shortText: String,
    val text: String,
    val formattedText: String,
    val urlNews: String
)

data class TokenBody(
    val Username: String,
    val Password: String,
    val NotificationToken: String
)

@Serializable
data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long
)

data class RefreshTokenRequest(
    val refreshToken: String
)

data class ProfileResponse(
    val teacherId: Int,
    val name: String,
    val position: String,
    val group: Group,
    val avatar: String
)

@Serializable
data class UserInfo(
    val id: Int,
    val position: String?,
    val username: String,
    val group: Group?,
    val isTeacher: Boolean,
    val avatar: String
) {
    fun toJson(): String {
        return Json.stringify(serializer(), this)
    }
}

@Serializable
class NotificationSettings() {
    var BeforeStart: Int = 0
    var BeforeEnd: Int = 0

    fun toJson(): String {
        return Json.stringify(serializer(), this)
    }
}

@Serializable
class Performance(
    val course: Short,
    val academicYear: String,
    val semester: Semester
)

@Serializable
data class Semester(
    val number: Short,
    val exams: ArrayList<Mark>,
    val credits: ArrayList<Mark>,
    val practices: ArrayList<Mark>
)

@Serializable
data class Mark(
    val teacher: Teacher,
    val lesson: Lesson,
    val checkpoint1: Short,
    val checkpoint2: Short,
    val finalPercent: Short,
    val value: Short
)

@Serializable
data class Lesson(
    val lessonId: Long,
    val title: String
)

data class FormOfControl(
    val name: String,
    val marks: ArrayList<Mark>
)

@Serializable
data class TaskRequest(
    val text: String,
    val deadline: Long,
    val groupId: Long,
    val lessonId: Long
) {
    fun toJson(): String {
        return Json.stringify(serializer(), this)
    }
}

@Serializable
data class MyFacultyUserResponse(
    val teacherId: Int,
    val name: String,
    val avatar: String
)

@Serializable
data class MyFacultyFacultiesResponse(
    val faculty: String,
    val teachers: ArrayList<MyFacultyUserResponse>
)

data class TokenUpdate(
    val accessToken: String,
    val refreshToken: String
)

data class NotificationFrom(
    val Id:Int,
    val name:String,
    val avatar:String
)

data class NotificationResponse(
    val title: String,
    val text: String,
    val date: String,
    val teacher: NotificationFrom,
    val group: NotificationFrom
)

data class NotificationRequest(
    val groupId:Int,
    val title:String,
    val text:String
)

data class NotificationFilters(
    val filters: ArrayList<String>
)

