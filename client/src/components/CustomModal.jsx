import {useStatusAlert} from "../../hooks/useStatusAlert";
import {Button, Col, Form, Modal, Row} from "react-bootstrap";
import {getTaskToAchieveStatus} from "../utils/ticketUtil";
import React, { useState} from "react";
import {ModalType} from "../../enums/ModalType";
import {TicketStatus} from "../../enums/TicketStatus";
import {TicketPriority} from "../../enums/TicketPriority";
import Experts from "./Experts";
import API from "../API";

function CustomModal({show, hide, backdrop, keyboard, type, desiredState, ticketId,completingAction}) {

    const {StatusAlertComponent, showError, resetStatusAlert} = useStatusAlert();

    const handleClose = () => {
        hide();
        resetStatusAlert()
    }


    return (
        <Modal
            show={show}
            onHide={handleClose}
            backdrop={backdrop}
            size="xl"
            keyboard={keyboard}
            centered
        >
            <CustomModalHeader type={type} desiredState={desiredState} StatusAlertComponent={StatusAlertComponent}/>
            <CustomModalInterior type={type} desiredState={desiredState} ticketId={ticketId} handleClose={handleClose}
                                 completingAction={completingAction} resetStatusAlert={resetStatusAlert}
                                 showError={showError}/>
        </Modal>
    )
}

function CustomModalHeader({type, desiredState, StatusAlertComponent}) {
    const getTitle = (type) => {
        switch (type) {
            case ModalType.STATUS_CHANGE:
                return `${getTaskToAchieveStatus(desiredState)} ticket`
            case ModalType.CREATE:
                return "Create ticket"
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

function CustomModalInterior({type, desiredState, ticketId, handleClose, completingAction,resetStatusAlert, showError}) {
    const getModalInterior = () => {
        switch (type) {
            case ModalType.STATUS_CHANGE:
                if (desiredState === TicketStatus.IN_PROGRESS)
                    return <StatusChangeInProgressModal ticketId={ticketId} handleClose={handleClose} resetStatusAlert={resetStatusAlert}
                                                  completingAction={completingAction} showError={showError}/>
                else
                    return <StatusChangeStandardModal ticketId={ticketId} desiredState={desiredState} handleClose={handleClose}
                                                      completingAction={completingAction} showError={showError}/>

            case ModalType.CREATE:
                return <TicketCreationModal ticketId={ticketId} handleClose={handleClose} showError={showError}/>
        }
    }

    return getModalInterior()
}

function StatusChangeInProgressModal({ticketId,handleClose, resetStatusAlert, completingAction,showError}) {

    const [selectedExpert, setSelectedExpert] = useState(null);
    const [ticketPriority, setTicketPriority] = useState(null);
    const [description, setDescription] = useState('');


    return (
        <Modal.Body>
            {selectedExpert === null ?
                <Experts title={"Select expert"} actionName={"Select"} action={setSelectedExpert}/>
                :
                <ChangeStatusToInProgressForm
                    ticketId={ticketId} selectedExpert={selectedExpert} cancelSelectedExpert={() => setSelectedExpert(null)}
                    ticketPriority={ticketPriority} setTicketPriority={setTicketPriority}
                    description={description} setDescription={setDescription}
                    completingAction={completingAction} handleClose={handleClose} showError={showError}/>

            }
        </Modal.Body>


    )
}

function ChangeStatusToInProgressForm({
                                    ticketId,selectedExpert, cancelSelectedExpert,
                                    ticketPriority, setTicketPriority,
                                    description, setDescription,
                                    completingAction,handleClose,showError
                                }) {

    const [validated, setValidated] = useState(false);


    const handleSubmit = (event) => {
        const form = event.currentTarget;
        event.preventDefault();
        if (form.checkValidity() === false) {
            event.stopPropagation();
        } else {
            const apiCall=getUpdateApiForDesiredStatus(TicketStatus.IN_PROGRESS,completingAction,showError)
            apiCall(ticketId,selectedExpert.expertId,ticketPriority,description)
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

function StatusChangeStandardModal({ticketId,desiredState, handleClose, completingAction, showError}) {

    const requiredDesc = desiredState === TicketStatus.REOPENED

    const [validated, setValidated] = useState(false);
    const [description, setDescription] = useState('');

    const handleSubmit = (event) => {
        const form = event.currentTarget;
        event.preventDefault();
        if (form.checkValidity() === false) {
            event.stopPropagation();
        } else {
            const apiCall=getUpdateApiForDesiredStatus(desiredState,completingAction,showError)
            apiCall(ticketId,description)
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

function TicketCreationModal({ticketId, handleClose}) {

    return (
        <>
        </>

    )
}

function getUpdateApiForDesiredStatus(desiredStatus,desiredPostUpdateAction,showError){
    const startTicket = (ticketId,expertId,priorityLevel,description) => {
        API.startTicket(ticketId,expertId,priorityLevel,description)
            .then(() => {
                desiredPostUpdateAction()
                }
            )
            .catch(err => showError(err.error))
    }

    const stopTicket = (ticketId,description) => {
        API.stopTicket(ticketId,description)
            .then(() => {
                    desiredPostUpdateAction()
                }
            )
            .catch(err => showError(err.error))
    }

    const resolveTicket = (ticketId,description) => {
        API.resolveTicket(ticketId,description)
            .then(() => {
                    desiredPostUpdateAction()
                }
            )
            .catch(err => showError(err.error))
    }

    const reopenTicket = (ticketId,description) => {
        API.reopenTicket(ticketId,description)
            .then(() => {
                    desiredPostUpdateAction()
                }
            )
            .catch(err => showError(err.error))
    }

    const closeTicket = (ticketId,description) => {
        API.closeTicket(ticketId,description)
            .then(() => {
                    desiredPostUpdateAction()
                }
            )
            .catch(err => showError(err.error))
    }

    switch (desiredStatus){
        case TicketStatus.OPEN: return (ticketId,description)=>stopTicket(ticketId,description)
        case TicketStatus.CLOSED: return (ticketId,description)=>closeTicket(ticketId,description)
        case TicketStatus.REOPENED: return (ticketId,description)=>reopenTicket(ticketId,description)
        case TicketStatus.RESOLVED: return (ticketId,description)=>resolveTicket(ticketId,description)
        case TicketStatus.IN_PROGRESS: return (ticketId,expertId,priorityLevel,description)=>startTicket(ticketId,expertId,priorityLevel,description)
    }
}
export default CustomModal;
