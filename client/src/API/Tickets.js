import {API_URL} from "./APIUrl";
import {getAccessToken, handleErrorResponse} from "../utils/utils";
import {SERVER_COMMUNICATION_ERROR} from "../utils/constants";
import {Ticket} from "../models/Ticket";
import {TicketStatusChange} from "../models/TicketStatusChange";

// GET /API/tickets/{ticketId}
async function getTicketById(ticketId) {
    return new Promise((resolve, reject) => {
        fetch(new URL(`tickets/${ticketId}`, API_URL), {
            headers: {
                Authorization: `Bearer ${getAccessToken()}`
            },
            credentials: 'include'
        })
            .then(async (response) => {
                if (response.ok) {
                    const ticketJson = await response.json();
                    resolve(new Ticket(
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
                    ));
                } else {
                    const error = await handleErrorResponse(response);
                    reject(error);
                }
            })
            .catch((_error) => reject(SERVER_COMMUNICATION_ERROR));
    });
}

// GET /API/tickets/{ticketId}/statusChanges
async function getTicketStatusChangesByTicketId(ticketId) {
    return new Promise((resolve, reject) => {
        fetch(new URL(`tickets/${ticketId}/statusChanges`, API_URL), {
            headers: {
                Authorization: `Bearer ${getAccessToken()}`
            },
            credentials: 'include'
        })
            .then(async (response) => {
                if (response.ok) {
                    const ticketStatusChangeJSon = await response.json();
                    resolve(
                        ticketStatusChangeJSon.map((tsc) => new TicketStatusChange(tsc.currentExpertId, tsc.oldStatus, tsc.newStatus, tsc.changedBy, tsc.description, tsc.time))
                    );
                } else {
                    const error = await handleErrorResponse(response);
                    reject(error);
                }
            })
            .catch((_error) => reject(SERVER_COMMUNICATION_ERROR));
    });
}

// POST /API/tickets

async function createTicket(productTokenId, title, description) {
    return new Promise((resolve, reject) => {
        fetch(new URL(`tickets`, API_URL), {
            method: 'POST',
            headers: {
                Authorization: `Bearer ${getAccessToken()}`,
                'Content-Type': 'application/json',
            },
            credentials: 'include',
            body: JSON.stringify({
                productTokenId, title, description
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

export {getTicketById, getTicketStatusChangesByTicketId,createTicket};
