package nl.stevenbontenbal.chorister.repository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class ScoreRepositoryTests @Autowired constructor(
    val entityManager: TestEntityManager,
    val scoreRepository: ScoreRepository,
)