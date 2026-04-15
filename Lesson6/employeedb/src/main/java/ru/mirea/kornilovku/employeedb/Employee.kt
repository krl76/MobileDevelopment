package ru.mirea.kornilovku.employeedb

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "employee")
data class Employee(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var name: String,
    var power: String,
    var city: String
)