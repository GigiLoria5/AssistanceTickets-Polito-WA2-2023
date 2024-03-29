import {API_URL} from "./APIUrl";
import {getAccessToken, handleErrorResponse} from "../utils/utils";
import {SERVER_COMMUNICATION_ERROR} from "../utils/constants";
import {Message} from "../models/Message";

// GET /API/chats/{ticketId}/messages
async function getAllMessagesByTicketId(ticketId) {
    return new Promise((resolve, reject) => {
        fetch(new URL(`chats/${ticketId}/messages`, API_URL), {
            headers: {
                Authorization: `Bearer ${getAccessToken()}`
            },
            credentials: 'include'
        })
            .then(async (response) => {
                if (response.ok) {
                    const body = await response.json();
                    resolve(body.map((m) => (new Message(m.messageId, m.sender, m.expertId, m.content, m.attachments, m.time))));
                } else {
                    const error = await handleErrorResponse(response);
                    reject(error);
                }
            })
            .catch((_error) => reject(SERVER_COMMUNICATION_ERROR));
    })
}

// GET /API/chats/{ticketId}/messages/{messageId}/attachment/{attachmentsId}

async function getAttachment(ticketId, messageId, attachmentId) {
    return new Promise((resolve, reject) => {
        fetch(new URL(`chats/${ticketId}/messages/${messageId}/attachments/${attachmentId}`, API_URL), {
            headers: {
                Authorization: `Bearer ${getAccessToken()}`
            },
            credentials: 'include'
        })
            .then(async (response) => {
                if (response.ok) {
                    const body = await response.blob();
                    resolve(body);
                } else {
                    const error = await handleErrorResponse(response);
                    reject(error);
                }
            })
            .catch((_error) => reject(SERVER_COMMUNICATION_ERROR));
    });
}

// POST /API/chats/{ticketId}/messages

async function addMessageWithAttachments(ticketId, content, attachments = []) {

    const formData = new FormData();
    formData.append('content', content);
    if (attachments && attachments.length > 0) {
        Array.from(attachments).forEach((attachment) => {
            formData.append('attachments', attachment);
        });
    }
    return new Promise((resolve, reject) => {
        fetch(new URL(`chats/${ticketId}/messages`, API_URL), {
                method: 'POST',
                headers: {
                    Authorization: `Bearer ${getAccessToken()}`
                },
                credentials: 'include',
                body: formData,
            }
        )
            .then(async (response) => {
                if (response.ok) {
                    resolve(null)
                } else {
                    const error = await handleErrorResponse(response);
                    reject(error)
                }
            })
            .catch((_error) => reject(SERVER_COMMUNICATION_ERROR));
    })
}

export {getAllMessagesByTicketId, getAttachment, addMessageWithAttachments}
