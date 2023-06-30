import {API_URL} from "./APIUrl";
import {SERVER_COMMUNICATION_ERROR} from "../utils/constants";
import {getAccessToken, handleErrorResponse} from "../utils/utils";
import {Expert} from "../models/Expert";
import {Skill} from "../models/Skill";

// GET /API/experts
async function getAllExperts() {
    return new Promise((resolve, reject) => {
        fetch(new URL(`experts`, API_URL), {
            headers: {
                Authorization: `Bearer ${getAccessToken()}`
            },
            credentials: 'include'
        })
            .then(async (response) => {
                if (response.ok) {
                    const expertsJSon = await response.json();
                    resolve(expertsJSon.map((e) => (new Expert(e.expertId, e.name, e.surname, e.email, e.country, e.city, e.skills.map(skill => new Skill(skill.expertise, skill.level))))));
                } else {
                    const error = await handleErrorResponse(response);
                    reject(error);
                }
            })
            .catch((_error) => reject(SERVER_COMMUNICATION_ERROR));
    });
}

export {getAllExperts};
