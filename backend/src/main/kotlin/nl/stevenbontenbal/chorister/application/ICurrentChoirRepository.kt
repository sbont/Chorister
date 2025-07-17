package nl.stevenbontenbal.chorister.application

import nl.stevenbontenbal.chorister.domain.users.Choir

interface ICurrentChoirRepository {
    fun insertAsCurrentChoir(choir: Choir): Choir

    fun setCurrentChoir(choir: Choir)
}