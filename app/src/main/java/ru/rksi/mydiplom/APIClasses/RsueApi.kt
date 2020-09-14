package ru.rksi.mydiplom.APIClasses

import retrofit2.Call
import retrofit2.http.*

interface RsueApi {

    /*
    * Расписание
    */
    @GET("schedule/Faculties")
    fun getFaculties(): Call<ArrayList<Faculty>>

    @GET("schedule/Courses")
    fun getCourses(@Query("facultyId") id: Int): Call<ArrayList<Course>>

    @GET("schedule/Groups")
    fun getGroups(@Query("courseId") id: Int): Call<ArrayList<Group>>

    @GET("schedule/getForStudent")
    fun getStudentSchedule(@Query("groupId") id: Int): Call<Schedule>

    @GET("schedule/teachers")
    fun getTeachers(): Call<ArrayList<Teacher>>

    @GET("schedule/getForTeacher")
    fun getTeachersSchedule(@Query("teacherId") id: Int): Call<Schedule>

    /*
    * Новости
    */
    @GET("news")
    fun getNews(@Query("offset") offset: Int, @Query("count") count: Int): Call<ArrayList<News>>

    @GET("news")
    fun getNews(): Call<ArrayList<News>>

    /*
    * Авторизация
    */
    @POST("account/token")
    @Headers("Content-Type: application/json")
    fun getToken(@Body body: TokenBody): Call<TokenResponse>

    @POST("account/token/refresh")
    @Headers("Content-Type: application/json")
    fun updateToken(@Body body:RefreshTokenRequest):Call<TokenResponse>

    /*
    * Профиль
    */
    // Получает инфу о текущем пользователе
    @GET("profile")
    @Headers("Accept: application/json")
    fun getUserinfo(@Header("Authorization") Authorization: String): Call<ProfileResponse>

    //Получает список оценок пользователя если студент
    @GET("performance")
    @Headers("Content-Type: application/json")
    fun getMarks(@Header("Authorization") Authorization: String): Call<ArrayList<Performance>>

    //Получает все группы у которых ведет юзер если препод
    @GET("teacher/groups")
    @Headers("Content-Type: application/json")
    fun getGroupsForUser(@Header("Authorization") Authorization: String): Call<ArrayList<Group>>

    //Получает список всех пар, которые ведет юзер для группы, если препод
    @GET("teacher/lessons")
    @Headers("Content-Type: application/json")
    fun getLessonsForUser(@Header("Authorization") Authorization: String, @Query("groupId") groupId: Int): Call<ArrayList<Lesson>>
    /*
    * Задания
    */
    @GET("tasks")
    @Headers("Content-Type: application/json")
    fun getTasks(@Header("Authorization") Authorization: String): Call<ArrayList<TaskResponse>>

    @POST("tasks")
    @Headers("Accept: application/json")
    fun addTasks(@Header("Authorization") Authorization: String, @Body body: TaskResponse): Call<Void>

    @DELETE("tasks/{id}")
    @Headers("Accept: application/json")
    fun deleteTasks(@Header("Authorization") Authorization: String, @Path("id") taskId: Long): Call<Void>


    @PUT("tasks/{id}")
    @Headers("Accept: application/json")
    fun changeTasks(@Header("Authorization") Authorization: String, @Body body: TaskResponse, @Path("id") taskId: Long): Call<Void>

    /*
     * Мой факультет
     */
    @GET("myfaculty/forstudents")
    @Headers("Content-Type: application/json")
    fun getGroupMates(@Header("Authorization") Authorization: String, @Query("groupId") groupId: Int): Call<ArrayList<MyFacultyUserResponse>>

    @GET("myfaculty/forteachers")
    @Headers("Content-Type: application/json")
    fun getFacultiesWithTeachers(@Header("Authorization") Authorization: String): Call<ArrayList<MyFacultyFacultiesResponse>>

    //Получает список всех пар, которые ведет юзер, если препод
    @GET("myfaculty/teacherlessons")
    @Headers("Content-Type: application/json")
    fun getAllLessons(@Header("Authorization") Authorization: String, @Query("teacherId") teacherId: Int): Call<ArrayList<Lesson>>

    @GET("notifications")
    @Headers("Content-Type: application/json")
    fun getNotification(@Header("Authorization") Authorization: String): Call<ArrayList<NotificationResponse>>

    @POST("notifications")
    @Headers("Content-Type: application/json")
    fun sendNotification(@Header("Authorization") Authorization: String, @Body body:NotificationRequest): Call<Void>

    @GET("notifications/filters")
    fun getFilters(@Header("Authorization") Authorization: String):Call<NotificationFilters>

    @PUT("notifications/filters")
    fun sendFilters(@Header("Authorization") Authorization: String, @Body filters:NotificationFilters): Call<NotificationFilters>

}