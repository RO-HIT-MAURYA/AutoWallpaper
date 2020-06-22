package com.rohit.maurya.autowallpaper

class ImageModal {
    var imgUrl: String = ""
        set(value) {
            imgUrl = value
        }

    var isRecent: Boolean = false
        set(value) {
            isRecent = value
        }

    var isFav: Boolean = false
        set(value) {
            isFav = value
        }

}