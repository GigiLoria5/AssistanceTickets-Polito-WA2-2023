import {useContext, useState} from "react";
import Experts from "../../Experts";
import {Button, Col, Form, Modal, Row} from "react-bootstrap";
import {CustomModalContext} from "../CustomModal";
import {TicketPriority} from "../../../enums/TicketPriority";
import {TicketStatus} from "../../../enums/TicketStatus";
import {getUpdateStatusApiCall} from "../../../utils/modalUtil";


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

    const {objectId, handleClose, completingAction, showError} = useContext(CustomModalContext)

    const [validated, setValidated] = useState(false);

    const changeStatus = () => {
        const apiCall = getUpdateStatusApiCall(TicketStatus.IN_PROGRESS)
        apiCall(objectId, selectedExpert.expertId, ticketPriority, description)
            .then(() => {
                    completingAction()
                }
            ).catch(err => showError(err.error))
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

export {StatusChangeInProgressModalBody};
