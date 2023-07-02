import {getUserInfo, logIn, signup} from "./API/Auth";
import {getExpertById} from "./API/Experts";
import {getAllProducts, searchProduct} from "./API/Products";
import {getProfileByEmail, updateProfile} from "./API/Profiles";

const API = {
    signup, logIn, getUserInfo,
    getExpertById,
    getAllProducts, searchProduct,
    getProfileByEmail, updateProfile,
};

export default API;
