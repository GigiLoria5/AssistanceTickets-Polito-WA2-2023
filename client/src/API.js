import {getUserInfo, logIn, signup} from "./API/Auth";
import {createExpert, getAllExperts, getExpertById} from "./API/Experts";
import {getAllProducts, searchProduct,generateToken} from "./API/Products";
import {getProfileById, getTicketsOfProfileByProfileId, updateProfile} from "./API/Profiles";
import {createTicket, getTicketById, getTicketStatusChangesByTicketId} from "./API/Tickets";
import {startTicket, stopTicket, reopenTicket, closeTicket, resolveTicket} from "./API/TicketStatusChange";

const API = {
    signup, logIn, getUserInfo,
    getAllExperts, getExpertById, createExpert,
    getAllProducts, searchProduct, generateToken,
    getProfileById, getTicketsOfProfileByProfileId,updateProfile,
    getTicketById,getTicketStatusChangesByTicketId,createTicket,
    startTicket,stopTicket,resolveTicket,reopenTicket,closeTicket

};

export default API;
