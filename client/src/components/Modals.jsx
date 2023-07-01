import {useStatusAlert} from "../../hooks/useStatusAlert";
import {Button, Col, Form, Modal, Row} from "react-bootstrap";
import {getTaskToAchieveStatus} from "../utils/ticketUtil";
import React, {createContext, useContext, useMemo, useState} from "react";
import {ModalType} from "../../enums/ModalType";
import {TicketStatus} from "../../enums/TicketStatus";
import {TicketPriority} from "../../enums/TicketPriority";
import Experts from "./Experts";
import API from "../API";

const ModalContext = createContext(null);

function CustomModal({
                         show,
                         hide,
                         backdrop = true,
                         keyboard = true,
                         type,
                         desiredState = null,
                         ticketId = null,
                         completingAction = null
                     }) {

    const {StatusAlertComponent, showError, resetStatusAlert} = useStatusAlert();

    const handleClose = () => {
        hide();
        resetStatusAlert()
    }

    const ModalProviderValue
        = useMemo(() =>
            ({type, desiredState, StatusAlertComponent, ticketId, handleClose, completingAction, showError}),
        [type, desiredState, StatusAlertComponent, ticketId, handleClose, completingAction, showError]);


    const setSize = () => {
        switch (type) {
            case ModalType.CREATE:
                return "xl"
            case ModalType.STATUS_CHANGE:
                return "xl"
            case ModalType.CONFIRM_STATUS_CHANGE:
                return "lg"
            case ModalType.CONFIRM_CREATE:
                return "lg"
            case ModalType.REGISTER_PRODUCT:
                return "sm"
            default:
                return "xl"
        }
    }
    const modalSize = setSize()

    return (
        <Modal
            show={show}
            onHide={handleClose}
            backdrop={backdrop}
            size={modalSize}
            keyboard={keyboard}
            centered
        >
            <ModalContext.Provider value={ModalProviderValue}>
                <CustomModalHeader/>
                <CustomModalBody/>
            </ModalContext.Provider>
        </Modal>
    )
}

function CustomModalHeader() {
    const {type, desiredState, StatusAlertComponent} = useContext(ModalContext)

    const getTitle = (type) => {
        switch (type) {
            case ModalType.STATUS_CHANGE:
                return `${getTaskToAchieveStatus(desiredState)} tickets`
            case ModalType.CREATE:
                return "Create ticket"
            case ModalType.CONFIRM_STATUS_CHANGE:
                return "Status change completed"
            case ModalType.CONFIRM_CREATE:
                return "Ticket creation completed"
            case ModalType.REGISTER_PRODUCT:
                return "Register purchase"
        }
    }
    return (
        <>
            <Modal.Header closeButton>
                <Modal.Title>
                    {
                        getTitle(type)
                    }
                </Modal.Title>
            </Modal.Header>
            <Row className="mt-2 mx-3">
                <StatusAlertComponent/>
            </Row>
        </>
    )
}

function CustomModalBody() {

    const {type, desiredState} = useContext(ModalContext)
    switch (type) {
        case ModalType.STATUS_CHANGE:
            if (desiredState === TicketStatus.IN_PROGRESS)
                return <StatusChangeInProgressModalBody/>
            else
                return <StatusChangeStandardModalBody/>

        case ModalType.CREATE:
            return <TicketCreationModalBody/>
        case ModalType.CONFIRM_STATUS_CHANGE:
            return <OperationCompletedModalBody description="Status change successfully concluded"/>
        case ModalType.CONFIRM_CREATE:
            return <OperationCompletedModalBody description="Ticket creation successfully concluded"/>
        case ModalType.REGISTER_PRODUCT:
            return <RegisterProductModalBody/>
    }

}

function StatusChangeInProgressModalBody() {

    const [selectedExpert, setSelectedExpert] = useState(null);
    const [ticketPriority, setTicketPriority] = useState(null);
    const [description, setDescription] = useState('');


    return (
        <Modal.Body>
            {selectedExpert === null ?
                <Experts title={"Select expert"} actionName={"Select"} action={setSelectedExpert}/>
                :
                <ChangeStatusToInProgressForm
                    selectedExpert={selectedExpert} cancelSelectedExpert={() => setSelectedExpert(null)}
                    ticketPriority={ticketPriority} setTicketPriority={setTicketPriority}
                    description={description} setDescription={setDescription}
                />

            }
        </Modal.Body>


    )
}

function ChangeStatusToInProgressForm({
                                          selectedExpert, cancelSelectedExpert,
                                          ticketPriority, setTicketPriority,
                                          description, setDescription,
                                      }) {

    const {ticketId, handleClose, completingAction, showError} = useContext(ModalContext)

    const [validated, setValidated] = useState(false);


    const handleSubmit = (event) => {
        const form = event.currentTarget;
        event.preventDefault();
        if (form.checkValidity() === false) {
            event.stopPropagation();
        } else {
            const apiCall = getUpdateApiForDesiredStatus(TicketStatus.IN_PROGRESS, completingAction, showError)
            apiCall(ticketId, selectedExpert.expertId, ticketPriority, description)
        }
        setValidated(true);

    };

    return (
        <>
            <Row className="mb-3" md={5}>
                <Col>
                    <Row>
                        <Col>
                            Selected expert:
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            {selectedExpert.name} {selectedExpert.surname}
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            {selectedExpert.email}
                        </Col>
                    </Row>

                </Col>
                <Col>
                    <Button onClick={cancelSelectedExpert}>
                        Change
                    </Button>
                </Col>
            </Row>
            <Form noValidate validated={validated} onSubmit={handleSubmit}>
                <Row className="mb-3">
                    <Form.Group controlId="validationCustom1">
                        <Form.Label>Ticket priority level</Form.Label>
                        <Row>
                            <Col>
                                <Form.Check.Input
                                    inline="true"
                                    required
                                    defaultChecked={ticketPriority === TicketPriority.LOW}
                                    name="group1"
                                    type="radio"
                                    id={`inline-radio-1`}
                                    onClick={() => setTicketPriority(TicketPriority.LOW)}
                                />
                                <Form.Check.Label className="mx-2">
                                    {TicketPriority.LOW}
                                </Form.Check.Label>
                                <Form.Check.Input
                                    inline="true"
                                    required
                                    defaultChecked={ticketPriority === TicketPriority.MEDIUM}

                                    name="group1"
                                    type="radio"
                                    id={`inline-radio-2`}
                                    onClick={() => setTicketPriority(TicketPriority.MEDIUM)}
                                />
                                <Form.Check.Label className="mx-2">
                                    {TicketPriority.MEDIUM}
                                </Form.Check.Label>
                                <Form.Check.Input
                                    inline="true"
                                    required
                                    defaultChecked={ticketPriority === TicketPriority.HIGH}

                                    name="group1"
                                    type="radio"
                                    id={`inline-radio-3`}
                                    onClick={() => setTicketPriority(TicketPriority.HIGH)}
                                />
                                <Form.Check.Label className="mx-2">
                                    {TicketPriority.HIGH}
                                </Form.Check.Label>
                                <Form.Check.Input
                                    inline="true"
                                    required
                                    defaultChecked={ticketPriority === TicketPriority.CRITICAL}

                                    name="group1"
                                    type="radio"
                                    id={`inline-radio-4`}
                                    onClick={() => setTicketPriority(TicketPriority.CRITICAL)}
                                />
                                <Form.Check.Label className="mx-2">
                                    {TicketPriority.CRITICAL}
                                </Form.Check.Label>
                                <Form.Control.Feedback type="invalid">
                                    Please select a priority level
                                </Form.Control.Feedback>
                            </Col>
                        </Row>
                    </Form.Group>
                </Row>

                <Row className="mb-3">
                    <Form.Group controlId="validationCustom2">
                        <Form.Label>Optional description</Form.Label>
                        <Form.Control
                            required={false}
                            type="text"
                            as="textarea" rows={3}
                            value={description}
                            onChange={ev => setDescription(ev.target.value)}
                            placeholder="Insert description"
                        />
                    </Form.Group>
                </Row>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => handleClose()}>
                        Cancel
                    </Button>
                    <Button variant="primary" type="submit">
                        Confirm status change
                    </Button>
                </Modal.Footer>
            </Form>
        </>
    )
}

function StatusChangeStandardModalBody() {

    const {ticketId, desiredState, handleClose, completingAction, showError} = useContext(ModalContext)

    const requiredDesc = desiredState === TicketStatus.REOPENED
    const [validated, setValidated] = useState(false);
    const [description, setDescription] = useState('');

    const handleSubmit = (event) => {
        const form = event.currentTarget;
        event.preventDefault();
        if (form.checkValidity() === false) {
            event.stopPropagation();
        } else {
            const apiCall = getUpdateApiForDesiredStatus(desiredState, completingAction, showError)
            apiCall(ticketId, description)
        }
        setValidated(true);

    };

    return (
        <>
            <Modal.Body>
                <Form noValidate validated={validated} onSubmit={handleSubmit}>
                    <Row className="mb-3">
                        <Form.Group controlId="validationCustom">
                            <Form.Label>{requiredDesc ? "Description" : "Optional description"}</Form.Label>
                            <Form.Control
                                required={requiredDesc}
                                type="text"
                                as="textarea" rows={3}
                                value={description}
                                onChange={ev => setDescription(ev.target.value)}
                                placeholder="Insert description"
                            />
                            <Form.Control.Feedback type="invalid">
                                Please provide a description
                            </Form.Control.Feedback> </Form.Group>
                    </Row>
                    <Modal.Footer>
                        <Button variant="secondary" onClick={() => handleClose()}>
                            Cancel
                        </Button>
                        <Button variant="primary" type="submit">
                            Confirm status change
                        </Button>
                    </Modal.Footer>
                </Form>
            </Modal.Body>
        </>
    )
}


function getUpdateApiForDesiredStatus(desiredStatus, desiredPostUpdateAction, showError) {
    const startTicket = (ticketId, expertId, priorityLevel, description) => {
        API.startTicket(ticketId, expertId, priorityLevel, description)
            .then(() => {
                    desiredPostUpdateAction()
                }
            )
            .catch(err => showError(err.error))
    }

    const stopTicket = (ticketId, description) => {
        API.stopTicket(ticketId, description)
            .then(() => {
                    desiredPostUpdateAction()
                }
            )
            .catch(err => showError(err.error))
    }

    const resolveTicket = (ticketId, description) => {
        API.resolveTicket(ticketId, description)
            .then(() => {
                    desiredPostUpdateAction()
                }
            )
            .catch(err => showError(err.error))
    }

    const reopenTicket = (ticketId, description) => {
        API.reopenTicket(ticketId, description)
            .then(() => {
                    desiredPostUpdateAction()
                }
            )
            .catch(err => showError(err.error))
    }

    const closeTicket = (ticketId, description) => {
        API.closeTicket(ticketId, description)
            .then(() => {
                    desiredPostUpdateAction()
                }
            )
            .catch(err => showError(err.error))
    }

    switch (desiredStatus) {
        case TicketStatus.OPEN:
            return (ticketId, description) => stopTicket(ticketId, description)
        case TicketStatus.CLOSED:
            return (ticketId, description) => closeTicket(ticketId, description)
        case TicketStatus.REOPENED:
            return (ticketId, description) => reopenTicket(ticketId, description)
        case TicketStatus.RESOLVED:
            return (ticketId, description) => resolveTicket(ticketId, description)
        case TicketStatus.IN_PROGRESS:
            return (ticketId, expertId, priorityLevel, description) => startTicket(ticketId, expertId, priorityLevel, description)
    }
}

function TicketCreationModalBody() {
//    const {ticketId, handleClose, showError} = useContext(ModalContext)
    return (
        <>
        </>

    )
}

function OperationCompletedModalBody({description}) {
    const {handleClose} = useContext(ModalContext)
    return (<>
            <Modal.Body>{description}</Modal.Body>
            <Modal.Footer>
                <Button variant="primary" onClick={handleClose}>
                    Close
                </Button>
            </Modal.Footer>
        </>
    )
}

function RegisterProductModalBody() {

    const {handleClose /*, completingAction, showError */} = useContext(ModalContext)

    const [validated, setValidated] = useState(false);
    const [uuid, setUuid] = useState('');

    const handleSubmit = (event) => {
        const form = event.currentTarget;
        event.preventDefault();
        if (form.checkValidity() === false) {
            event.stopPropagation();
        } else {
            //TODO IN CASO DI SUCCESSO. GIà TI PASSO IL COMPLETING ACTION DA FARE
            //  const apiCall = getUpdateApiForDesiredStatus(desiredState, completingAction, showError)
            // apiCall(ticketId, description)
        }
        setValidated(true);

    };

    return (
        <>
            <Modal.Body>
                <Form noValidate validated={validated} onSubmit={handleSubmit}>
                    <Row className="mb-3">
                        <Form.Group controlId="validationCustom">
                            <Form.Label>Uuid</Form.Label>
                            <Form.Control
                                required={true}
                                type="text"
                                value={uuid}
                                onChange={ev => setUuid(ev.target.value)}
                                placeholder="Insert uuid"
                            />
                            <Form.Control.Feedback type="invalid">
                                Please provide a uuid
                            </Form.Control.Feedback> </Form.Group>
                    </Row>
                    <Modal.Footer>
                        <Button variant="secondary" onClick={() => handleClose()}>
                            Cancel
                        </Button>
                        <Button variant="primary" type="submit">
                            Confirm purchase
                        </Button>
                    </Modal.Footer>
                </Form>
            </Modal.Body>
        </>
    )

}

export {CustomModal};
