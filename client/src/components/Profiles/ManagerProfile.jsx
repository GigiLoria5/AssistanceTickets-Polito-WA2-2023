import {Col, Container, Row} from "react-bootstrap";

function ManagerProfile({userInfo}) {
    return (
        <Container className="my-5">
            <Row className="justify-content-center">
                <Col md={8} className="text-center">
                    <h1 className="mb-4">Manager Profile</h1>
                    <div className="p-3">
                        <p className="mb-2"><b>Name:</b> {userInfo.name}</p>
                        <p className="mb-2"><b>Email:</b> {userInfo.email}</p>
                    </div>
                </Col>
            </Row>
        </Container>
    );
}

export default ManagerProfile;
