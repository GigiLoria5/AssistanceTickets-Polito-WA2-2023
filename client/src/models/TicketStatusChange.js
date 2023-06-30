export function TicketStatusChange(id, currentExpertId, oldStatus, newStatus, changedBy, description, time) {
    this.id = id
    this.currentExpertId = currentExpertId
    this.oldStatus = oldStatus
    this.newStatus = newStatus
    this.changedBy = changedBy
    this.description = description
    this.time = time
}
