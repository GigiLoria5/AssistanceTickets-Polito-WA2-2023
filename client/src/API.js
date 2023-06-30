import {getUserInfo, logIn} from "./API/Auth";
import {getAllProducts, searchProduct} from "./API/Products";
import {addProfile, getProfileByEmail, updateProfile} from "./API/Profiles";
import {getTicketById, getTicketStatusChangesByTicketId} from "./API/Tickets";
import {getAllExperts} from "./API/Experts";

const API = {
    logIn, getUserInfo,
    getAllProducts, searchProduct,
    getProfileByEmail, addProfile, updateProfile,
    getTicketById,getTicketStatusChangesByTicketId,
    getAllExperts
};

export default API;
