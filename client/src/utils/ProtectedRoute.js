import {useEffect, useState} from 'react';
import {Navigate, Outlet} from 'react-router-dom';
import API from '../API';
import {HttpStatusCode} from "../enums/HttpStatusCode";
import {Col, Container, Row, Spinner} from "react-bootstrap";
import {setAccessToken} from "./utils";

/**
 *
 * @param {User} userInfo user info state default to null
 * @param {Function} setUserInfo to update user state
 * @param {Array<String>} rolesAllowed array containing all types of users who can access the route
 * @returns protected route if access is possible, otherwise redirect to login page
 */
const ProtectedRoute = ({userInfo, setUserInfo, rolesAllowed}) => {
    const [sessionReload, setSessionReload] = useState(true);

    useEffect(() => {
        API.getUserInfo()
            .then(user => {
                setUserInfo(user);
            })
            .catch(error => {
                if (error.status === HttpStatusCode.UNAUTHORIZED) {
                    setUserInfo(null);
                    setAccessToken(null);
                }
            })
            .finally(_ => {
                setSessionReload(false)
            })
    }, []);

    if (sessionReload)
        return (
            <Container className="vh-100">
                <Row className="h-100">
                    <Col className="d-flex align-items-center justify-content-center">
                        <Spinner animation="border" variant="primary"/>
                    </Col>
                </Row>
            </Container>
        );
    else {
        if (!userInfo) {
            setUserInfo(null);
            setAccessToken(null);
            return <Navigate to="/login"/>;
        }
        if (userInfo && !rolesAllowed.includes(userInfo.role)) {
            return <Navigate to="/"/>;
        }
        return <Outlet/>;
    }
};

export default ProtectedRoute;
