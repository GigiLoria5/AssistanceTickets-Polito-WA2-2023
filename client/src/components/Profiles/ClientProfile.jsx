import {useEffect, useState} from "react";
import API from "../../API";
import {handleApiError} from "../../utils/utils";
import {Col, Container, Row, Spinner} from "react-bootstrap";

function ClientProfile({clientEmail, showError}) {
    const [client, setClient] = useState(null);

    useEffect(() => {
        API.getProfileByEmail(clientEmail)
            .then(client => {
                setClient(client)
            })
            .catch(err => handleApiError(err, showError))
    }, [])

    return (
        <Container className="my-5">
            <Row className="justify-content-center">
                <Col md={8} className="text-center">
                    <h1 className="mb-4">Client Profile</h1>
                    {
                        !client
                            ? <Spinner animation="border" variant="primary"/>
                            : (
                                <div className="p-3">
                                    <p className="mb-2"><b>Name:</b> {client.name} {client.surname}</p>
                                    <p className="mb-2"><b>Email:</b> {client.email}</p>
                                    <p className="mb-2"><b>Phone Number:</b> {client.phoneNumber}</p>
                                    <p>
                                        <b>Location:</b> {client.address}, {client.city}, {client.country}
                                    </p>
                                </div>
                            )
                    }
                </Col>
            </Row>
        </Container>
    );
}

export default ClientProfile;
