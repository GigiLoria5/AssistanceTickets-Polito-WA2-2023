import {getUserInfo, logIn, signup} from "./API/Auth";
import {createExpert, getAllExperts, getExpertById, getTicketsOfExpertsByExpertId, getStatusChangesOfExpertById} from "./API/Experts";
import {getAllProducts, searchProduct,generateToken,registerProduct} from "./API/Products";
import {getProfileById, getTicketsOfProfileByProfileId,getPurchasesByProfileId,getPurchaseByProfileIdAndProductTokenId, updateProfile} from "./API/Profiles";
import {createTicket, getAllTickets, getAllTicketsOpen, getAllTicketsReopened, getTicketById, getTicketStatusChangesByTicketId} from "./API/Tickets";
import {startTicket, stopTicket, reopenTicket, closeTicket, resolveTicket} from "./API/TicketStatusChange";

const API = {
    signup, logIn, getUserInfo,
    getAllExperts, getExpertById, createExpert, getTicketsOfExpertsByExpertId, getStatusChangesOfExpertById,
    getAllProducts, searchProduct, generateToken,registerProduct,
    getProfileById, getTicketsOfProfileByProfileId,getPurchasesByProfileId,getPurchaseByProfileIdAndProductTokenId,updateProfile,
    getAllTickets, getAllTicketsOpen, getAllTicketsReopened, getTicketById,getTicketStatusChangesByTicketId,createTicket,
    startTicket,stopTicket,resolveTicket,reopenTicket,closeTicket

};

export default API;
