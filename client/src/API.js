import {getUserInfo, logIn} from "./API/Auth";
import {getExpertById} from "./API/Experts";
import {getAllProducts, searchProduct} from "./API/Products";
import {addProfile, getProfileByEmail, updateProfile} from "./API/Profiles";

const API = {
    logIn, getUserInfo,
    getExpertById,
    getAllProducts, searchProduct,
    getProfileByEmail, addProfile, updateProfile,
};

export default API;
