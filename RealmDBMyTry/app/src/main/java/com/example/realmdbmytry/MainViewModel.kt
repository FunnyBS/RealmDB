package com.example.realmdbproject


import Models.Address
import Models.Student
import Models.Teacher
import Models.Course
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import io.realm.kotlin.ext.realmListOf
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {

    private val realm = MyApp.realm

    val courses = realm
        .query<Course>()
        .asFlow()
        .map {results ->
            results.list.toList()
        }
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

    var courseDetails: Course? by mutableStateOf(null)
        private set

    var _isFloatButtonPressed by mutableStateOf(false)
        private set

    var _textFieldStateForFullName by mutableStateOf("")
    var _textFieldStateForStreetName by mutableStateOf("")
    var _textFieldStateForHouseNumber by mutableStateOf("")
    var _textFieldStateForZipCode by mutableStateOf("")
    var _textFieldStateForCityName by mutableStateOf("")
    var _textFieldStateForCourseName by mutableStateOf("")
    var _textFieldStateForStudentName by mutableStateOf("")

//    init {
//        createSampleEntries()
//    }

    fun setToInitial (){
        _textFieldStateForFullName = ""
        _textFieldStateForStreetName = ""
        _textFieldStateForHouseNumber = ""
        _textFieldStateForCityName = ""
        _textFieldStateForZipCode = ""
        _textFieldStateForCourseName = ""
        _textFieldStateForStudentName = ""
    }

    fun showDialog (course: Course){
        courseDetails = course
    }

    fun hideCourseDetails (){
        courseDetails = null
    }

    fun showDialogToAddNewCourse () {
        _isFloatButtonPressed = true
    }

    fun hideDialogToAddNewCourse () {
        _isFloatButtonPressed = false
    }

    fun deleteAfterwards (name: String) {
        viewModelScope.launch {
            val courseToBeDeleted = realm.query<Course>("name == $0", name).find().firstOrNull()
            realm.writeBlocking {
                if (courseToBeDeleted != null) {
                    findLatest(courseToBeDeleted)
                        ?.also { delete(it) }
                }
            }
        }
    }

    fun addNewCourse(
        teacherAddress: Address,
        courseName: String,
        student: Student
    ){
        viewModelScope.launch {
            realm.write {
                val address1 = Address().apply {
                    fullName = teacherAddress.fullName
                    street = teacherAddress.street
                    houseNumber = teacherAddress.houseNumber
                    zip = teacherAddress.zip
                    city = teacherAddress.city
                }

                val course1 = Course().apply {
                    name = courseName
                }

                val teacher1 = Teacher().apply {
                    address = teacherAddress
                    coursesToTeach = realmListOf(course1)
                }

                course1.teacher = teacher1
                address1.teacher = teacher1

                val student1 = Student().apply {
                    name = student.name
                }

                course1.enrolledStudents.add(student1)

                copyToRealm(teacher1, updatePolicy = UpdatePolicy.ALL)
                copyToRealm(course1, updatePolicy = UpdatePolicy.ALL)
                copyToRealm(student1, updatePolicy = UpdatePolicy.ALL)
            }
        }
    }

    fun createSampleEntries(){
        viewModelScope.launch {
            realm.write {
                val address1 = Address().apply {
                    fullName = "John Doe"
                    street = "John Doe Street"
                    houseNumber = 24
                    zip = 12345
                    city = "Johntown"
                }
                val address2 = Address().apply {
                    fullName = "Jane Doe"
                    street = "Jane Doe Street"
                    houseNumber = 25
                    zip = 12345
                    city = "Janetown"
                }
                val course1 = Course().apply {
                    name = "Kotlin Programming Made Easy"
                }
                val course2 = Course().apply {
                    name = "Android Basics"
                }
                val course3 = Course().apply {
                    name = "Asynchronous Programming With Coroutines"
                }
                val teacher1 = Teacher().apply {
                    address = address1
                    coursesToTeach = realmListOf(course1, course2)
                }
                val teacher2 = Teacher().apply {
                    address = address2
                    coursesToTeach = realmListOf(course3)
                }

                course1.teacher = teacher1
                course2.teacher = teacher1
                course3.teacher = teacher2

                address1.teacher = teacher1
                address2.teacher = teacher2

                val student1 = Student().apply {
                    name = "John Junior"
                }
                val student2 = Student().apply {
                    name = "Jane Junior"
                }

                course1.enrolledStudents.add(student1)
                course2.enrolledStudents.add(student2)
                course3.enrolledStudents.addAll(listOf(student1, student2))

                copyToRealm(teacher1, updatePolicy = UpdatePolicy.ALL)
                copyToRealm(teacher2, updatePolicy = UpdatePolicy.ALL)

                copyToRealm(course1, updatePolicy = UpdatePolicy.ALL)
                copyToRealm(course2, updatePolicy = UpdatePolicy.ALL)
                copyToRealm(course3, updatePolicy = UpdatePolicy.ALL)

                copyToRealm(student1, updatePolicy = UpdatePolicy.ALL)
                copyToRealm(student2, updatePolicy = UpdatePolicy.ALL)
            }
        }
    }

    fun deleteCourse() {
        viewModelScope.launch {
            realm.write {
                val course = courseDetails ?: return@write
                val latestCourse = findLatest(course) ?: return@write
                delete(latestCourse)

                courseDetails = null
            }
        }
    }
}