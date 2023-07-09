import { API_URL } from "./APIUrl";
import {getAccessToken, handleErrorResponse} from "../utils/utils";
import {SERVER_COMMUNICATION_ERROR} from "../utils/constants";
import {Message} from "../models/Message";

// GET /API/chats/{ticketId}/messages

async function getAllMessagesByTicketId(ticketId){
    return new Promise((resolve,reject) => {
        fetch(new URL(`chats/${ticketId}/messages`, API_URL),{
            headers: {
                Authorization: `Bearer ${getAccessToken()}`
            },
            credentials: 'include'
        })
        .then(async(response) => {
            if(response.ok){
                const body = await response.json();
                resolve(body.map((m) => (new Message(m.messageId,m.sender,m.expertId,m.content,m.attachments,m.time))));
            } else {
                const error = await handleErrorResponse(response);
                reject(error);
            }
        })
        .catch((_error) => reject(SERVER_COMMUNICATION_ERROR));
    })
}

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
                    const contentType = response.headers.get('Content-Type');
                    const contentDisposition = response.headers.get('Content-Disposition');
                    let filename = 'attachment';
                    if (contentDisposition) {
                        const match = contentDisposition.match(/filename\*?=['"]?(?:UTF-\d['"]*)?([^;\r\n"']*)['"]?;?/i);
                        if (match && match[1]) {
                            filename = decodeURIComponent(match[1]);
                        }
                    }
                    const body = await response.blob();
                    resolve({ data: body, contentType, filename });
                } else {
                    const error = await handleErrorResponse(response);
                    reject(error);
                }
            })
            .catch((_error) => reject(SERVER_COMMUNICATION_ERROR));
    });
}

export {getAllMessagesByTicketId, getAttachment}
