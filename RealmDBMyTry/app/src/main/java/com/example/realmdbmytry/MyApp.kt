package com.example.realmdbproject

import Models.Address
import Models.Course
import Models.Student
import Models.Teacher
import android.app.Application
import io.realm.kotlin.Configuration
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

class MyApp: Application() {

    companion object {
        lateinit var realm: Realm
    }

    override fun onCreate() {
        super.onCreate()
        realm = Realm.open(configuration = RealmConfiguration.create(
            schema = setOf(
                Address::class,
                Teacher::class,
                Course::class,
                Student::class,
            )
        ))
    }
}