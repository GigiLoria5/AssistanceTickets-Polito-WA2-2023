import {API_URL} from "./APIUrl";
import {Product} from "../models/Product";
import {SERVER_COMMUNICATION_ERROR} from "../utils/constants";
import {handleErrorResponse} from "../utils/utils";

// GET /API/products
async function getAllProducts() {
    return new Promise((resolve, reject) => {
        fetch(new URL(`products`, API_URL), {credentials: 'include'})
            .then(async (response) => {
                if (response.ok) {
                    const productsJson = await response.json();
                    resolve(productsJson.map((p) => (new Product(p.productId, p.asin, p.brand, p.category, p.manufacturerNumber, p.name, p.price, p.weight))));
                } else {
                    const error = await handleErrorResponse(response);
                    reject(error);
                }
            })
            .catch(() => reject({error: SERVER_COMMUNICATION_ERROR}));
    });
}

// GET /API/products/{productId}
async function searchProduct(productId) {
    return new Promise((resolve, reject) => {
        fetch(new URL(`products/${productId}`, API_URL), {credentials: 'include'})
            .then(async (response) => {
                if (response.ok) {
                    const p = await response.json();
                    resolve(new Product(p.productId, p.asin, p.brand, p.category, p.manufacturerNumber, p.name, p.price, p.weight));
                } else {
                    const error = await handleErrorResponse(response);
                    reject(error);
                }
            })
            .catch(() => reject({error: SERVER_COMMUNICATION_ERROR}));
    });
}

export {getAllProducts, searchProduct};
