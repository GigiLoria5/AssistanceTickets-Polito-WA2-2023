import API from "../../../API";
import {TicketStatus} from "../../../../enums/TicketStatus";
import {ModalType} from "../../../../enums/ModalType";
import {getTaskToAchieveStatus} from "../../../utils/ticketUtil";

function getModalSize(type) {
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

function getModalTitle(type, desiredState) {
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

function getUpdateApiForDesiredStatus(desiredStatus, desiredPostUpdateAction, showError) {
    const startTicket = (ticketId, expertId, priorityLevel, description) => {
        API.startTicket(ticketId, expertId, priorityLevel, description)
            .then(() => {
                    desiredPostUpdateAction()
                }
            )
            .catch(err => showError(err.error))
    }

    const stopTicket = (ticketId, description) => {
        API.stopTicket(ticketId, description)
            .then(() => {
                    desiredPostUpdateAction()
                }
            )
            .catch(err => showError(err.error))
    }

    const resolveTicket = (ticketId, description) => {
        API.resolveTicket(ticketId, description)
            .then(() => {
                    desiredPostUpdateAction()
                }
            )
            .catch(err => showError(err.error))
    }

    const reopenTicket = (ticketId, description) => {
        API.reopenTicket(ticketId, description)
            .then(() => {
                    desiredPostUpdateAction()
                }
            )
            .catch(err => showError(err.error))
    }

    const closeTicket = (ticketId, description) => {
        API.closeTicket(ticketId, description)
            .then(() => {
                    desiredPostUpdateAction()
                }
            )
            .catch(err => showError(err.error))
    }

    switch (desiredStatus) {
        case TicketStatus.OPEN:
            return (ticketId, description) => stopTicket(ticketId, description)
        case TicketStatus.CLOSED:
            return (ticketId, description) => closeTicket(ticketId, description)
        case TicketStatus.REOPENED:
            return (ticketId, description) => reopenTicket(ticketId, description)
        case TicketStatus.RESOLVED:
            return (ticketId, description) => resolveTicket(ticketId, description)
        case TicketStatus.IN_PROGRESS:
            return (ticketId, expertId, priorityLevel, description) => startTicket(ticketId, expertId, priorityLevel, description)
    }
}


export {getModalSize, getModalTitle, getUpdateApiForDesiredStatus}
