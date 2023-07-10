import {ModalType} from "../enums/ModalType";
import {getTaskToAchieveStatus} from "./ticketUtil";
import {TicketStatus} from "../enums/TicketStatus";
import API from "../API";

export function getModalSize(type) {
    switch (type) {
        case ModalType.CREATE_TICKET:
            return "xl"
        case ModalType.STATUS_CHANGE:
            return "xl"
        case ModalType.CONFIRM_STATUS_CHANGE:
            return "lg"
        case ModalType.CONFIRM_CREATE:
            return "lg"
        case ModalType.REGISTER_PRODUCT:
            return "sm"
        case ModalType.CONFIRM_REGISTER:
            return "lg"
        default:
            return "xl"
    }
}

export function getModalTitle(type, desiredState) {
    switch (type) {
        case ModalType.STATUS_CHANGE:
            return `${getTaskToAchieveStatus(desiredState)} tickets`
        case ModalType.CREATE_TICKET:
            return "Create ticket"
        case ModalType.CONFIRM_STATUS_CHANGE:
            return "Status change completed"
        case ModalType.CONFIRM_CREATE:
            return "Ticket created"
        case ModalType.REGISTER_PRODUCT:
            return "Register purchase"
        case ModalType.CONFIRM_REGISTER:
            return "Product registered"
        default :
            return "Title"
    }
}

export function getUpdateStatusApiCall(desiredStatus) {
    switch (desiredStatus) {
        case TicketStatus.OPEN:
            return (ticketId, description) => API.stopTicket(ticketId, description)
        case TicketStatus.CLOSED:
            return (ticketId, description) => API.closeTicket(ticketId, description)
        case TicketStatus.REOPENED:
            return (ticketId, description) => API.reopenTicket(ticketId, description)
        case TicketStatus.RESOLVED:
            return (ticketId, description) => API.resolveTicket(ticketId, description)
        case TicketStatus.IN_PROGRESS:
            return (ticketId, expertId, priorityLevel, description) => API.startTicket(ticketId, expertId, priorityLevel, description)
    }
}
