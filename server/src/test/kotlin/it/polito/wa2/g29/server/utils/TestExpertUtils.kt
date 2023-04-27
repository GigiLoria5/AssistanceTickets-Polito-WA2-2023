package it.polito.wa2.g29.server.utils

import it.polito.wa2.g29.server.enums.Expertise
import it.polito.wa2.g29.server.enums.Level
import it.polito.wa2.g29.server.model.Expert
import it.polito.wa2.g29.server.model.Skill
import it.polito.wa2.g29.server.repository.ExpertRepository

object TestExpertUtils {
    val experts = listOf(
        Expert(
            name = "John",
            surname = "Doe",
            email = "john.doe@example.com",
            country = "United States",
            city = "New York",
            skills = mutableSetOf()
        ),
        Expert(
            name = "Jane",
            surname = "Doe",
            email = "jane.doe@example.com",
            country = "United States",
            city = "Los Angeles",
            skills = mutableSetOf()
        ),
        Expert(
            name = "Bob",
            surname = "Smith",
            email = "bob.smith@example.com",
            country = "Canada",
            city = "Toronto",
            skills = mutableSetOf()
        )
    )

    init {
        val expert0 = experts[0]
        expert0.skills.add(Skill(expertise = Expertise.SMARTPHONE, level = Level.AVERAGE, expert = expert0))
        expert0.skills.add(Skill(expertise = Expertise.COMPUTER, level = Level.SKILLED, expert = expert0))

        val expert1 = experts[1]
        expert1.skills.add(Skill(expertise = Expertise.APPLIANCES, level = Level.EXPERT, expert = expert1))
        expert1.skills.add(Skill(expertise = Expertise.CONSUMER_ELECTRONICS, level = Level.SKILLED, expert = expert1))
        expert1.skills.add(Skill(expertise = Expertise.SMARTPHONE, level = Level.SPECIALIST, expert = expert1))

        val expert2 = experts[2]
        expert2.skills.add(Skill(expertise = Expertise.COMPUTER, level = Level.BEGINNER, expert = expert2))
        expert2.skills.add(Skill(expertise = Expertise.APPLIANCES, level = Level.AVERAGE, expert = expert2))
        expert2.skills.add(Skill(expertise = Expertise.SMARTPHONE, level = Level.SKILLED, expert = expert2))
        expert2.skills.add(
            Skill(
                expertise = Expertise.CONSUMER_ELECTRONICS,
                level = Level.SPECIALIST,
                expert = expert2
            )
        )
    }

    fun insertExperts(expertRepository: ExpertRepository) {
        expertRepository.saveAll(experts)
    }
}