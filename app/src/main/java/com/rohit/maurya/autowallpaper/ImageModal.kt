package com.rohit.maurya.autowallpaper

import io.realm.RealmObject
import java.sql.Blob

open class ImageModal : RealmObject() {
    var imgUrl: String = ""


    var isRecent: Boolean = false


    var isFav: Boolean = false


    var base64: String = ""

}