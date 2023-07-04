import {API_URL} from "./APIUrl";
import {Product} from "../models/Product";
import {SERVER_COMMUNICATION_ERROR} from "../utils/constants";
import {getAccessToken, handleErrorResponse} from "../utils/utils";

// GET /API/products
async function getAllProducts() {
    return new Promise((resolve, reject) => {
        fetch(new URL(`products`, API_URL), {
            headers: {
                Authorization: `Bearer ${getAccessToken()}`
            },
            credentials: 'include'
        })
            .then(async (response) => {
                if (response.ok) {
                    const body = await response.json();
                    resolve(body.map((p) => (new Product(p.productId, p.asin, p.brand, p.category, p.manufacturerNumber, p.name, p.price, p.weight))));
                } else {
                    const error = await handleErrorResponse(response);
                    reject(error);
                }
            })
            .catch((_error) => reject(SERVER_COMMUNICATION_ERROR));
    });
}

// GET /API/products/{productId}
async function searchProduct(productId) {
    return new Promise((resolve, reject) => {
        fetch(new URL(`products/${productId}`, API_URL), {
            headers: {
                Authorization: `Bearer ${getAccessToken()}`
            },
            credentials: 'include'
        })
            .then(async (response) => {
                if (response.ok) {
                    const body = await response.json();
                    resolve(new Product(body.productId, body.asin, body.brand, body.category, body.manufacturerNumber, body.name, body.price, body.weight));
                } else {
                    const error = await handleErrorResponse(response);
                    reject(error);
                }
            })
            .catch((_error) => reject(SERVER_COMMUNICATION_ERROR));
    });
}

// POST /API/products/{productId}/token
async function generateToken(productId) {
    return new Promise((resolve, reject) => {
        fetch(new URL(`products/${productId}/token`, API_URL), {
            method: 'POST',
            headers: {
                Authorization: `Bearer ${getAccessToken()}`
            },
            credentials: 'include'
        })
            .then(async (response) => {
                if (response.ok) {
                    const body = await response.json()
                    resolve(body.token);
                } else {
                    const error = await handleErrorResponse(response);
                    reject(error);
                }
            })
            .catch((_error) => reject(SERVER_COMMUNICATION_ERROR));
    });
}

export {getAllProducts, searchProduct, generateToken};
