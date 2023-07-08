import React, { useEffect, useState } from "react";
import { useStatusAlert } from "../../hooks/useStatusAlert";
import { useNavigate, useParams } from "react-router-dom";
import { Button, Col, Row, Spinner, Table } from "react-bootstrap";
import ExpertProfile from "./Profiles/ExpertProfile";
import Tickets from "./Tickets";
import API from "../API";
import { handleApiError } from "../utils/utils";
import { getTicketStatusChangesByTicketId } from "../API/Tickets";

function ExpertDetails({ userInfo }) {
    const { expertId } = useParams();
    const { StatusAlertComponent, showSuccess, showError, resetStatusAlert } = useStatusAlert();
    const navigate = useNavigate()
    const [loading, setLoading] = useState(true)
    const [load, setLoad] = useState(true)
    const [tickets, setTickets] = useState(null);
    const [statusChanges, setStatusChanges] = useState(null)

    useEffect(() => {
        const getData = async () => {
            await Promise.all([getTickets(), getStatusChanges()])
        }
        if (load === true) {
            setLoading(true)
            getData().then(() => {
                setLoading(false)
                resetStatusAlert()
            }).catch(err => stopAnimationAndShowError(err)).finally(() => {
                setLoad(false)
            })
        }
    }
        ,
        [load]
    )

    const getTickets = () => {
        API.getTicketsOfExpertsByExpertId(expertId)
            .then((t) => {
                setTickets(t)
                resetStatusAlert()
            })
            .catch(err => handleApiError(err, showError))
    }

    const getStatusChanges = () => {
        API.getStatusChangesOfExpertById(expertId)
            .then((statusChanges) => {
                setStatusChanges(statusChanges)
                resetStatusAlert()
            })
            .catch(err => handleApiError(err, showError))
    }


    const formatTickets = () => {
        if (tickets && tickets.length > 0)
            return tickets;
        else
            return [];
    }

    const formatStatusChanges = () => {
        if (statusChanges && statusChanges.length > 0)
            return statusChanges;
        else
            return [];
    }

    return <>
        <Row className='pb-5'>
            <Col md="auto" className="d-flex align-items-center">
                <Button onClick={() => navigate(-1)}> {"Go back"}</Button>
            </Col>
        </Row>
        <Row className='mb-5'>
            <ExpertProfile expertId={expertId} showError={showError}></ExpertProfile>
        </Row>
        <Row className='mb-5'>
            <ExpertTickets tickets={formatTickets()} ></ExpertTickets>
        </Row>
        <Row className='mb-5'>
            <ExpertStatusChanges statusChanges={formatStatusChanges()}></ExpertStatusChanges>
        </Row>
    </>
}

function ExpertTickets({ tickets }) {
    return <>
    <h2>Expert Tickets</h2>
    <Tickets tickets={tickets} title={tickets.length > 0 ? `This expert has ${tickets.length} ticket${tickets.length !== 1 ? "s" : ""}` : ``}></Tickets>
    </>
}

function ExpertStatusChanges({ statusChanges }) {
    return (
        <>
            <h2>Expert Status Changes</h2>
            {
                statusChanges.length > 0 ?
                    <>
                        <Row className="mt-3">
                            <Col>
                                <Table>
                                    <thead>
                                        <tr>
                                            <th>Ticket Id</th>
                                            <th>Old Status</th>
                                            <th>New Status</th>
                                            <th>Current Expert Id</th>
                                            <th>Time</th>
                                            <th>Description</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {statusChanges.map((tsc) => (
                                            <tr key={tsc.time}>
                                                <td>{tsc.ticketId}</td>
                                                <td>{tsc.oldStatus}</td>
                                                <td>{tsc.newStatus}</td>
                                                <td>{tsc.currentExpertId}</td>
                                                <td>{dateTimeMillisFormatted(tsc.time)}</td>
                                                <td>{tsc.description}</td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </Table>
                            </Col>
                        </Row>
                    </> : <div>No status changes found</div>
            }
        </>
    );
}

export default ExpertDetails;