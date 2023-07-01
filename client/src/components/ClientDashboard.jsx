import React, {useEffect, useState} from "react";
import {useStatusAlert} from "../../hooks/useStatusAlert";
import API from "../API";
import {Button, Col, Row, Spinner, Tab, Tabs} from "react-bootstrap";
import {CustomModal} from "./Modals";
import {ModalType} from "../../enums/ModalType";
import Tickets from "./Tickets";
import {useNavigate} from "react-router-dom";
import PurchasedProducts from "./PurchasedProducts";


function ClientDashboard({userInfo}) {
    const [ticketsData, setTicketsData] = useState(null);
    const [productsData, setProductsData] = useState(null);
    const [loading, setLoading] = useState(true)
    const [load, setLoad] = useState(true)
    const [errorPresence, setErrorPresence] = useState(false)
    const {StatusAlertComponent, showError, resetStatusAlert} = useStatusAlert();
    const [confirmModalShow, setConfirmModalShow] = React.useState(false);
    const [confirmModalType, setConfirmModalType] = React.useState(null);

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
        showError(err.error)
    }

    const getTicketsData = async () => {
        console.log(userInfo.email)
        return new Promise((resolve, reject) => {
            API.getTicketsOfProfileByEmail(userInfo.email)
                .then((t) => {
                        setTicketsData(t)
                        resolve()
                    }
                ).catch(e => reject(e))
        })
    }

    const getProductsData = async () => {
        return new Promise((resolve, reject) => {
            /* TODO
                X ORA PRENDI TUTTI I PRODUCTS,
                POI PERò PRENDERAI SOLO I PRODOTTI ACQUISTATI
                CON LE DATE DI ACQUISTO E REGISTRAZIONE
                */
            API.getAllProducts()
                .then((p) => {
                        setProductsData(p)
                        resolve()
                    }
                ).catch(e => reject(e))
        })
    }

    return (
        <>
            <Row className='pb-5'>
                <Col className="d-flex align-items-center">
                    <h1>Client dashboard</h1>
                </Col>
            </Row>
            <StatusAlertComponent/>
            {
                loading ?
                    <Spinner animation="border" variant="primary"/>
                    :
                    !errorPresence ?
                        <>
                            <Row className='mb-4'>
                                <Col>
                                    <RegisterProduct
                                        update={() => {
                                            setLoad(true);
                                            setConfirmModalShow(true)
                                            setConfirmModalType(ModalType.CONFIRM_REGISTER)

                                        }}
                                    />
                                </Col>
                            </Row>
                            <Row className='mb-4'>
                                <Col>
                                    <ClientDashboardTabs ticketsData={ticketsData}
                                                         productsData={productsData}
                                                         update={() => {
                                                             setLoad(true);
                                                             setConfirmModalShow(true)
                                                             setConfirmModalType(ModalType.CONFIRM_CREATE)
                                                         }}
                                    />
                                </Col>
                            </Row>

                            <CustomModal
                                show={confirmModalShow}
                                hide={() => setConfirmModalShow(false)}
                                type={confirmModalType}
                            />
                        </>
                        :
                        null
            }
        </>
    )
}

function RegisterProduct({update}) {
    const [registerProductModalShow, setRegisterProductModalShow] = React.useState(false);

    return (
        <>
            <Button onClick={() => setRegisterProductModalShow(true)}>
                Register purchase
            </Button>
            <CustomModal
                show={registerProductModalShow}
                completingAction={update}
                hide={() => setRegisterProductModalShow(false)}
                type={ModalType.REGISTER_PRODUCT}
            />
        </>
    )
}

function ClientDashboardTabs({ticketsData, productsData, update}) {

    const formatTickets = () => {
        return ticketsData.map(ticket => {
            const product = productsData.find(p => p.productId === ticket.productId)
            return {...ticket, "product": product.name}
        })
    }

    const formatProducts = () => {
        return productsData.filter(product => {
            return ticketsData.find(ticket => ticket.productId === product.productId) || product.productId % 130 === 0
        }).map(product => {
            const ticket = ticketsData.find(ticket => ticket.productId === product.productId)
            return {
                ...product,
                "ticketId": ticket && ticket.status !== "CLOSED" ? ticket.ticketId : undefined,
                "purchaseDate": 1677065479943,
                "registrationDate": 1677065479943
            }

        })
    }
    return (
        <Tabs
            defaultActiveKey="products"
            id="uncontrolled-tab-example"
            className="mb-3"
        >
            <Tab eventKey="products" title="Purchased products">
                <ProductsTable products={formatProducts()} update={update}/>
            </Tab>
            <Tab eventKey="tickets" title="My tickets">
                <TicketsTable tickets={formatTickets()}/>
            </Tab>

        </Tabs>
    )
}

function TicketsTable({tickets}) {
    const navigate = useNavigate();

    const actionGoToTicket = (ticket) => {
        navigate(`/tickets/${ticket.ticketId}`)
    }
    return (
        <Tickets tickets={tickets} title={`You have ${tickets.length} ticket${tickets.length !== 1 ? "s" : ""}`}
                 actionName={"Details"}
                 action={actionGoToTicket}/>
    )
}

function ProductsTable({products, update}) {
    const navigate = useNavigate();
    const [targetProductId, setTargetProductId] = useState(null)
    const [showCustomModal, setShowCustomModal] = useState(false);

    const actionForTicket = (product) => {
        if (product.ticketId !== undefined)
            navigate(`/tickets/${product.ticketId}`)
        else {
            setTargetProductId(product.productId)
            setShowCustomModal(true)
        }
    }

    const actionNameFinder = (product) => {
        return (product.ticketId !== undefined) ? "Ticket details" : "Create new ticket"

    }


    return (
        <>
            <PurchasedProducts products={products}
                               title={`You have ${products.length} registered purchase${products.length !== 1 ? "s" : ""}`}
                               actionNameFinder={actionNameFinder}
                               action={actionForTicket}/>
            <CustomModal show={showCustomModal}
                         hide={() => setShowCustomModal(false)}
                         backdrop="static"
                         keyboard={false}
                         type={ModalType.CREATE_TICKET}
                         objectId={targetProductId}
                         completingAction={update}
            />
        </>
    )
}

export default ClientDashboard;
