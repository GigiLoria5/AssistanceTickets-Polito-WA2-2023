import {getUserInfo, logIn} from "./API/Auth";
import {getAllProducts, searchProduct} from "./API/Products";
import {addProfile, getProfileByEmail, getTicketsOfProfileByEmail, updateProfile} from "./API/Profiles";
import {getTicketById, getTicketStatusChangesByTicketId} from "./API/Tickets";
import {getAllExperts} from "./API/Experts";
import {startTicket, stopTicket, reopenTicket, closeTicket, resolveTicket} from "./API/TicketStatusChange";

const API = {
    logIn, getUserInfo,
    getAllProducts, searchProduct,
    getProfileByEmail, getTicketsOfProfileByEmail,addProfile, updateProfile,
    getTicketById,getTicketStatusChangesByTicketId,
    getAllExperts,
    startTicket,stopTicket,resolveTicket,reopenTicket,closeTicket
};

export default API;
