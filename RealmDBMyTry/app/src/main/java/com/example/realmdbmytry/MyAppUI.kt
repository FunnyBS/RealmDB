package com.example.realmdbmytry

import Models.Address
import Models.Course
import Models.Student
import Models.Teacher
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.realmdbproject.MainViewModel
import io.realm.kotlin.ext.realmListOf

@Composable
fun MyAppUI(
    viewModel: MainViewModel
) {
    val courses by viewModel.courses.collectAsState()



    fun templateForAdding() {
        val addressToBeAdded = Address().apply {
            fullName = viewModel._textFieldStateForFullName
            street = viewModel._textFieldStateForStreetName
            houseNumber = viewModel._textFieldStateForHouseNumber.toInt()
            zip = viewModel._textFieldStateForZipCode.toInt()
            city = viewModel._textFieldStateForCityName
        }
        val course = Course().apply {
            name = viewModel._textFieldStateForCourseName
        }
        val teacher = Teacher().apply {
            address = addressToBeAdded
            coursesToTeach = realmListOf(course)
        }
        addressToBeAdded.teacher = teacher
        course.teacher = teacher
        val studentToBeAdded: Student = Student().apply {
            name = viewModel._textFieldStateForStudentName
        }
        course.enrolledStudents.add(studentToBeAdded)
        viewModel.addNewCourse(addressToBeAdded, course.name, studentToBeAdded)
        viewModel.hideDialogToAddNewCourse()
        viewModel.deleteAfterwards(course.name)
        viewModel.setToInitial()
    }
    Scaffold(
        floatingActionButton = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomEnd
            ) {
                FloatingActionButton(
                    onClick = {
                        viewModel.showDialogToAddNewCourse()
                    },
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "+",
                        fontSize = 25.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    if (viewModel._isFloatButtonPressed) {
                        Dialog(onDismissRequest = { viewModel.hideDialogToAddNewCourse() }) {
                            Column (
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(16.dp),
                            ) {
                                TextField(
                                    value = viewModel._textFieldStateForFullName,
                                    onValueChange = { viewModel._textFieldStateForFullName = it },
                                    placeholder = {
                                        Text(text = "Teacher's name")   /*  Address: street, houseNumber, zip, city, teacher*/
                                    }
                                )
                                TextField(
                                    value = viewModel._textFieldStateForStreetName,
                                    onValueChange = { viewModel._textFieldStateForStreetName = it },
                                    placeholder = {
                                        Text(text = "Teacher's street")
                                    }
                                )
                                TextField(
                                    value = viewModel._textFieldStateForHouseNumber,
                                    onValueChange = { viewModel._textFieldStateForHouseNumber = it },
                                    placeholder = {
                                        Text(text = "Teacher's house number")
                                    }
                                )
                                TextField(
                                    value = viewModel._textFieldStateForZipCode,
                                    onValueChange = { viewModel._textFieldStateForZipCode = it },
                                    placeholder = {
                                        Text(text = "Teacher's ZIP code")
                                    }
                                )
                                TextField(
                                    value = viewModel._textFieldStateForCityName,
                                    onValueChange = { viewModel._textFieldStateForCityName = it },
                                    placeholder = {
                                        Text(text = "Teacher's city")
                                    }
                                )
                                TextField(
                                    value = viewModel._textFieldStateForCourseName,
                                    onValueChange = { viewModel._textFieldStateForCourseName = it },
                                    placeholder = {
                                        Text(text = "Course name")
                                    }
                                )
                                TextField(
                                    value = viewModel._textFieldStateForStudentName,
                                    onValueChange = { viewModel._textFieldStateForStudentName = it },
                                    placeholder = {
                                        Text(text = "Students to be enrolled")
                                    }
                                )
                                Spacer(modifier = Modifier.height(15.dp))
                                Button(onClick = {
                                    templateForAdding()
                                },
                                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.onSecondaryContainer)
                                    ) {
                                    Text(
                                        text = "Submit",
                                        color = MaterialTheme.colorScheme.secondaryContainer
                                        )
                                }
                            }
                        }
                    }
                }
            }
        }
    ) {padding ->
        LazyColumn (
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(courses){course ->
                CourseItem(
                    course = course,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.showDialog(course)
                        }
                )
            }
        }
        if (viewModel.courseDetails != null) {
            Dialog(onDismissRequest = viewModel::hideCourseDetails) {
                Column(
                    modifier = Modifier
                        .widthIn(200.dp, 300.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp)
                ) {
                    viewModel.courseDetails?.teacher?.address?.let {address ->
                        Text(text = address.fullName)
                        Text(text = address.street + " " + address.houseNumber)
                        Text(text = address.zip.toString() + " " + address.city)
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    Button(
                        onClick = viewModel::deleteCourse,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    ) {
                        Text(text = "Delete")
                    }
                }
            }
        }

    }
}

@Composable
fun CourseItem(
    course: Course,
    modifier: Modifier = Modifier
) {
    Column (
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = course.name,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Text(
            text = "Held by ${course.teacher?.address?.fullName}",
            fontSize = 12.sp,
            fontStyle = FontStyle.Italic
        )
        Text(
            text = "Enrolled Students: ${course.enrolledStudents.joinToString { it.name }}",
            fontSize = 10.sp
        )
        Spacer(
            modifier = Modifier.height(8.dp)
        )
    }
}