import {API_URL} from "./APIUrl";
import {Profile} from "../models/Profile";
import {SERVER_COMMUNICATION_ERROR} from "../utils/constants";
import {getAccessToken, handleErrorResponse} from "../utils/utils";
import {Ticket} from "../models/Ticket";
import {ProductToken} from "../models/ProductToken";
import {Product} from "../models/Product";

// GET /API/profiles/{profileId}
async function getProfileById(id) {
    return new Promise((resolve, reject) => {
        fetch(new URL(`profiles/${id}`, API_URL), {
            headers: {
                Authorization: `Bearer ${getAccessToken()}`
            },
            credentials: 'include'
        })
            .then(async (response) => {
                if (response.ok) {
                    const body = await response.json();
                    resolve(new Profile(body.profileId, body.email, body.name, body.surname, body.phoneNumber, body.address, body.city, body.country));
                } else {
                    const error = await handleErrorResponse(response);
                    reject(error);
                }
            })
            .catch((_error) => reject(SERVER_COMMUNICATION_ERROR));
    });
}

// PUT /API/profiles
function updateProfile(profile) {
    console.log(profile)

    return new Promise((resolve, reject) => {
        fetch(new URL(`profiles`, API_URL), {
            method: 'PUT',
            credentials: 'include',
            headers: {
                Authorization: `Bearer ${getAccessToken()}`,
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                name: profile.name,
                surname: profile.surname,
                phoneNumber: profile.phoneNumber,
                address: profile.address,
                city: profile.city,
                country: profile.country
            })
        })
            .then(async (response) => {
                if (response.ok) {
                    resolve(null);
                } else {
                    const error = await handleErrorResponse(response);
                    reject(error);
                }
            })
            .catch((_error) => reject(SERVER_COMMUNICATION_ERROR));
    });
}

// GET /API/profiles/{profileId}/tickets
async function getTicketsOfProfileByProfileId(profileId) {
    return new Promise((resolve, reject) => {
        fetch(new URL(`profiles/${profileId}/tickets`, API_URL), {
            headers: {
                Authorization: `Bearer ${getAccessToken()}`
            },
            credentials: 'include'
        })
            .then(async (response) => {
                if (response.ok) {
                    const ticketsJson = await response.json();
                    resolve(ticketsJson.map((ticketJson) => (
                                new Ticket(
                                    ticketJson.ticketId,
                                    ticketJson.title,
                                    ticketJson.description,
                                    ticketJson.productId,
                                    ticketJson.productTokenId,
                                    ticketJson.customerId,
                                    ticketJson.expertId,
                                    ticketJson.totalExchangedMessages,
                                    ticketJson.status,
                                    ticketJson.priorityLevel,
                                    ticketJson.createdAt,
                                    ticketJson.lastModifiedAt
                                )
                            )
                        )
                    )
                } else {
                    const error = await handleErrorResponse(response);
                    reject(error);
                }
            })
            .catch((_error) => reject(SERVER_COMMUNICATION_ERROR));
    });
}

// GET /API/profiles/{profileId}/products
async function getPurchasesByProfileId(profileId) {
    return new Promise((resolve, reject) => {
        fetch(new URL(`profiles/${profileId}/products`, API_URL), {
            headers: {
                Authorization: `Bearer ${getAccessToken()}`
            },
            credentials: 'include'
        })
            .then(async (response) => {
                if (response.ok) {
                    const purchasesJson = await response.json();
                    resolve(purchasesJson.map((purchase) => (
                                new ProductToken(
                                    purchase.productTokenId,
                                    purchase.createdAt,
                                    purchase.registeredAt,
                                    purchase.token,
                                    purchase.userId,
                                    new Product(
                                        purchase.product.productId,
                                        purchase.product.asin,
                                        purchase.product.brand,
                                        purchase.product.category,
                                        purchase.product.manufacturerNumber,
                                        purchase.product.name,
                                        purchase.product.price,
                                        purchase.product.weight
                                    )
                                )
                            )
                        )
                    )
                } else {
                    const error = await handleErrorResponse(response);
                    reject(error);
                }
            })
            .catch((_error) => reject(SERVER_COMMUNICATION_ERROR));
    });
}

// GET /API/profiles/{profileId}/products/{productTokenId}
async function getPurchaseByProfileIdAndProductTokenId(profileId, productTokenId) {
    return new Promise((resolve, reject) => {
        fetch(new URL(`profiles/${profileId}/products/${productTokenId}`, API_URL), {
            headers: {
                Authorization: `Bearer ${getAccessToken()}`
            },
            credentials: 'include'
        })
            .then(async (response) => {
                if (response.ok) {
                    const purchase = await response.json();
                    resolve(
                        new ProductToken(
                            purchase.productTokenId,
                            purchase.createdAt,
                            purchase.registeredAt,
                            purchase.token,
                            purchase.userId,
                            new Product(
                                purchase.product.productId,
                                purchase.product.asin,
                                purchase.product.brand,
                                purchase.product.category,
                                purchase.product.manufacturerNumber,
                                purchase.product.name,
                                purchase.product.price,
                                purchase.product.weight
                            )
                        )
                    );
                } else {
                    const error = await handleErrorResponse(response);
                    reject(error);
                }
            })
            .catch((_error) => reject(SERVER_COMMUNICATION_ERROR));
    });
}

export {getProfileById, updateProfile, getTicketsOfProfileByProfileId, getPurchasesByProfileId,getPurchaseByProfileIdAndProductTokenId}
