const {Product} = require("./utils/Product");
import {Profile} from "./utils/Profile";
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

//Profile API

async function getProfileByEmail(email){
    return new Promise((resolve, reject) => {
        fetch(new URL(`profiles/${email}`, APIURL), {credentials: 'include'})
            .then(async (response) => {
                if (response.ok) {
                    const profile = await response.json();
                    resolve({id:profile.id,email:profile.email,name:profile.name,surname:profile.surname,phoneNumber:profile.phoneNumber,address:profile.address,city:profile.city,country:profile.country});
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

function addProfile(profile){
    return new Promise((resolve,reject) => {
        fetch(new URL('profiles',APIURL), {
            method: 'POST',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(new Profile(profile.email,profile.name,profile.surname,profile.phoneNumber,profile.address,profile.city,profile.country))
        }).then(async (response)=> {
            if(response.ok){
                resolve(null);
            } else {
                const errorBody = await response.json();
                reject({ error: `Server error: ${errorBody.errorMessage}` });
            }
        }).catch(()=> reject({error:"cannot communicate withe the server"}));
    });
}
function updateProfile(profile,email){
    return new Promise((resolve,reject) => {
        fetch(new URL(`profiles/${email}`,APIURL), {
            method: 'PUT',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(new Profile(profile.email,profile.name,profile.surname,profile.phoneNumber,profile.address,profile.city,profile.country))
        }).then(async (response)=> {
            if(response.ok){
                resolve(null);
            } else {
                const errorBody = await response.json();
                reject({ error: `Server error: ${errorBody.errorMessage}` });
            }
        }).catch(()=> reject({error:"cannot communicate withe the server"}));
    });
}

const API = {getAllProducts, searchProduct,getProfileByEmail,addProfile,updateProfile};
export default API;