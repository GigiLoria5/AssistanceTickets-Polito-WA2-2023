export function TicketStatusChange(currentExpertId, oldStatus, newStatus, changedBy, description, time) {
    this.currentExpertId = currentExpertId
    this.oldStatus = oldStatus
    this.newStatus = newStatus
    this.changedBy = changedBy
    this.description = description
    this.time = time
}
