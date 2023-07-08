import { Button, Col, Row, Table } from "react-bootstrap";
import React from "react";
import { dateTimeMillisFormatted } from "../utils/utils";

function Tickets({ tickets, title, actionName, action, showClientInfo, showExpertInfo, showInfoToManager }) {

    /* Here it is not possible to load the tickets list here, because this component can be used,
    * for example, both from client (which sees only his ticket) and manager (which sees all tickets)*/

    return (
        <>
            <h4>
                {title}
            </h4>
            <Row>
                <Col>
                    <TicketsTable tickets={tickets} actionName={actionName}
                        action={action} showClientInfo={showClientInfo} showExpertInfo={showExpertInfo} showInfoToManager={showInfoToManager} />
                </Col>
            </Row>
        </>

    )
}

function TicketsTable({ tickets, actionName, action, showClientInfo, showExpertInfo, showInfoToManager }) {
    return (
        (tickets.length) > 0 ?
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
                            {showClientInfo ? <th>Customer info</th> : null}
                            {showExpertInfo ? <th>Expert info</th> : null}
                            {showInfoToManager ? <th>Total Exchanged Messages</th> : null}
                            <th>Status</th>
                            <th>Priority Level</th>
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
                                <td>{showClientInfo ? <Button onClick={() => showClientInfo(ticket.customerId)}>Show</Button>  : null}</td>
                                <td>{showExpertInfo && ticket.expertId ? <Button onClick={() => showExpertInfo(ticket.expertId)}>Show</Button> : null}</td>
                                {showInfoToManager ? <td>{ticket.totalExchangedMessages}</td> : null}
                                <td>{ticket.status}</td>
                                <td>{ticket.priorityLevel}</td>
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
