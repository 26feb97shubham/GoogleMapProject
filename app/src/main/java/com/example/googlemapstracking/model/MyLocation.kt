package com.example.googlemapstracking.model

data class MyLocation(
    var latitude : Double?,
    var longitude : Double?
){
    constructor() : this(
        null,
        null
    )
}
