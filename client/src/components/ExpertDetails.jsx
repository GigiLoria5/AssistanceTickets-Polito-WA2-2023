import React, { useEffect, useState } from "react";
import { useStatusAlert } from "../../hooks/useStatusAlert";
import { useNavigate, useParams } from "react-router-dom";
import { Button, Col, Row, Spinner, Table } from "react-bootstrap";
import ExpertProfile from "./Profiles/ExpertProfile";
import Tickets from "./Tickets";
import API from "../API";
import { handleApiError } from "../utils/utils";

function ExpertDetails() {
    const [tickets, setTickets] = useState([]);
    const [statusChanges, setStatusChanges] = useState(null)
    const [products, setProducts] = useState(null);
    const { expertId } = useParams();
    const { StatusAlertComponent, showSuccess, showError, resetStatusAlert } = useStatusAlert();
    const navigate = useNavigate()
    const [loading, setLoading] = useState(true)
    const [errorPresence, setErrorPresence] = useState(false)
    const [load, setLoad] = useState(true)

    useEffect(() => {
        const getData = async () => {
            await Promise.all([getTickets(), getStatusChanges(), getProductsData()])
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

    const stopAnimationAndShowError = (err) => {
        setLoading(false)
        setErrorPresence(true)
        handleApiError(err, showError)
    }

    const getTickets = () => {
        return new Promise((resolve, reject) => {
            API.getTicketsOfExpertsByExpertId(expertId)
                .then((t) => {
                    setTickets(t)
                    resolve()
                }).catch(e => reject(e))
        })
    }

    const getStatusChanges = () => {
        return new Promise((resolve, reject) => {
            API.getStatusChangesOfExpertById(expertId)
                .then((statusChanges) => {
                    setStatusChanges(statusChanges)
                    resolve()
                }).catch(e => reject(e))
        })
    }

    const getProductsData = async () => {
        return new Promise((resolve, reject) => {
            API.getAllProducts()
                .then((p) => {
                    setProducts(p)
                    resolve()
                }
                ).catch(e => reject(e))
        })
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
            <Col md="auto" className="d-flex align-items-center">
                <h1>Expert Details</h1>
            </Col>
        </Row>
        <StatusAlertComponent />
        {loading ?
            <Spinner animation="border" variant="primary" />
            :
            !errorPresence ?
                <>
                    <Row className='mb-5'>
                        <ExpertProfile expertId={expertId} showError={showError}></ExpertProfile>
                    </Row>
                    <Row className='mb-5'>
                        <ExpertTickets tickets={tickets} products={products} ></ExpertTickets>
                    </Row>
                    <Row className='mb-5'>
                        <ExpertStatusChanges statusChanges={formatStatusChanges()}></ExpertStatusChanges>
                    </Row>
                </> : null
        }
    </>
}

function ExpertTickets({ tickets, products }) {
    const formatTickets = () => {
        return tickets.map(ticket => {
            const product = products ? products.find(p => p.productId === ticket.productId) : ""
            return { ...ticket, "product": product.name }
        })
    }
    return <>
        <h2>Expert Tickets</h2>
        <Tickets tickets={formatTickets()} title={tickets.length > 0 ? `This expert has ${tickets.length} ticket${tickets.length !== 1 ? "s" : ""}` : ``}></Tickets>
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