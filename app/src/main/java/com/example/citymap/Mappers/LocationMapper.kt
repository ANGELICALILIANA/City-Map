package com.example.citymap.Mappers

import com.example.citymap.VO.LocationDatabaseVO
import com.example.citymap.database.entity.LocationEntity

object LocationMapper {

    // De LocationEntity a LocationDatabaseVO
    fun entityToVO(entity: LocationEntity): LocationDatabaseVO {
        return LocationDatabaseVO(
            latitude = entity.latitude,
            longitude = entity.longitude,
            name = entity.name
        )
    }

    // De LocationDatabaseVO a LocationEntity
    fun voToEntity(vo: LocationDatabaseVO): LocationEntity {
        return LocationEntity(
            name = vo.name,
            latitude = vo.latitude,
            longitude = vo.longitude
        )
    }

}