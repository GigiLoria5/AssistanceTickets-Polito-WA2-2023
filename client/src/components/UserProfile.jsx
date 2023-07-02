import {Button, Col, Container, Row, Spinner} from "react-bootstrap";
import {UserRole} from "../../enums/UserRole";
import {setAccessToken} from "../utils/utils";
import {useStatusAlert} from "../../hooks/useStatusAlert";
import ManagerProfile from "./Profiles/ManagerProfile";
import ExpertProfile from "./Profiles/ExpertProfile";
import ClientProfile from "./Profiles/ClientProfile";

function UserProfile({userInfo, setUserInfo}) {
    const {StatusAlertComponent, showError} = useStatusAlert();

    const renderProfile = (userRole) => {
        switch (userRole) {
            case UserRole.MANAGER:
                return <ManagerProfile userInfo={userInfo}/>;
            case UserRole.EXPERT:
                return <ExpertProfile expertId={userInfo.id} showError={showError}/>;
            case UserRole.CLIENT:
                return <ClientProfile clientEmail={userInfo.email} showError={showError}/>;
            default:
                return null;
        }
    }

    const handleLogout = () => {
        setUserInfo(null);
        setAccessToken(null);
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
                                    <Button variant="danger" onClick={handleLogout}>Logout</Button>{' '}
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
