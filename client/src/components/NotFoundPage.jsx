import React from 'react';
import {Link} from 'react-router-dom';
import {Button, Col, Container, Row} from 'react-bootstrap';

function NotFoundPage() {
    return (
        <Container>
            <Row className="justify-content-center">
                <Col md={8} className="text-center">
                    <h1 className="my-5">Oops! Page not found.</h1>
                    <p className="mb-5">The page you are looking for might have been removed, had its name changed, or
                        is temporarily unavailable.</p>
                    <Link to="/">
                        <Button variant="primary">Return to homepage</Button>
                    </Link>
                </Col>
            </Row>
        </Container>
    );
}

export default NotFoundPage;
