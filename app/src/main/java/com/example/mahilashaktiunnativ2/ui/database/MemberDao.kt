package com.example.mahilashaktiunnativ2.ui.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao

interface MemberDao {

    @Insert(
        onConflict =
            OnConflictStrategy.REPLACE
    )

    suspend fun insertMember(
        member: MemberEntity
    )

    @Update

    suspend fun updateMember(
        member: MemberEntity
    )

    @Query(
        "SELECT * FROM members WHERE isArchived = 0"
    )

    fun getAllMembers():
            Flow<List<MemberEntity>>

    @Query(
        "SELECT * FROM members WHERE isArchived = 1 ORDER BY archivedDate DESC"
    )

    fun getArchivedMembers():
            Flow<List<MemberEntity>>

    @Query(
        "UPDATE members SET isArchived = 1, archivedDate = :archivedDate, archiveReason = :reason WHERE id = :id"
    )

    suspend fun archiveMember(
        id: Int,
        archivedDate: Long,
        reason: String
    )

    @Query(
        "UPDATE members SET isArchived = 0, archivedDate = NULL, archiveReason = '' WHERE id = :id"
    )

    suspend fun restoreMember(
        id: Int
    )

    @Query(
        "DELETE FROM members WHERE id = :id"
    )

    suspend fun deleteMember(
        id: Int
    )
}
