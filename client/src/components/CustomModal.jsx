import {useStatusAlert} from "../../hooks/useStatusAlert";
import {Button, Col, Form, Modal, Row, Spinner} from "react-bootstrap";
import {getTaskToAchieveStatus} from "../utils/ticketUtil";
import React, {useEffect, useState} from "react";
import {ModalType} from "../../enums/ModalType";
import {TicketStatus} from "../../enums/TicketStatus";
import {TicketPriority} from "../../enums/TicketPriority";
import API from "../API";
import Experts from "./Experts";

function CustomModal({show, hide, backdrop, keyboard, type, desiredState, ticketId}) {

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
                                 resetStatusAlert={resetStatusAlert}
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

function CustomModalInterior({type, desiredState, ticketId, handleClose, resetStatusAlert, showError}) {
    const getModalInterior = () => {
        switch (type) {
            case ModalType.STATUS_CHANGE:
                if (desiredState === TicketStatus.IN_PROGRESS)
                    return <StatusChangeOpenModal handleClose={handleClose} resetStatusAlert={resetStatusAlert}
                                                  showError={showError}/>
                else
                    return <StatusChangeStandardModal desiredState={desiredState} handleClose={handleClose}
                                                      showError={showError}/>

            case ModalType.CREATE:
                return <TicketCreationModal ticketId={ticketId} handleClose={handleClose} showError={showError}/>
        }
    }

    return getModalInterior()
}

function StatusChangeOpenModal({handleClose, resetStatusAlert, showError}) {

    const [validated, setValidated] = useState(false);
    const [ticketPriority, setTicketPriority] = useState(null);
    const [description, setDescription] = useState('');
    const [expertsData, setExpertsData] = useState(null);
    const [selectedExpert, setSelectedExpert] = useState(null);
    const [loading, setLoading] = useState(true)
    const [errorPresence, setErrorPresence] = useState(false)

    useEffect(() => {
            getAllExperts()
        }, []
    );

    const getAllExperts = () => {
        API.getAllExperts()
            .then((x) => {
                    setExpertsData(x)
                    setLoading(false)
                    resetStatusAlert()
                }
            )
            .catch(err => stopAnimationAndShowError(err.error))
    }
    const stopAnimationAndShowError = (err) => {
        setLoading(false)
        setErrorPresence(true)
        showError(err.error)
    }

    const handleSubmit = (event) => {
        const form = event.currentTarget;
        event.preventDefault();
        if (form.checkValidity() === false) {
            event.stopPropagation();
        } else {
            //QUI METTI IL CODICE SE TUTTO VA BENE
            showError(`ciao ${ticketPriority}`)
        }
        setValidated(true);

    };

    return (
        loading ?
            <Spinner animation="border" variant="primary"/>
            :
            !errorPresence ?
                <Modal.Body>
                    {selectedExpert === null ?
                        <>
                            <h4>
                                Select expert
                            </h4>
                            <Row>
                                <Col>
                                    <Experts experts={expertsData} actionName={"Select"}
                                             action={setSelectedExpert}/>
                                </Col>
                            </Row>
                        </> :
                        <>
                            <Row className="mb-3"  md={4}>
                                <Col>
                                    <Row>
                                        <Col>
                                            Selected expert:
                                        </Col>
                                    </Row>
                                    <Row>
                                        <Col>
                                            {selectedExpert.name} {selectedExpert.surname} ( {selectedExpert.email} )
                                        </Col>
                                    </Row>
                                </Col>
                                <Col>
                                    <Button onClick={() => setSelectedExpert(null)}>
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
                                                    inline
                                                    required
                                                    checked={ticketPriority===TicketPriority.LOW}
                                                    name="group1"
                                                    type="radio"
                                                    id={`inline-radio-1`}
                                                    onClick={() => setTicketPriority(TicketPriority.LOW)}
                                                />
                                                <Form.Check.Label className="mx-2">
                                                    {TicketPriority.LOW}
                                                </Form.Check.Label>
                                                <Form.Check.Input
                                                    inline
                                                    required
                                                    checked={ticketPriority===TicketPriority.MEDIUM}

                                                    name="group1"
                                                    type="radio"
                                                    id={`inline-radio-2`}
                                                    onClick={() => setTicketPriority(TicketPriority.MEDIUM)}
                                                />
                                                <Form.Check.Label className="mx-2">
                                                    {TicketPriority.MEDIUM}
                                                </Form.Check.Label>
                                                <Form.Check.Input
                                                    inline
                                                    required
                                                    checked={ticketPriority===TicketPriority.HIGH}

                                                    name="group1"
                                                    type="radio"
                                                    id={`inline-radio-3`}
                                                    onClick={() => setTicketPriority(TicketPriority.HIGH)}
                                                />
                                                <Form.Check.Label className="mx-2">
                                                    {TicketPriority.HIGH}
                                                </Form.Check.Label>
                                                <Form.Check.Input
                                                    inline
                                                    required
                                                    checked={ticketPriority===TicketPriority.CRITICAL}

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
                    }
                </Modal.Body>
                :
                null

    )
}

function StatusChangeStandardModal({desiredState, handleClose, showError}) {

    const requiredDesc = desiredState === TicketStatus.REOPENED

    const [validated, setValidated] = useState(false);
    const [description, setDescription] = useState('');

    const handleSubmit = (event) => {
        const form = event.currentTarget;
        event.preventDefault();
        if (form.checkValidity() === false) {
            event.stopPropagation();
        } else {
            //QUI METTI IL CODICE SE TUTTO VA BENE
            showError("ciao")
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
            <Modal.Body>
                <Form>
                    <Form.Group
                        className="mb-3"
                    >
                        <div>TODO DESCRIZIONE OBBLIGATORIA SE REOPEN</div>
                        <Form.Label>Optional description</Form.Label>
                        <Form.Control as="textarea" rows={3}/>
                    </Form.Group>
                </Form>
            </Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={() => handleClose()}>
                    Cancel
                </Button>
                <Button variant="primary" onClick={() => showError("ciao")}>
                    Confirm status change
                </Button>
            </Modal.Footer>
        </>

    )
}

export default CustomModal;
