export function ProductToken(productTokenId, createdAt, registeredAt, token, userId, product) {
    this.productTokenId = productTokenId;
    this.createdAt = createdAt;
    this.registeredAt = registeredAt;
    this.token = token;
    this.userId = userId;
    this.product = product;
}
