package com.sarrawi.mynokat.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sarrawi.mynokat.db.Dao.FavImageDao
import com.sarrawi.mynokat.db.Dao.FavNokatDao
import com.sarrawi.mynokat.db.Dao.NokatDao
import com.sarrawi.mynokat.model.FavImgModel
import com.sarrawi.mynokat.model.FavNokatModel
import com.sarrawi.mynokat.model.LocalDateTimeConverter
import com.sarrawi.mynokat.model.NokatModel

@Database(
    entities = [NokatModel::class,FavNokatModel::class,FavImgModel::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(LocalDateTimeConverter::class)
abstract class PostDatabase: RoomDatabase() {

    abstract fun nokatDao():NokatDao
    abstract fun favNokatDao():FavNokatDao
    abstract fun favImageDao():FavImageDao

    companion object {

        @Volatile
        private var instance: PostDatabase? = null

        fun getInstance(context: Context): PostDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): PostDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                PostDatabase::class.java,
                "PostDatabase.db"
            )
                .fallbackToDestructiveMigration()
                .build()
        }

    }
}