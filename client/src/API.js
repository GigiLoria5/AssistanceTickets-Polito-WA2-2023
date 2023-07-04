import {getUserInfo, logIn, signup} from "./API/Auth";
import {getExpertById} from "./API/Experts";
import {generateToken, getAllProducts, searchProduct} from "./API/Products";
import {getProfileById, updateProfile} from "./API/Profiles";

const API = {
    signup, logIn, getUserInfo,
    getExpertById,
    getAllProducts, searchProduct, generateToken,
    getProfileById, updateProfile,
};

export default API;
