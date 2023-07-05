import {getUserInfo, logIn, signup} from "./API/Auth";
import {createExpert, getAllExperts, getExpertById} from "./API/Experts";
import {getAllProducts, searchProduct,generateToken,registerProduct} from "./API/Products";
import {getProfileById, getTicketsOfProfileByProfileId, getProductsOfProfileByProfileId,updateProfile} from "./API/Profiles";
import {createTicket, getTicketById, getTicketStatusChangesByTicketId} from "./API/Tickets";
import {startTicket, stopTicket, reopenTicket, closeTicket, resolveTicket} from "./API/TicketStatusChange";

const API = {
    signup, logIn, getUserInfo,
    getAllExperts, getExpertById, createExpert,
    getAllProducts, searchProduct, generateToken,registerProduct,
    getProfileById, getTicketsOfProfileByProfileId,getProductsOfProfileByProfileId,updateProfile,
    getTicketById,getTicketStatusChangesByTicketId,createTicket,
    startTicket,stopTicket,resolveTicket,reopenTicket,closeTicket

};

export default API;
