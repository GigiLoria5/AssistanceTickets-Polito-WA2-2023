export function Ticket(id, title, description, productId, customerId, expertId, totalExchangedMessages, status, priorityLevel, createdAt, lastModifiedAt) {
    this.id = id
    this.title = title
    this.description = description
    this.productId = productId
    this.customerId = customerId
    this.expertId = expertId
    this.totalExchangedMessages = totalExchangedMessages
    this.status = status
    this.priorityLevel = priorityLevel
    this.createdAt = createdAt
    this.lastModifiedAt = lastModifiedAt
}
