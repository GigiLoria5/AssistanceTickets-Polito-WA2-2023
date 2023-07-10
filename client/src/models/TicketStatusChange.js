export function TicketStatusChange(ticketId, currentExpertId, oldStatus, newStatus, changedBy, description, time) {
    this.ticketId = ticketId
    this.currentExpertId = currentExpertId
    this.oldStatus = oldStatus
    this.newStatus = newStatus
    this.changedBy = changedBy
    this.description = description
    this.time = time
}
