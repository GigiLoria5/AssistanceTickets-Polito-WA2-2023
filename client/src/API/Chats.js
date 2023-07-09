import { API_URL } from "./APIUrl";
import {getAccessToken, handleErrorResponse} from "../utils/utils";
import {SERVER_COMMUNICATION_ERROR} from "../utils/constants";
import {Message} from "../models/Message";

// GET /API/chats/{ticketId}/messages

async function getAllMessagesByTicketId(ticketId){
    return new Promise((resolve,reject) => {
        fetch(new URL(`chats/${ticketId}/messages`),{
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

export {getAllMessagesByTicketId}
