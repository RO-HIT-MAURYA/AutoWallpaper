package com.rohit.maurya.autowallpaper

import android.util.Log
import io.realm.Realm
import io.realm.RealmResults
import org.json.JSONArray

class RealmHelper {
    companion object {
        fun storeDataIntoDb(string: String) {
            val realm = Realm.getDefaultInstance()

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
            val realm = Realm.getDefaultInstance()
            val arrayList: ArrayList<String> = ArrayList();

            val realmResult: RealmResults<ImageModal> =
                realm.where(ImageModal::class.java).equalTo("isRecent", true).findAll()
            for (i in 0 until realmResult.size)
                realmResult[i]?.imgUrl?.let { arrayList.add(it) }

            return arrayList
        }
    }
}