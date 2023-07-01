import {API_URL} from "./APIUrl";
import {getAccessToken, handleErrorResponse} from "../utils/utils";
import {SERVER_COMMUNICATION_ERROR} from "../utils/constants";
import {Expert} from "../models/Expert";

// GET /API/experts/expertId
async function getExpertById(id) {
    return new Promise((resolve, reject) => {
        fetch(new URL(`experts/${id}`, API_URL), {
            headers: {
                Authorization: `Bearer ${getAccessToken()}`
            },
            credentials: 'include'
        })
            .then(async (response) => {
                if (response.ok) {
                    const expert = await response.json();
                    resolve(new Expert(expert.expertId, expert.name, expert.surname, expert.email, expert.country, expert.city, expert.skills));
                } else {
                    const error = await handleErrorResponse(response);
                    reject(error);
                }
            })
            .catch((_error) => reject(SERVER_COMMUNICATION_ERROR));
    });
}

export {getExpertById};
