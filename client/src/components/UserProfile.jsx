import {Button, Col, Container, Row, Spinner} from "react-bootstrap";
import {UserRole} from "../enums/UserRole";
import {setAccessToken} from "../utils/utils";
import {useStatusAlert} from "../hooks/useStatusAlert";
import ManagerProfile from "./profiles/ManagerProfile";
import ExpertProfile from "./profiles/ExpertProfile";
import ClientProfile from "./profiles/ClientProfile";
import {useState} from "react";
import {useNavigate} from "react-router-dom";

function UserProfile({userInfo}) {
    const navigate = useNavigate()
    const {StatusAlertComponent, showSuccess, showError, resetStatusAlert} = useStatusAlert();
    const [hideLogout, setHideLogout] = useState(false);

    const switchLogoutVisibility = () => {
        setHideLogout(hideLogout => !hideLogout)
    }

    const renderProfile = (userRole) => {
        switch (userRole) {
            case UserRole.MANAGER:
                return <ManagerProfile userInfo={userInfo}/>;
            case UserRole.EXPERT:
                return <ExpertProfile expertId={userInfo.id} showError={showError}/>;
            case UserRole.CLIENT:
                return <ClientProfile clientId={userInfo.id} showSuccess={showSuccess} showError={showError}
                                      resetStatusAlert={resetStatusAlert}
                                      switchLogoutVisibility={switchLogoutVisibility}/>;
            default:
                return null;
        }
    }

    const handleLogout = () => {
        setAccessToken(null);
        navigate("/login");
    };

    return (
        <Container className="h-100">
            <Row className="h-100">
                <Col className="d-flex flex-column align-items-center justify-content-center">
                    {
                        userInfo
                            ? (
                                <>
                                    <StatusAlertComponent/>
                                    {renderProfile(userInfo.role)}
                                    {!hideLogout && <Button variant="danger" onClick={handleLogout}>Logout</Button>}
                                </>
                            )
                            : <Spinner animation="border" variant="primary"/>
                    }
                </Col>
            </Row>
        </Container>
    );
}

export {UserProfile};
