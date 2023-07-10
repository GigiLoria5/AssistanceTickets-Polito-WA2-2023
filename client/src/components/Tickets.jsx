import {Button, Col, Row, Table} from "react-bootstrap";
import React from "react";
import {dateTimeMillisFormatted} from "../utils/utils";
import {TicketPriority} from "../enums/TicketPriority";

function Tickets({tickets, title, actionName, action, showClientInfo, showExpertInfo, hidePriority, sorting}) {

    /* Here it is not possible to load the tickets list here, because this component can be used,
    * for example, both from client (which sees only his ticket) and manager (which sees all tickets)*/

    const sortedTickets = (sorting) => {
        const priorityOrder = Object.values(TicketPriority)
        switch (sorting) {
            case "Created At":
                return tickets.sort((t1, t2) => t2.createdAt - t1.createdAt)
            case "Priority Level":
                return tickets.sort((t1, t2) => priorityOrder.indexOf(t2.priorityLevel) - priorityOrder.indexOf(t1.priorityLevel))
            default:
                return tickets.sort((t1, t2) => t2.lastModifiedAt - t1.lastModifiedAt)
        }
    }

    return (
        <>
            <h4>
                {title}
            </h4>
            <Row>
                <Col>
                    <TicketsTable tickets={sortedTickets(sorting)} actionName={actionName} action={action}
                                  showClientInfo={showClientInfo} showExpertInfo={showExpertInfo}
                                  hidePriority={hidePriority}/>
                </Col>
            </Row>
        </>

    )
}

function TicketsTable({tickets, actionName, action, showClientInfo, showExpertInfo, hidePriority}) {

    const handleShowExpert = () => {
        return showExpertInfo ? <td>{null}</td> : null;
    }

    return (
        (tickets  && tickets.length) > 0 ?
            <div className="table-responsive">
                <Table>
                    <thead>
                    <tr>
                        {tickets[0].product !== undefined ?
                            <th>Product</th>
                            : <th>Product id</th>
                        }
                        <th>Title</th>
                        <th>Description</th>
                        {showClientInfo ? <th>Customer Info</th> : null}
                        {showExpertInfo ? <th>Expert Info</th> : null}
                        <th>Status</th>
                        {hidePriority ? null : <th>Priority Level</th>}
                        <th>Created At</th>
                        <th>Last Modified At</th>
                        <th></th>

                    </tr>
                    </thead>
                    <tbody>
                    {tickets.map((ticket) => (
                        <tr key={ticket.ticketId}>
                            {ticket.product !== undefined ?
                                <td>{ticket.product}</td>
                                : <td>{ticket.productId}</td>
                            }
                            <td>{ticket.title}</td>
                            <td>{ticket.description}</td>
                            {showClientInfo ?
                                <td><Button onClick={() => showClientInfo(ticket.customerId)}>Show</Button></td> : null}
                            {showExpertInfo && ticket.expertId
                                ? <td>
                                    <Button onClick={() => showExpertInfo(ticket.expertId)}>Show</Button>
                                </td>
                                : handleShowExpert()
                            }
                            <td>{ticket.status}</td>
                            {hidePriority ? null : <td>{ticket.priorityLevel}</td>}
                            <td>{dateTimeMillisFormatted(ticket.createdAt)}</td>
                            <td>{dateTimeMillisFormatted(ticket.lastModifiedAt)}</td>
                            {action !== undefined ?
                                <td>
                                    <Button onClick={() => action(ticket)}>
                                        {actionName}
                                    </Button>
                                </td>
                                : null
                            }
                        </tr>
                    ))}
                    </tbody>
                </Table>
            </div> : <div>No tickets found</div>

    );

}


export default Tickets;
