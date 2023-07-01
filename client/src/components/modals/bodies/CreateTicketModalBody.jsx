import React, {useContext, useState} from "react";
import API from "../../../API";
import {Button, Form, Modal, Row} from "react-bootstrap";
import {CustomModalContext} from "../CustomModal";

function CreateTicketModalBody() {

    const {objectId, handleClose, completingAction, showError} = useContext(CustomModalContext)

    const [validated, setValidated] = useState(false);
    const [title, setTitle] = useState('');
    const [description, setDescription] = useState('');

    const createTicket = () => {
        API.createTicket(objectId, title, description)
            .then(() => {
                    completingAction()
                }
            )
            .catch(err => showError(err.error))
    }

    const handleSubmit = (event) => {
        const form = event.currentTarget;
        event.preventDefault();
        if (form.checkValidity() === false) {
            event.stopPropagation();
        } else {
            createTicket()
        }
        setValidated(true);
    };

    return (
        <>
            <Modal.Body>
                <Form noValidate validated={validated} onSubmit={handleSubmit}>
                    <Row className="mb-3">
                        <Form.Group controlId="validationCustom">
                            <Form.Label>Title</Form.Label>
                            <Form.Control
                                required={true}
                                type="text"
                                value={title}
                                onChange={ev => setTitle(ev.target.value)}
                                placeholder="Insert title"
                            />
                            <Form.Control.Feedback type="invalid">
                                Please provide a title
                            </Form.Control.Feedback> </Form.Group>
                    </Row>
                    <Row className="mb-3">
                        <Form.Group controlId="validationCustom">
                            <Form.Label> "Description"</Form.Label>
                            <Form.Control
                                required={true}
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
                            Create
                        </Button>
                    </Modal.Footer>
                </Form>
            </Modal.Body>
        </>
    )
}

export {CreateTicketModalBody};
