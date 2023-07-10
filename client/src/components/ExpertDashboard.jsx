import {useNavigate} from "react-router-dom";
import React, {useEffect, useState} from "react";
import API from "../API";
import { Button, Col, Row, Spinner, Tab, Tabs, Offcanvas } from "react-bootstrap";
import { useStatusAlert } from "../../hooks/useStatusAlert";
import { handleApiError, handleErrorResponse } from "../utils/utils";
import Tickets from "./Tickets";
import ClientInfoCanvas from "./ClientInfoCanvas";


function ExpertDashboard({ userInfo }) {
    const { StatusAlertComponent, showError, resetStatusAlert } = useStatusAlert();
    const [ticketsData, setTicketsData] = useState(null);
    const [productsData, setProductsData] = useState(null);
    const [loading, setLoading] = useState(true)
    const [load, setLoad] = useState(true)
    const [errorPresence, setErrorPresence] = useState(false)
    const [clientInfo, setClientInfo] = useState(null)
    

    useEffect(() => {
            const getData = async () => {
                await Promise.all([getTicketsData(), getProductsData()])
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

    const getTicketsData = () => {
        return new Promise((resolve, reject) => {
            API.getTicketsOfExpertsByExpertId(userInfo.id)
                .then((t) => {
                    setTicketsData(t)
                    resolve()
                })
                .catch(e => reject(e))
        })
    }

    const getProductsData = async () => {
        return new Promise((resolve, reject) => {
            API.getAllProducts()
                .then((p) => {
                    setProductsData(p)
                    resolve()
                }
                )
                .catch(e => reject(e))
        })
    }
    const getClientInfo = (clientId) => {
        API.getProfileById(clientId)
            .then((client) => {
                setClientInfo(client)
                resetStatusAlert
            })
            .catch(err => handleApiError(err, showError))
    }

    const formatTickets = () => {
        if (ticketsData && ticketsData.length > 0) {
            return ticketsData.map(ticket => {
                const product = productsData.find(p => p.productId === ticket.productId)
                return { ...ticket, "product": product.name }
            })
        } else {
            return [];
        }

    }
    return (
        <>
            <Row className='pb-5'>
                <Col className="d-flex align-items-center">
                    <h1>Expert Dashboard</h1>
                </Col>
            </Row>
            <StatusAlertComponent/>
            {
                loading ?
                    <Spinner animation="border" variant="primary"/>
                    :
                    !errorPresence ?
                        <Row>
                            <Col>
                                <TicketsTable tickets={formatTickets()} getClientInfo={getClientInfo}
                                              clientInfo={clientInfo}/>
                            </Col>
                        </Row>
                        :
                        null
            }
        </>
    );
}

function TicketsTable({tickets, getClientInfo, clientInfo}) {
    const navigate = useNavigate();
    const [show, setShow] = useState(false)

    const handleClose = () => setShow(false)

    const actionGoToTicket = (ticket) => {
        navigate(`/tickets/${ticket.ticketId}`)
    }

    const handleShowClientInfo = (clientId) => {
        getClientInfo(clientId);
        setShow(true)

    }
    return (
        <>
            <Tickets tickets={tickets} title={`You have ${tickets.length} ticket${tickets.length !== 1 ? "s" : ""}`}
                     actionName={"Details"}
                     action={actionGoToTicket}
                     showClientInfo={handleShowClientInfo}
            />
            {clientInfo ?
                <ClientInfoCanvas show={show} onHide={handleClose} clientInfo={clientInfo}/>
                :
                null
            }


        </>
    )
}






export default ExpertDashboard;
