package com.rohit.maurya.autowallpaper

import io.realm.RealmObject

open class ImageModal : RealmObject() {
    var src: String = ""
        get() = src
}