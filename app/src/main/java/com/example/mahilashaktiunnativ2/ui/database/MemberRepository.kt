package com.example.mahilashaktiunnativ2.ui.database

import kotlinx.coroutines.flow.Flow

class MemberRepository(

    private val memberDao:
    MemberDao
) {

    val allMembers:
            Flow<List<MemberEntity>> =

        memberDao.getAllMembers()

    val archivedMembers:
            Flow<List<MemberEntity>> =

        memberDao.getArchivedMembers()

    suspend fun insertMember(
        member: MemberEntity
    ) {

        memberDao.insertMember(member)
    }

    suspend fun updateMember(
        member: MemberEntity
    ) {

        memberDao.updateMember(member)
    }

    suspend fun deleteMember(
        id: Int
    ) {

        memberDao.deleteMember(id)
    }

    suspend fun archiveMember(
        id: Int,
        archivedDate: Long,
        reason: String
    ) {

        memberDao.archiveMember(
            id = id,
            archivedDate = archivedDate,
            reason = reason
        )
    }

    suspend fun restoreMember(
        id: Int
    ) {

        memberDao.restoreMember(id)
    }
}
