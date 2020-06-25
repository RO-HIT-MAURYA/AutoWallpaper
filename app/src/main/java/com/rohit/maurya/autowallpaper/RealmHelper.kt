package com.rohit.maurya.autowallpaper

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import io.realm.Realm
import io.realm.RealmResults
import java.io.ByteArrayOutputStream

class RealmHelper {
    companion object {
        private val realm: Realm = Realm.getDefaultInstance()

        fun storeDataIntoDb(string: String) {

            val l = realm.where(ImageModal::class.java).equalTo("imgUrl", string).count()

            if (l > 0)
                return

            realm.beginTransaction()

            val imageModal = realm.createObject(ImageModal::class.java)
            imageModal.imgUrl = string
            imageModal.isRecent = true

            realm.commitTransaction()
        }

        fun getRecent(): ArrayList<String> {
            val arrayList: ArrayList<String> = ArrayList()

            val realmResult: RealmResults<ImageModal> =
                realm.where(ImageModal::class.java).equalTo("isRecent", true).findAll()
            for (i in 0 until realmResult.size)
                realmResult[i]?.imgUrl?.let { arrayList.add(it) }

            return arrayList
        }

        fun getFavourite(): ArrayList<String> {
            val arrayList = ArrayList<String>()
            val realmResult: RealmResults<ImageModal> =
                realm.where(ImageModal::class.java).equalTo("isFav", true).findAll()
            for (i in 0 until realmResult.size)
                realmResult[i]?.imgUrl?.let { arrayList.add(it) }

            return arrayList
        }

        fun isFavourite(string: String): Boolean {
            val imageModal: ImageModal? =
                realm.where(ImageModal::class.java).equalTo("imgUrl", string).findFirst()
            return imageModal != null && imageModal.isFav
        }

        fun updateFavourite(string: String) {
            realm.executeTransaction {
                val imageModal: ImageModal? =
                    realm.where(ImageModal::class.java).equalTo("imgUrl", string).findFirst()
                if (imageModal != null)
                    imageModal.isFav = !imageModal.isFav
            }
        }

        fun storeBase64(string: String, bitmap: Bitmap, iF : MainActivity.Interface) {
            realm.executeTransaction {
                val imageModal: ImageModal? =
                    realm.where(ImageModal::class.java).equalTo("imgUrl", string).findFirst()
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.WEBP, 100, byteArrayOutputStream)
                val bytes: ByteArray = byteArrayOutputStream.toByteArray()
                imageModal?.base64 = Base64.encodeToString(bytes, Base64.DEFAULT)
                iF.callBack()
            }
        }

        fun getBitmap(string: String): Bitmap {
            val imageModal = realm.where(ImageModal::class.java).equalTo("imgUrl", string)
                .and().equalTo("isFav", true).findFirst()
            val str: String? = imageModal?.base64
            val bytes = Base64.decode(str, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }
    }
}