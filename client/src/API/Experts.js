import {API_URL} from "./APIUrl";
import {getAccessToken, handleErrorResponse} from "../utils/utils";
import {SERVER_COMMUNICATION_ERROR} from "../utils/constants";
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
                    const body = await response.json();
                    resolve(body.map((exp) => (new Expert(exp.expertId, exp.name, exp.surname, exp.email, exp.country, exp.city, exp.skills.map(skill => new Skill(skill.expertise, skill.level))))));
                } else {
                    const error = await handleErrorResponse(response);
                    reject(error);
                }
            })
            .catch((_error) => reject(SERVER_COMMUNICATION_ERROR));
    });
}

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
                    const body = await response.json();
                    resolve(new Expert(body.expertId, body.name, body.surname, body.email, body.country, body.city, body.skills.map(skill => new Skill(skill.expertise, skill.level))));
                } else {
                    const error = await handleErrorResponse(response);
                    reject(error);
                }
            })
            .catch((_error) => reject(SERVER_COMMUNICATION_ERROR));
    });
}

// POST /API/experts/createExpert
async function createExpert(expert) {
    return new Promise((resolve, reject) => {
        fetch(new URL('experts/createExpert', API_URL), {
            method: 'POST',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json',
                Authorization: `Bearer ${getAccessToken()}`
            },
            body: JSON.stringify(expert)
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

export {getAllExperts, getExpertById, createExpert};
