package it.polito.wa2.g29.server.utils

import it.polito.wa2.g29.server.enums.TicketStatus

object TicketStatusChangeRules {
    //Key is the current state, values is the list of allowed future states
    private val allowedTicketStatusChanges: Map<TicketStatus, List<TicketStatus>> = mapOf(
        TicketStatus.OPEN to listOf(
            TicketStatus.IN_PROGRESS,
            TicketStatus.RESOLVED,
            TicketStatus.CLOSED
        ),
        TicketStatus.IN_PROGRESS to listOf(
            TicketStatus.OPEN,
            TicketStatus.RESOLVED,
            TicketStatus.CLOSED
        ),
        TicketStatus.RESOLVED to listOf(
            TicketStatus.REOPENED,
            TicketStatus.CLOSED
        ),
        TicketStatus.REOPENED to listOf(
            TicketStatus.IN_PROGRESS,
            TicketStatus.RESOLVED,
            TicketStatus.CLOSED
        ),
        TicketStatus.CLOSED to listOf(
            TicketStatus.REOPENED
        )
    )

    fun isValidStatusChange(fromStatus: TicketStatus, toStatus: TicketStatus): Boolean {
        //DEBUGGING. TO BE REMOVED
        println("FROM : ${fromStatus.name}, TO : ${toStatus.name}")
        println(allowedTicketStatusChanges[fromStatus]?.any { it == toStatus })
        return allowedTicketStatusChanges[fromStatus]?.any { it == toStatus } ?: false
    }

    fun getTaskToAchieveStatus(targetStatus: TicketStatus): String {
        return when (targetStatus) {
            TicketStatus.IN_PROGRESS -> "start"
            TicketStatus.OPEN -> "stop"
            TicketStatus.RESOLVED -> "resolve"
            TicketStatus.REOPENED -> "reopen"
            TicketStatus.CLOSED -> "close"
        }
    }
}