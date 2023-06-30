import {API_URL} from "./APIUrl";
import {getAccessToken, handleErrorResponse, setAccessToken} from "../utils/utils";
import {SERVER_COMMUNICATION_ERROR} from "../utils/constants";
import {User} from "../models/User";

// POST /API/auth/login
async function logIn(credentials) {
    return new Promise((resolve, reject) => {
        fetch(new URL(`auth/login`, API_URL), {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            credentials: 'include',
            body: JSON.stringify(credentials),
        })
            .then(async (response) => {
                if (response.ok) {
                    const responseJson = await response.json();
                    const {accessToken} = responseJson;
                    console.log(accessToken);
                    setAccessToken(accessToken)
                    resolve(true)
                } else {
                    const error = await handleErrorResponse(response);
                    reject(error);
                }
            })
            .catch((_error) => reject(SERVER_COMMUNICATION_ERROR));
    });
}

// GET /API/auth/user
async function getUserInfo() {
    return new Promise((resolve, reject) => {
        fetch(new URL(`auth/user`, API_URL), {
            headers: {
                Authorization: `Bearer ${getAccessToken()}`
            },
            credentials: 'include'
        })
            .then(async (response) => {
                if (response.ok) {
                    const user = await response.json();
                    resolve(new User(user.email, user.name, user.role));
                } else {
                    const error = await handleErrorResponse(response);
                    reject(error);
                }
            })
            .catch((_error) => reject(SERVER_COMMUNICATION_ERROR));
    });
}

export {logIn, getUserInfo};