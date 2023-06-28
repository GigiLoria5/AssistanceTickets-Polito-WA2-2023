import {Alert, Col, Row} from "react-bootstrap";
import React from "react";
import {AlertType} from "../../enums/AlertType";

const StatusAlert = ({type, message, resetMessage}) => {
    return (
        <Row className="mt-3">
            <Col>
                <Alert variant={type === AlertType.SUCCESS ? "success" : "danger"} dismissible onClose={resetMessage}
                       className="roundedError">
                    <Alert.Heading>{message}</Alert.Heading>
                </Alert>
            </Col>
        </Row>
    )
}

export default StatusAlert
