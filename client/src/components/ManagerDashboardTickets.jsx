import { useEffect, useState } from "react";
import {useStatusAlert} from "../../hooks/useStatusAlert";
import API from "../API";
import {Button, Col, Container, Form, Modal, Row, Spinner, Table} from "react-bootstrap";
import { handleApiError } from "../utils/utils";
import Tickets from "./Tickets";

function ManagerDashboardTickets() {
    const [tickets, setTickets] = useState(null);
    const [refreshTickets, setRefreshTickets] = useState(true)
    const { StatusAlertComponent, showError, resetStatusAlert } = useStatusAlert();

    useEffect(() => {
        if (refreshTickets)
            API.getAllTickets()
                .then((response) => {
                    setTickets(response)
                    resetStatusAlert()
                })
                .catch(err => handleApiError(err, showError))
                .finally(_ => {
                    setRefreshTickets(false)
                })
    }, [refreshTickets])

    return (
        <Container className="h-100">
            <Row className="h-100">
                <Col className="d-flex flex-column align-items-center justify-content-center">
                    <h2>Tickets Management</h2><StatusAlertComponent/>
                    {
                        tickets || !refreshTickets ?
                            <>
                                <Tickets tickets={tickets}/>
                            </>
                            : <Spinner animation="border" variant="primary"/>
                    }
                </Col>
            </Row>
        </Container>
    );
}

export default ManagerDashboardTickets;
