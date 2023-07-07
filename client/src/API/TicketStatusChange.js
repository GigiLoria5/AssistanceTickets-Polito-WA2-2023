import {API_URL} from "./APIUrl";
import {getAccessToken, handleErrorResponse} from "../utils/utils";
import {SERVER_COMMUNICATION_ERROR} from "../utils/constants";

// PUT /API/tickets/{ticketId}/start
async function startTicket(ticketId, expertId, priorityLevel, description) {
    return new Promise((resolve, reject) => {
        fetch(new URL(`tickets/${ticketId}/start`, API_URL), {
            method: 'PUT',
            credentials: 'include',

            headers: {
                Authorization: `Bearer ${getAccessToken()}`,
                'Content-Type': 'application/json',

            },
            body: JSON.stringify({
                expertId, priorityLevel, description
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


// PUT /API/tickets/{ticketId}/stop
async function stopTicket  (ticketId,description) {
    return new Promise((resolve, reject) => {
        fetch(new URL(`tickets/${ticketId}/stop`, API_URL), {
            headers: {
                Authorization: `Bearer ${getAccessToken()}`,
                'Content-Type': 'application/json',
            },
            method: 'PUT',
            credentials: 'include',
            body: JSON.stringify({
                description
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

// PUT /API/tickets/{ticketId}/resolve
async function resolveTicket  (ticketId,description) {
    return new Promise((resolve, reject) => {
        fetch(new URL(`tickets/${ticketId}/resolve`, API_URL), {
            headers: {
                Authorization: `Bearer ${getAccessToken()}`,
                'Content-Type': 'application/json',
            },
            method: 'PUT',
            credentials: 'include',
            body: JSON.stringify({
                description
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

// PUT /API/tickets/{ticketId}/reopen
async function reopenTicket  (ticketId,description) {
    return new Promise((resolve, reject) => {
        fetch(new URL(`tickets/${ticketId}/reopen`, API_URL), {
            headers: {
                Authorization: `Bearer ${getAccessToken()}`,
                'Content-Type': 'application/json',
            },
            method: 'PUT',
            credentials: 'include',
            body: JSON.stringify({
                description
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

// PUT /API/tickets/{ticketId}/close
async function closeTicket  (ticketId,description) {
    return new Promise((resolve, reject) => {
        fetch(new URL(`tickets/${ticketId}/close`, API_URL), {
            headers: {
                Authorization: `Bearer ${getAccessToken()}`,
                'Content-Type': 'application/json',
            },
            method: 'PUT',
            credentials: 'include',
            body: JSON.stringify({
                description
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



export {startTicket,stopTicket,resolveTicket,reopenTicket,closeTicket};
