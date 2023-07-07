import React, {useContext, useState} from "react";
import API from "../../../API";
import {Button, Form, Modal, Row} from "react-bootstrap";
import {CustomModalContext} from "../CustomModal";
import {handleApiError} from "../../../utils/utils";

function RegisterProductModalBody() {

    const {handleClose, completingAction, showError} = useContext(CustomModalContext)

    const [validated, setValidated] = useState(false);
    const [uuid, setUuid] = useState('');

    const registerProduct = () => {
        API.registerProduct(uuid)
            .then(() => {
                    completingAction()
                }
            )
            .catch(err => handleApiError(err,showError))
    }

    const handleSubmit = (event) => {
        const form = event.currentTarget;
        event.preventDefault();
        if (form.checkValidity() === false) {
            event.stopPropagation();
        } else {
            registerProduct()
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

export {RegisterProductModalBody};
