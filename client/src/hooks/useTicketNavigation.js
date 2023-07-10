import {useState} from 'react';
import {useNavigate} from 'react-router-dom';

function useTicketNavigation(products, tickets) {
    const navigate = useNavigate();
    const [showClient, setShowClient] = useState(false);

    const actionGoToTicket = (ticket) => {
        navigate(`/tickets/${ticket.ticketId}`);
    };

    const formatTickets = () => {
        return tickets.map((ticket) => {
            const product = products
                ? products.find((p) => p.productId === ticket.productId)
                : '';
            return {...ticket, 'product': product.name};
        });
    };

    return {
        actionGoToTicket,
        formatTickets,
        showClient,
        setShowClient,
    };
}

export default useTicketNavigation;
