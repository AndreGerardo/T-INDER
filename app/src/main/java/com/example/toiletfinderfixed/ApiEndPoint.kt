package com.example.toiletfinderfixed

class ApiEndPoint {
    companion object {

        private val SERVER = "http://192.168.100.14/toilet_finder/rating_dan_review/"
        val CREATE = SERVER+"create.php"
        val READ = SERVER+"read.php"
        val DELETE = SERVER+"delete.php"
        val UPDATE = SERVER+"update.php"
    }
}