package com.example.citymap.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.citymap.database.entity.LocationEntity

@Dao
interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(locationEntity: LocationEntity)

    @Query("SELECT * FROM locations WHERE name LIKE '%' || :query || '%' LIMIT 1")
    suspend fun searchByName(query: String): LocationEntity?
}