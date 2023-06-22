import {getAllProducts, searchProduct} from "./API/Products";
import {addProfile, getProfileByEmail, updateProfile} from "./API/Profiles";

const API = {
    getAllProducts, searchProduct,
    getProfileByEmail, addProfile, updateProfile,
};
export default API;
