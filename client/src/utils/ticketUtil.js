import {UserRole} from "../../enums/UserRole";
import {TicketStatus} from "../../enums/TicketStatus";

const allowedTicketStatusChanges = (ticketStatus) => {
    switch (ticketStatus) {
        case TicketStatus.OPEN:
            return [TicketStatus.IN_PROGRESS,
                TicketStatus.RESOLVED,
                TicketStatus.CLOSED]
        case TicketStatus.IN_PROGRESS:
            return [TicketStatus.OPEN,
                TicketStatus.RESOLVED,
                TicketStatus.CLOSED];
        case TicketStatus.RESOLVED:
            return [TicketStatus.REOPENED,
                TicketStatus.CLOSED];
        case TicketStatus.REOPENED:
            return [TicketStatus.IN_PROGRESS,
                TicketStatus.RESOLVED,
                TicketStatus.CLOSED];
        case TicketStatus.CLOSED:
            return [TicketStatus.REOPENED];
        default:
            return null;
    }
}

const allowedStatusChangesPerRole = (userRole) => {
    switch (userRole) {
        case UserRole.MANAGER:
            return [TicketStatus.IN_PROGRESS,
                TicketStatus.OPEN,
                TicketStatus.RESOLVED,
                TicketStatus.CLOSED]
        case UserRole.EXPERT:
            return [TicketStatus.OPEN,
                TicketStatus.RESOLVED,
                TicketStatus.CLOSED]
        case UserRole.CLIENT:
            return [TicketStatus.RESOLVED,
                TicketStatus.REOPENED]
        default:
            return null;
    }
}

export const availableTicketStatusChanges = (userRole, ticketStatus) => {
    const allowedTicketStatusChangesList = allowedTicketStatusChanges(ticketStatus)
    const allowedStatusChangesPerRoleList = allowedStatusChangesPerRole(userRole)
    return allowedTicketStatusChangesList.filter(newStatus => allowedStatusChangesPerRoleList.includes(newStatus))
}

export const getTaskToAchieveStatus = (ticketStatus) => {
    switch (ticketStatus) {
        case TicketStatus.IN_PROGRESS :
            return "Start"
        case TicketStatus.OPEN :
            return "Stop"
        case TicketStatus.RESOLVED :
            return "Resolve"
        case TicketStatus.REOPENED :
            return "Reopen"
        case TicketStatus.CLOSED :
            return "Close"
    }
}
