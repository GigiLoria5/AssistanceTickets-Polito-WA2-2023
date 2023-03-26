const {Product} = require("./utils/Product");
const APIURL = new URL('http://localhost:8080/API/');


async function getAllProducts() {
    // call: GET /api/products
    return new Promise((resolve, reject) => {
        fetch(new URL(`products`, APIURL),{credentials: 'include'})
            .then(async (response) => {
                if (response.ok) {
                    const productsJson = await response.json();
                    resolve(productsJson.map((p) => (new Product(p.productId, p.asin, p.brand, p.category, p.manufacturerNumber, p.name, p.price, p.weight))));
                } else {
                    reject({error: `server error ${response.status}`})
                }
            })
            .catch(() => {
                reject({error: "Cannot communicate with the server."})
            });
    });
}

async function searchProduct(productId) {
    // call: GET /api/products/:productId
    return new Promise((resolve, reject) => {
        fetch(new URL(`products/${productId}`, APIURL), {credentials: 'include'})
            .then(async (response) => {
                if (response.ok) {
                    const p = await response.json();
                    resolve(new Product(p.productId, p.asin, p.brand, p.category, p.manufacturerNumber, p.name, p.price, p.weight));
                }
                else {
                    reject({error: `server error ${response.status}`})
                }
            })
            .catch(() => {
                reject({error: "Cannot communicate with the server."})
            });
    });
}

const API = {getAllProducts, searchProduct};
export default API;