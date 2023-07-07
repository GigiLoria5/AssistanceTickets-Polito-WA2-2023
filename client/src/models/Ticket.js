export function Ticket(ticketId, title, description, productId, productTokenId,customerId, expertId, totalExchangedMessages, status, priorityLevel, createdAt, lastModifiedAt) {
    this.ticketId = ticketId
    this.title = title
    this.description = description
    this.productId = productId
    this.productTokenId=productTokenId
    this.customerId = customerId
    this.expertId = expertId
    this.totalExchangedMessages = totalExchangedMessages
    this.status = status
    this.priorityLevel = priorityLevel
    this.createdAt = createdAt
    this.lastModifiedAt = lastModifiedAt
}
