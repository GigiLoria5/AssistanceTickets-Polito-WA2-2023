import {Alert, Col, Row} from "react-bootstrap";
import React from "react";

const StatusAlert = ({error, resetError}) => {
    return (
        <Row className="mt-3">
            <Col>
                <Alert variant={error === "Success" ? "success" : "danger"} dismissible onClose={resetError}
                       className="roundedError">
                    <Alert.Heading>{error}</Alert.Heading>
                </Alert>
            </Col>
        </Row>
    )
}

export default StatusAlert
