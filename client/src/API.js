import {getUserInfo, logIn, signup} from "./API/Auth";
import {createExpert, getAllExperts, getExpertById, getTicketsOfExpertsByExpertId} from "./API/Experts";
import {getAllProducts, searchProduct,generateToken,registerProduct} from "./API/Products";
import {getProfileById, getTicketsOfProfileByProfileId,getPurchasesByProfileId,getPurchaseByProfileIdAndProductTokenId, updateProfile} from "./API/Profiles";
import {createTicket, getTicketById, getTicketStatusChangesByTicketId} from "./API/Tickets";
import {startTicket, stopTicket, reopenTicket, closeTicket, resolveTicket} from "./API/TicketStatusChange";
import {getAllMessagesByTicketId, getAttachment, addMessageWithAttachments} from "./API/Chats";

const API = {
    signup, logIn, getUserInfo,
    getAllExperts, getExpertById, createExpert, getTicketsOfExpertsByExpertId,
    getAllProducts, searchProduct, generateToken,registerProduct,
    getProfileById, getTicketsOfProfileByProfileId,getPurchasesByProfileId,getPurchaseByProfileIdAndProductTokenId,updateProfile,
    getTicketById,getTicketStatusChangesByTicketId,createTicket,
    startTicket,stopTicket,resolveTicket,reopenTicket,closeTicket,
    getAllMessagesByTicketId, getAttachment,addMessageWithAttachments

};

export default API;
