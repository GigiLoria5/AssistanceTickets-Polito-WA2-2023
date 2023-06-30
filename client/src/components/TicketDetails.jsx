import {useNavigate, useParams} from "react-router-dom";
import React, {useEffect, useState} from "react";
import {useStatusAlert} from "../../hooks/useStatusAlert";
import API from "../API";
import {Button, Col, Row, Spinner, Table} from "react-bootstrap";
import * as PropTypes from "prop-types";
import {dateTimeMillisFormatted} from "../utils/utils";
import {availableTicketStatusChanges, getTaskToAchieveStatus} from "../utils/ticketUtil";
import {UserRole} from "../../enums/UserRole";
import {ConfirmModal, CustomModal} from "./Modals";
import {ModalType} from "../../enums/ModalType";

TicketDataTable.propTypes = {ticket: PropTypes.any};

function TicketDetails({userInfo}) {
    const {ticketId} = useParams();
    const [ticketData, setTicketData] = useState(null);
    const [productData, setProductData] = useState(null);
    const [ticketStatusChangesData, setTicketStatusChangesData] = useState(null)
    const [loading, setLoading] = useState(true)
    const [load, setLoad] = useState(true)
    const [errorPresence, setErrorPresence] = useState(false)
    const {StatusAlertComponent, showError, resetStatusAlert} = useStatusAlert();
    const [confirmModalShow, setConfirmModalShow] = React.useState(false);

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
        showError(err.error)
    }

    const getTicketData = async () => {
        return new Promise((resolve, reject) => {
            API.getTicketById(ticketId)
                .then((t) => {
                        setTicketData(t)
                        resolve(getProductData(t.productId))
                    }
                ).catch(e => reject(e))
        })
    }

    const getProductData = async (productId) => {
        return new Promise((resolve, reject) => {
            API.searchProduct(productId)
                .then((p) => {
                        setProductData(p)
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
            <div className='mb-5'>
                <h1>Ticket Details</h1>
            </div>
            <StatusAlertComponent/>
            {
                loading ?
                    <Spinner animation="border" variant="primary"/>
                    :
                    !errorPresence ?
                        <>
                            <TicketDetailComponents
                                ticketData={ticketData}
                                productData={productData}
                                ticketStatusChangesData={ticketStatusChangesData}
                                update={() => {setLoad(true); setConfirmModalShow(true)}}
                                userRole={userInfo.role}/>
                            <ConfirmModal
                                show={confirmModalShow}
                                onHide={() => setConfirmModalShow(false)}
                            />
                        </>
                        :
                        null
            }
        </>
    )
}


function TicketDetailComponents({ticketData, productData, ticketStatusChangesData, update, userRole}) {
    return (
        <>
            <Row className='mb-4'>
                <Col>
                    <TicketDataTable ticket={ticketData}/>
                </Col>
                <Col>
                    <ProductDataTable product={productData}/>
                </Col>
            </Row>
            {
                userRole === UserRole.CLIENT || userRole === UserRole.EXPERT ?
                    <Row className='mb-5'>
                        <Col md={4}>
                            <TicketChat ticketId={ticketData.ticketId}/>
                        </Col>
                    </Row> : null
            }
            <Row className='mb-5'>
                <Col md={4}>
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

function ProductDataTable({product}) {
    return (
        <>
            <h2>Product</h2>
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
                    <td> {product.price}</td>
                </tr>
                <tr>
                    <th> Weight</th>
                    <td> {product.weight}</td>
                </tr>
                </tbody>
            </Table>
        </>
    )
}

function TicketChat({ticketId}) {
    const navigate = useNavigate()
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
                                <Col key={newDesiredState}>
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
                                     ticketId={ticketId}
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
                                        <th>oldStatus</th>
                                        <th>newStatus</th>
                                        <th>changedBy</th>
                                        <th>currentExpertId</th>
                                        <th>time</th>
                                        <th>description</th>
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
                    </> : <div>No available status changes</div>
            }
        </>
    );
}

export default TicketDetails;
