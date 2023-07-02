import {API_URL} from "./APIUrl";
import {Profile} from "../models/Profile";
import {SERVER_COMMUNICATION_ERROR} from "../utils/constants";
import {getAccessToken, handleErrorResponse} from "../utils/utils";

// GET /API/profiles/{email}
async function getProfileByEmail(email) {
    return new Promise((resolve, reject) => {
        fetch(new URL(`profiles/${email}`, API_URL), {
            headers: {
                Authorization: `Bearer ${getAccessToken()}`
            },
            credentials: 'include'
        })
            .then(async (response) => {
                if (response.ok) {
                    const profile = await response.json();
                    resolve(new Profile(profile.email, profile.name, profile.surname, profile.phoneNumber, profile.address, profile.city, profile.country));
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

export {getProfileByEmail, updateProfile}
