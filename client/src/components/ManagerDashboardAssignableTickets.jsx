import React, {useEffect, useState} from "react";
import {useStatusAlert} from "../../hooks/useStatusAlert";
import API from "../API";
import {Col, Container, Row, Spinner} from "react-bootstrap";
import {handleApiError} from "../utils/utils";
import Tickets from "./Tickets";
import {useNavigate} from "react-router-dom";
import ClientInfoCanvas from "./ClientInfoCanvas";

function ManagerDashboardAssignableTickets() {
    const [tickets, setTickets] = useState(null);
    const [products, setProducts] = useState(null);
    const [clientInfo, setClientInfo] = useState(null)
    const [loading, setLoading] = useState(true)
    const [load, setLoad] = useState(true)
    const {StatusAlertComponent, showError, resetStatusAlert} = useStatusAlert();

    useEffect(() => {
            const getData = async () => {
                await Promise.all([getAssignableTicketsData(), getProductsData()])
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

    const getAssignableTicketsData = async () => {
        return new Promise((resolve, reject) => {
            API.getAllTickets()
                .then((t) => {
                        setTickets(t.filter(ticket => !ticket.expertId))
                        resolve()
                    }
                ).catch(err => reject(err))
        })
    }

    const getProductsData = async () => {
        return new Promise((resolve, reject) => {
            API.getAllProducts()
                .then((p) => {
                        setProducts(p)
                        resolve()
                    }
                ).catch(err => reject(err))
        })
    }

    const getClientInfo = (clientId) => {
        API.getProfileById(clientId)
            .then((client) => {
                setClientInfo(client)
                resetStatusAlert()
            })
            .catch(err => handleApiError(err, showError))
    }

    const navigate = useNavigate();

    const actionGoToTicket = (ticket) => {
        navigate(`/tickets/${ticket.ticketId}`)
    }

    const formatTickets = () => {
        return tickets.map(ticket => {
            const product = products ? products.find(p => p.productId === ticket.productId) : ""
            return {...ticket, "product": product.name}
        })
    }

    const [showClient, setShowClient] = useState(false)

    const handleCloseClient = () => setShowClient(false)

    const handleShowClientInfo = (clientId) => {
        getClientInfo(clientId);
        setShowClient(true)
    }

    return (
        <Container className="h-100">
            <Row className="h-100">
                <Col className="d-flex flex-column align-items-center justify-content-center">
                    <h2>Tickets to assign</h2><StatusAlertComponent/>
                    {
                        tickets && !loading ?
                            <>
                                <Tickets tickets={formatTickets()}
                                         actionName={"Details"}
                                         action={actionGoToTicket}
                                         showClientInfo={handleShowClientInfo}
                                         hidePriority={true}
                                />
                                {clientInfo
                                    ? <ClientInfoCanvas show={showClient} onHide={handleCloseClient}
                                                        clientInfo={clientInfo}/>
                                    : null
                                }
                            </>
                            : <Spinner animation="border" variant="primary"/>
                    }
                </Col>
            </Row>
        </Container>
    );
}

export default ManagerDashboardAssignableTickets;
