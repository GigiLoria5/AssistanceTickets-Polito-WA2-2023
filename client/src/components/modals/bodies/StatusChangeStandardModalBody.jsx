import React, {useContext, useState} from "react";
import {TicketStatus} from "../../../../enums/TicketStatus";
import {Button, Form, Modal, Row} from "react-bootstrap";
import {CustomModalContext} from "../CustomModal";
import {getUpdateStatusApiCall} from "../../../utils/modalUtil";
import {handleApiError} from "../../../utils/utils";

function StatusChangeStandardModalBody() {

    const {objectId, desiredState, handleClose, completingAction, showError} = useContext(CustomModalContext)

    const requiredDesc = desiredState === TicketStatus.REOPENED
    const [validated, setValidated] = useState(false);
    const [description, setDescription] = useState('');

    const changeStatus = () => {
        const apiCall = getUpdateStatusApiCall(desiredState)
        apiCall(objectId, description)
            .then(() => {
                    completingAction()
                })
            .catch(err => handleApiError(err,showError))
    }

    const handleSubmit = (event) => {
        const form = event.currentTarget;
        event.preventDefault();
        if (form.checkValidity() === false) {
            event.stopPropagation();
        } else {
            changeStatus()
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

export {StatusChangeStandardModalBody};
