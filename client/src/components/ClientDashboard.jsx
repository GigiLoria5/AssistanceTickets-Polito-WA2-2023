import React, {useEffect, useState} from "react";
import {useStatusAlert} from "../hooks/useStatusAlert";
import API from "../API";
import {Button, Col, Row, Spinner, Tab, Tabs} from "react-bootstrap";
import {CustomModal} from "./modals/CustomModal";
import {ModalType} from "../enums/ModalType";
import Tickets from "./Tickets";
import {useNavigate} from "react-router-dom";
import PurchasedProducts from "./PurchasedProducts";
import {handleApiError} from "../utils/utils";

function ClientDashboard({userInfo}) {
    const [ticketsData, setTicketsData] = useState(null);
    const [purchasesData, setPurchasesData] = useState(null);
    const [loading, setLoading] = useState(true)
    const [load, setLoad] = useState(true)
    const [errorPresence, setErrorPresence] = useState(false)
    const {StatusAlertComponent, showError, resetStatusAlert} = useStatusAlert();
    const [confirmModalShow, setConfirmModalShow] = React.useState(false);
    const [confirmModalType, setConfirmModalType] = React.useState(null);

    useEffect(() => {
            const getData = async () => {
                await Promise.all([getTicketsData(), getPurchasesData()])
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

    const getTicketsData = async () => {
        return new Promise((resolve, reject) => {
            API.getTicketsOfProfileByProfileId(userInfo.id)
                .then((t) => {
                        setTicketsData(t)
                        resolve()
                    }
                ).catch(e => reject(e))
        })
    }

    const getPurchasesData = async () => {
        return new Promise((resolve, reject) => {

            API.getPurchasesByProfileId(userInfo.id)
                .then((p) => {
                        setPurchasesData(p)
                        resolve()
                    }
                ).catch(e => reject(e))
        })
    }

    return (
        <>
            <Row className='pb-5'>
                <Col className="d-flex align-items-center">
                    <h1>Client Dashboard</h1>
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
                                                         purchasesData={purchasesData}
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

function ClientDashboardTabs({ticketsData, purchasesData, update}) {

    const formatTickets = () => {
        return ticketsData.map(ticket => {
            const product = purchasesData.find(p => p.product.productId === ticket.productId).product
            return {...ticket, "product": product.name}
        })
    }

    const formatPurchases = () => {
        return purchasesData.map(purchase => {
            const ticket = ticketsData.find(ticket => ticket.productTokenId === purchase.productTokenId)
            return {
                ...purchase,
                "ticketId": ticket && ticket.status !== "CLOSED" ? ticket.ticketId : undefined
            }

        })
    }
    return (
        <Tabs
            defaultActiveKey="purchases"
            id="uncontrolled-tab-example"
            className="mb-3"
        >
            <Tab eventKey="purchases" title="My purchases">
                <PurchasesTab purchases={formatPurchases()} update={update}/>
            </Tab>
            <Tab eventKey="tickets" title="My tickets">
                <TicketsTab tickets={formatTickets()}/>
            </Tab>

        </Tabs>
    )
}

function TicketsTab({tickets}) {
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

function PurchasesTab({purchases, update}) {
    const navigate = useNavigate();
    const [targetProductTokenId, setTargetProductTokenId] = useState(null)
    const [showCustomModal, setShowCustomModal] = useState(false);

    const actionForTicket = (purchase) => {
        if (purchase.ticketId !== undefined)
            navigate(`/tickets/${purchase.ticketId}`)
        else {
            setTargetProductTokenId(purchase.productTokenId)
            setShowCustomModal(true)
        }
    }

    const actionNameFinder = (purchase) => {
        return (purchase.ticketId !== undefined) ? "Ticket details" : "Create new ticket"
    }

    return (
        <>
            <PurchasedProducts purchases={purchases}
                               title={`You have ${purchases.length} registered purchase${purchases.length !== 1 ? "s" : ""}`}
                               actionNameFinder={actionNameFinder}
                               action={actionForTicket}/>
            <CustomModal show={showCustomModal}
                         hide={() => setShowCustomModal(false)}
                         backdrop="static"
                         keyboard={false}
                         type={ModalType.CREATE_TICKET}
                         objectId={targetProductTokenId}
                         completingAction={update}
            />
        </>
    )
}

export default ClientDashboard;
