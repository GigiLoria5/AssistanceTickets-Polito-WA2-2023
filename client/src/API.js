import {getUserInfo, logIn} from "./API/Auth";
import {getAllProducts, searchProduct} from "./API/Products";
import {addProfile, getProfileByEmail, updateProfile} from "./API/Profiles";

const API = {
    logIn, getUserInfo,
    getAllProducts, searchProduct,
    getProfileByEmail, addProfile, updateProfile,
};

export default API;
