import {useNavigate, useParams} from "react-router-dom";
import React, {useEffect, useState} from "react";
import {useStatusAlert} from "../hooks/useStatusAlert";
import API from "../API";
import {Button, Col, Row, Spinner, Table} from "react-bootstrap";
import * as PropTypes from "prop-types";
import {dateTimeMillisFormatted, handleApiError} from "../utils/utils";
import {availableTicketStatusChanges, getTaskToAchieveStatus} from "../utils/ticketUtil";
import {UserRole} from "../enums/UserRole";
import {CustomModal} from "./modals/CustomModal";
import {ModalType} from "../enums/ModalType";

TicketDataTable.propTypes = {ticket: PropTypes.any};

function TicketDetails({userInfo}) {
    const {ticketId} = useParams();
    const [ticketData, setTicketData] = useState(null);
    const [purchaseData, setPurchaseData] = useState(null);
    const [ticketStatusChangesData, setTicketStatusChangesData] = useState(null)
    const [loading, setLoading] = useState(true)
    const [load, setLoad] = useState(true)
    const [errorPresence, setErrorPresence] = useState(false)
    const {StatusAlertComponent, showError, resetStatusAlert} = useStatusAlert();
    const [confirmModalShow, setConfirmModalShow] = React.useState(false);

    const navigate = useNavigate()

    useEffect(() => {
            const getData = async () => {
                await Promise.all([getTicketData(), getTicketStatusChangesData()])
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

    const getTicketData = async () => {
        return new Promise((resolve, reject) => {
            API.getTicketById(ticketId)
                .then((t) => {
                        setTicketData(t)
                        resolve(getPurchaseData(t.customerId, t.productTokenId))
                    }
                ).catch(e => reject(e))
        })
    }

    const getPurchaseData = async (customerId, productTokenId) => {
        return new Promise((resolve, reject) => {
            API.getPurchaseByProfileIdAndProductTokenId(customerId, productTokenId)
                .then((p) => {
                        setPurchaseData(p)
                        resolve()
                    }
                ).catch(e => reject(e))
        })
    }

    const getTicketStatusChangesData = async () => {
        return new Promise((resolve, reject) => {
            API.getTicketStatusChangesByTicketId(ticketId)
                .then((tsc) => {
                        setTicketStatusChangesData(tsc)
                        resolve()
                    }
                ).catch(e => reject(e))
        })

    }

    return (
        <>
            <Row className='pb-5'>
                <Col md="auto" className="d-flex align-items-center">
                    <Button onClick={() => navigate(-1)}> {"Go back"}</Button>
                </Col>
                <Col md="auto" className="d-flex align-items-center">
                    <h1>Ticket Details</h1>
                </Col>
            </Row>

            <StatusAlertComponent/>
            {
                loading ?
                    <Spinner animation="border" variant="primary"/>
                    :
                    !errorPresence ?
                        <>
                            <TicketDetailComponents
                                ticketData={ticketData}
                                purchaseData={purchaseData}
                                ticketStatusChangesData={ticketStatusChangesData}
                                update={() => {
                                    setLoad(true);
                                    setConfirmModalShow(true)
                                }}
                                userRole={userInfo.role}/>
                            <CustomModal
                                show={confirmModalShow}
                                hide={() => setConfirmModalShow(false)}
                                type={ModalType.CONFIRM_STATUS_CHANGE}
                            />
                        </>
                        :
                        null
            }
        </>
    )
}


function TicketDetailComponents({ticketData, purchaseData, ticketStatusChangesData, update, userRole}) {
    return (
        <>
            <Row className='mb-4'>
                <Col>
                    <TicketDataTable ticket={ticketData}/>
                </Col>
                <Col>
                    <PurchaseDataTable purchase={purchaseData}/>
                </Col>
            </Row>
            {
                userRole === UserRole.CLIENT || userRole === UserRole.EXPERT ?
                    <Row className='mb-5'>
                        <Col>
                            <TicketChat ticketId={ticketData.ticketId}/>
                        </Col>
                    </Row> : null
            }
            <Row className='mb-5'>
                <Col>
                    <TicketStatusAvailableChanges
                        ticketId={ticketData.ticketId}
                        availableStatuses={availableTicketStatusChanges(userRole, ticketData.status)}
                        update={update}
                    />
                </Col>
            </Row>
            <Row className='mb-5'>
                <Col>
                    <TicketStatusChangesTable ticketStatusChanges={ticketStatusChangesData}/>
                </Col>
            </Row>
        </>
    )
}

function TicketDataTable({ticket}) {
    return (
        <>
            <h2>Ticket</h2>
            <Table>
                <tbody>
                <tr>
                    <th> Title</th>
                    <td> {ticket.title}</td>
                </tr>
                <tr>
                    <th> Description</th>
                    <td> {ticket.description}</td>
                </tr>
                <tr>
                    <th> Total Exchanged Messages</th>
                    <td> {ticket.totalExchangedMessages}</td>
                </tr>
                <tr>
                    <th> Status</th>
                    <td> {ticket.status}</td>
                </tr>
                <tr>
                    <th> Priority Level</th>
                    <td> {ticket.priorityLevel}</td>
                </tr>
                <tr>
                    <th> Created At</th>
                    <td> {dateTimeMillisFormatted(ticket.createdAt)}</td>
                </tr>
                <tr>
                    <th> Last Modified At</th>
                    <td> {dateTimeMillisFormatted(ticket.lastModifiedAt)}</td>
                </tr>
                </tbody>
            </Table>
        </>
    )
}

function PurchaseDataTable({purchase}) {
    const product = purchase.product
    return (
        <>
            <h2>Purchase</h2>
            <Table>
                <tbody>
                <tr>
                    <th> Asin</th>
                    <td> {product.asin}</td>
                </tr>
                <tr>
                    <th> Brand</th>
                    <td> {product.brand}</td>
                </tr>
                <tr>
                    <th> Name</th>
                    <td> {product.name}</td>
                </tr>
                <tr>
                    <th> Category</th>
                    <td> {product.category}</td>
                </tr>
                <tr>
                    <th> Manufacturer Number</th>
                    <td> {product.manufacturerNumber}</td>
                </tr>
                <tr>
                    <th> Price</th>
                    <td> {product.price} €</td>
                </tr>
                <tr>
                    <th> Weight</th>
                    <td> {product.weight} kg</td>
                </tr>
                <tr>
                    <th> Purchase date</th>
                    <td> {dateTimeMillisFormatted(purchase.createdAt)}</td>
                </tr>
                <tr>
                    <th> Registration date</th>
                    <td> {dateTimeMillisFormatted(purchase.registeredAt)}</td>
                </tr>
                </tbody>
            </Table>
        </>
    )
}

function TicketChat({ticketId}) {
    //const navigate = useNavigate()
    return (
        <>
            <h2>Ticket chat</h2>
            <Button onClick={null/*navigate('??')*/}>
                Open chat
            </Button>
        </>
    )
}

function TicketStatusAvailableChanges({ticketId, availableStatuses, update}) {

    const [desiredState, setDesiredState] = useState(null)
    const [showCustomModal, setShowCustomModal] = useState(false);

    return (
        <>
            <h2>Change ticket status</h2>
            {
                availableStatuses.length > 0 ?
                    <>
                        <Row>
                            {availableStatuses.map((newDesiredState) => (
                                <Col md="auto" key={newDesiredState}>
                                    <Button onClick={() => {
                                        setShowCustomModal(true)
                                        setDesiredState(newDesiredState)
                                    }
                                    }>
                                        {getTaskToAchieveStatus(newDesiredState)} ticket
                                    </Button>
                                </Col>
                            ))}
                        </Row>
                        <CustomModal show={showCustomModal}
                                     hide={() => setShowCustomModal(false)}
                                     backdrop="static"
                                     keyboard={false}
                                     type={ModalType.STATUS_CHANGE}
                                     desiredState={desiredState}
                                     objectId={ticketId}
                                     completingAction={update}
                        />
                    </> : <div>No available changes</div>
            }
        </>
    )
}

function TicketStatusChangesTable({ticketStatusChanges}) {
    return (
        <>
            <h2>Ticket Status Changes</h2>
            {
                ticketStatusChanges.length > 0 ?
                    <>
                        <Row className="mt-3">
                            <Col>
                                <Table>
                                    <thead>
                                    <tr>
                                        <th>Old Status</th>
                                        <th>New Status</th>
                                        <th>Changed By</th>
                                        <th>Current Expert Id</th>
                                        <th>Time</th>
                                        <th>Description</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    {ticketStatusChanges.map((tsc) => (
                                        <tr key={tsc.time}>
                                            <td>{tsc.oldStatus}</td>
                                            <td>{tsc.newStatus}</td>
                                            <td>{tsc.changedBy}</td>
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

export default TicketDetails;
