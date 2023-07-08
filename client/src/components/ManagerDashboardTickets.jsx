import { useEffect, useState } from "react";
import { useStatusAlert } from "../../hooks/useStatusAlert";
import API from "../API";
import { Button, Col, Container, Form, Modal, Row, Spinner, Table, Offcanvas } from "react-bootstrap";
import { handleApiError } from "../utils/utils";
import Tickets from "./Tickets";
import { useNavigate } from "react-router-dom";

function ManagerDashboardTickets() {
    const [tickets, setTickets] = useState(null);
    const [products, setProducts] = useState(null);
    const [clientInfo, setClientInfo] = useState(null)
    const [expertInfo, setExpertInfo] = useState(null)
    const [showInfoToManager, setShowInfoToManager] = useState(true);
    const [refreshTickets, setRefreshTickets] = useState(true)
    const [loading, setLoading] = useState(true)
    const [load, setLoad] = useState(true)
    const [errorPresence, setErrorPresence] = useState(false)
    const { StatusAlertComponent, showError, resetStatusAlert } = useStatusAlert();

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

    const getTicketsData = () => {
        API.getAllTickets()
            .then((t) => {
                setTickets(t)
                resetStatusAlert()
            }
            ).catch(err => handleApiError(err, showError))
    }

    const getProductsData = () => {
        API.getAllProducts()
            .then((p) => {
                setProducts(p)
                resetStatusAlert()
            }
            ).catch(err => handleApiError(err, showError))
    }

    const getClientInfo = (clientId) => {
        API.getProfileById(clientId)
            .then((client) => {
                setClientInfo(client)
                resetStatusAlert
            })
            .catch(err => handleApiError(err, showError))
    }

    const getExpertInfo = (expertId) => {
        API.getExpertById(expertId)
            .then((expert) => {
                setExpertInfo(expert)
                resetStatusAlert
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
            return { ...ticket, "product": product.name }
        })
    }

    const [showClient, setShowClient] = useState(false)
    const [showExpert, setShowExpert] = useState(false)

    const handleCloseClient = () => setShowClient(false)
    const handleCloseExpert = () => setShowExpert(false)

    const handleShowClientInfo =  (clientId) => {
        getClientInfo(clientId);
        setShowClient(true)
    }

    const handleShowExpertInfo =  (expertId) => {
        getExpertInfo(expertId);
        setShowExpert(true)
    }

    return (
        <Container className="h-100">
            <Row className="h-100">
                <Col className="d-flex flex-column align-items-center justify-content-center">
                    <h2>Tickets Management</h2><StatusAlertComponent />
                    {
                        tickets || !refreshTickets ?
                            <>
                                <Tickets tickets={formatTickets()}
                                    actionName={"Details"}
                                    action={actionGoToTicket}
                                    showInfoToManager={showInfoToManager}
                                    showClientInfo={handleShowClientInfo}
                                    showExpertInfo={handleShowExpertInfo}
                                    />
                                    {clientInfo ? 
                                    <ClientInfo show={showClient} handleClose={handleCloseClient} info={clientInfo} title={"Customer Info"}/> : null}
                                    {expertInfo ? 
                                    <ExpertInfo show={showExpert} handleClose={handleCloseExpert} info={expertInfo} title={"Expert Info"}/> : null}
                                    </>
                            : <Spinner animation="border" variant="primary" />
                    }
                </Col>
            </Row>
        </Container>
    );
}

function ClientInfo({show, handleClose, info, title}) {
    return <Offcanvas show={show} onHide={handleClose}>
            <Offcanvas.Header closeButton>
            <Offcanvas.Title >{title}</Offcanvas.Title>
            </Offcanvas.Header>
            <Offcanvas.Body>
                {<h4>Name:  {info.name}</h4> }
                {<h4>Surname:  {info.surname}</h4> }
                {<h4>Phone:  {info.phoneNumber}</h4> }
                {<h4>E-mail:  {info.email}</h4> }
            </Offcanvas.Body>
        </Offcanvas>
}

function ExpertInfo({show, handleClose, info, title}) {
    return <Offcanvas show={show} onHide={handleClose}>
            <Offcanvas.Header closeButton>
            <Offcanvas.Title >{title}</Offcanvas.Title>
            </Offcanvas.Header>
            <Offcanvas.Body>
                {<h4>Name:  {info.name}</h4> }
                {<h4>Surname:  {info.surname}</h4> }
                {<h4>E-mail:  {info.email}</h4> }
            </Offcanvas.Body>
        </Offcanvas>
}

export default ManagerDashboardTickets;
